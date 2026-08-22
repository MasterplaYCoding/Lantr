package app.lantr.lantr.Util

import android.content.Context
import android.net.Uri
import app.lantr.lantr.Models.Arc
import app.lantr.lantr.Models.Audience
import app.lantr.lantr.Models.BlockType
import app.lantr.lantr.Models.ContentAlignment
import app.lantr.lantr.Models.ContentArrangement
import app.lantr.lantr.Models.ContentBlock
import app.lantr.lantr.Models.ImageBlock
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Models.PresentationDuration
import app.lantr.lantr.Models.PresentationPurpose
import app.lantr.lantr.Models.PredefinedBlockType
import app.lantr.lantr.Models.Rhetoric
import app.lantr.lantr.Models.RhetoricValue
import app.lantr.lantr.Models.Slide
import app.lantr.lantr.Models.StoryBlock
import app.lantr.lantr.Models.StoryBlockCategory
import app.lantr.lantr.Models.StoryBlockType
import app.lantr.lantr.Models.StorySection
import app.lantr.lantr.Models.StorySectionType
import app.lantr.lantr.Models.TextBlock
import app.lantr.lantr.Models.Themes.FontWeight
import app.lantr.lantr.Models.Themes.RGBColor
import app.lantr.lantr.Models.Themes.TextStyle
import app.lantr.lantr.Models.Themes.Theme
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

@OptIn(ExperimentalSerializationApi::class)
object LtrnArchiveService {
    private const val MAX_ARCHIVE_ENTRIES = 2_048
    private const val MAX_ARCHIVE_BYTES = 256L * 1024L * 1024L
    private const val MAX_JSON_ENTRY_BYTES = 2 * 1024 * 1024
    private const val MAX_IMAGE_ENTRY_BYTES = 64 * 1024 * 1024

    private val json = Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        ignoreUnknownKeys = true
    }

    fun loadPresentation(context: Context, uri: Uri): Presentation {
        val archiveFile = copyUriToCache(context, uri, "import")
        return loadPresentationFromFile(archiveFile)
    }

    fun savePresentation(context: Context, uri: Uri, presentation: Presentation) {
        val tempFile = File.createTempFile("lantr-export-", ".ltrn", context.cacheDir)
        savePresentationToFile(tempFile, presentation)
        context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
            tempFile.inputStream().use { input -> input.copyTo(output) }
        }
        tempFile.delete()
    }

    private fun copyUriToCache(context: Context, uri: Uri, prefix: String): File {
        val tempFile = File.createTempFile("lantr-$prefix-", ".ltrn", context.cacheDir)
        context.contentResolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        } ?: error("Could not open input stream for $uri")
        return tempFile
    }

    private fun loadPresentationFromFile(file: File): Presentation {
        ZipFile(file).use { archive ->
            validateArchive(archive)
            val metadata = readJsonObject(archive, "metadata.json")
            val presentationId = metadata.string("id") ?: UUID.randomUUID().toString()
            val imagesDirectory = extractImages(archive, file.nameWithoutExtension, file.parentFile ?: file.parentFile)
            val slidesById = loadSlides(archive, imagesDirectory)
            val blocksById = loadBlocks(archive, slidesById)
            val orderedBlocks = loadOrderedBlocks(archive, blocksById)

            return buildPresentation(
                metadata = metadata,
                themeObject = readJsonObjectOrNull(archive, "theme.json"),
                orderedBlocks = orderedBlocks
            ).also { presentation ->
                registerPresentationImages(
                    presentationId = presentation.id.toString().ifBlank { presentationId },
                    imagePaths = collectSlides(presentation)
                        .flatMap { slide -> slide.blocks.filterIsInstance<ImageBlock>().map(ImageBlock::imagePath) }
                )
            }
        }
    }

    private fun savePresentationToFile(file: File, presentation: Presentation) {
        ZipOutputStream(file.outputStream().buffered()).use { output ->
            val imageNames = collectImageNames(presentation)

            writeJsonEntry(output, "manifest.json", buildManifestObject(presentation, imageNames.size))
            writeJsonEntry(output, "metadata.json", buildMetadataObject(presentation))
            writeJsonEntry(output, "theme.json", buildThemeObject(presentation.theme))
            writeJsonEntry(output, "arc/blocks.json", buildBlocksIndexObject(presentation))

            flattenBlocks(presentation.arc).forEachIndexed { index, block ->
                writeJsonEntry(
                    output,
                    "arc/blocks/block_${(index + 1).toString().padStart(3, '0')}.json",
                    buildBlockObject(block)
                )
            }

            collectSlides(presentation).forEachIndexed { index, slide ->
                writeJsonEntry(
                    output,
                    "slides/slide_${(index + 1).toString().padStart(3, '0')}.json",
                    buildSlideObject(slide, imageNames)
                )
            }

            imageNames.forEach { (imagePath, archiveName) ->
                val imageFile = File(imagePath)
                if (imageFile.exists() && imageFile.isFile) {
                    writeBinaryEntry(output, "images/$archiveName", imageFile.readBytes())
                }
            }
        }
    }

    private fun buildPresentation(
        metadata: JsonObject,
        themeObject: JsonObject?,
        orderedBlocks: List<StoryBlock>
    ): Presentation {
        return Presentation(
            id = metadata.string("id")?.let(::safeUuid) ?: UUID.randomUUID(),
            title = metadata.string("title") ?: "Imported Presentation",
            author = metadata.string("author") ?: "Unknown Author",
            description = metadata.string("description").orEmpty(),
            duration = metadata.string("duration")?.let(::parseDuration) ?: PresentationDuration.FIFTEEN_MINUTES,
            audience = parseAudience(metadata["audience"]?.jsonObject),
            purpose = parsePurpose(metadata["purpose"]?.jsonArray).toMutableList(),
            createdAt = metadata.string("created_at")?.let(::parseDate) ?: Date(),
            modifiedAt = metadata.string("modified_at")?.let(::parseDate) ?: Date(),
            theme = themeObject?.let(::parseTheme) ?: Theme.standard,
            arc = Arc(sections = orderedBlocks.groupIntoSections())
        )
    }

    private fun List<StoryBlock>.groupIntoSections(): List<StorySection> {
        if (isEmpty()) return emptyList()
        val sections = mutableListOf<StorySection>()
        var currentType: StorySectionType? = null
        val currentBlocks = mutableListOf<StoryBlock>()

        fun flush() {
            val type = currentType ?: return
            sections += StorySection(type = type, blocks = currentBlocks.toList())
            currentBlocks.clear()
        }

        for (block in this) {
            val nextType = block.category.toSectionType()
            if (currentType == null) {
                currentType = nextType
            } else if (currentType != nextType) {
                flush()
                currentType = nextType
            }
            currentBlocks += block
        }

        flush()
        return sections
    }

    private fun loadOrderedBlocks(archive: ZipFile, blocksById: Map<UUID, StoryBlock>): List<StoryBlock> {
        val orderedIds = readJsonObject(archive, "arc/blocks.json")["block_ids"]
            ?.jsonArray
            ?.mapNotNull { it.jsonPrimitive.contentOrNull?.let(::safeUuid) }
            .orEmpty()
        return if (orderedIds.isNotEmpty()) orderedIds.mapNotNull(blocksById::get) else blocksById.values.toList()
    }

    private fun loadBlocks(archive: ZipFile, slidesById: Map<String, Slide>): Map<UUID, StoryBlock> {
        return archive.entries().asSequence()
            .filter { !it.isDirectory && it.name.startsWith("arc/blocks/") && it.name.endsWith(".json") }
            .mapNotNull { entry ->
                parseStoryBlock(readJsonObject(archive, entry), slidesById)?.let { it.id to it }
            }
            .toMap()
    }

    private fun loadSlides(archive: ZipFile, imagesDirectory: File): Map<String, Slide> {
        return archive.entries().asSequence()
            .filter { !it.isDirectory && it.name.startsWith("slides/") && it.name.endsWith(".json") }
            .mapNotNull { entry ->
                parseSlide(readJsonObject(archive, entry), imagesDirectory)?.let { it.id to it }
            }
            .toMap()
    }

    private fun extractImages(archive: ZipFile, seedName: String, baseDir: File?): File {
        val tempDirectory = File(baseDir ?: File("."), "lantr-images-$seedName-${System.currentTimeMillis()}").apply {
            mkdirs()
            deleteOnExit()
        }

        archive.entries().asSequence()
            .filter { !it.isDirectory && it.name.startsWith("images/") }
            .forEach { entry ->
                val targetFile = File(tempDirectory, entry.name.substringAfterLast('/'))
                targetFile.writeBytes(readEntryBytes(archive, entry, MAX_IMAGE_ENTRY_BYTES))
                targetFile.deleteOnExit()
            }

        return tempDirectory
    }

    private fun parseSlide(slideObject: JsonObject, imagesDirectory: File): Slide? {
        val slideId = slideObject.string("id") ?: return null
        val blocks = slideObject["blocks"]?.jsonArray?.flatMap { parseSlideBlock(it.jsonObject, imagesDirectory) }.orEmpty()
        return Slide(
            id = slideId,
            contentArrangement = slideObject.string("content_arrangement")?.let(::parseContentArrangement) ?: ContentArrangement.top,
            overrideColor = slideObject["override_color"]?.jsonObject?.let(::parseColor),
            usesAltScheme = slideObject.boolean("uses_alt_scheme") ?: false,
            blocks = blocks
        )
    }

    private fun parseSlideBlock(blockObject: JsonObject, imagesDirectory: File): List<ContentBlock> {
        return when (blockObject.string("block_type")) {
            "text" -> listOf(parseTextBlock(blockObject))
            "image" -> listOf(parseImageBlock(blockObject, imagesDirectory))
            "columns" -> blockObject["columns"]?.jsonArray?.flatMap { column ->
                column.jsonArray.flatMap { nested -> parseSlideBlock(nested.jsonObject, imagesDirectory) }
            }.orEmpty()
            else -> emptyList()
        }
    }

    private fun parseTextBlock(blockObject: JsonObject): TextBlock {
        return TextBlock(
            id = blockObject.string("id")?.let(::safeUuid) ?: UUID.randomUUID(),
            type = blockObject.string("type")?.let(::parseBlockType) ?: BlockType.body,
            contentAlignment = blockObject.string("content_alignment")?.let(::parseContentAlignment) ?: ContentAlignment.leading,
            content = blockObject.string("content").orEmpty(),
            overrideColor = blockObject["override_color"]?.jsonObject?.let(::parseColor)
        )
    }

    private fun parseImageBlock(blockObject: JsonObject, imagesDirectory: File): ImageBlock {
        val relativePath = blockObject.string("image_path").orEmpty()
        return ImageBlock(
            id = blockObject.string("id")?.let(::safeUuid) ?: UUID.randomUUID(),
            contentAlignment = blockObject.string("content_alignment")?.let(::parseContentAlignment) ?: ContentAlignment.center,
            imagePath = if (relativePath.isBlank()) "" else File(imagesDirectory, relativePath).absolutePath,
            imageHeight = blockObject.float("image_height") ?: 320f,
            cornerRadius = blockObject.float("corner_radius") ?: 0f
        )
    }

    private fun parseStoryBlock(blockObject: JsonObject, slidesById: Map<String, Slide>): StoryBlock? {
        val blockId = blockObject.string("id")?.let(::safeUuid) ?: return null
        val name = blockObject.string("name").orEmpty()
        val category = blockObject.string("category")?.let(::parseStoryBlockCategory) ?: StoryBlockCategory.OTHER
        val rhetoric = blockObject["rhetoric"]?.jsonObject?.let(::parseRhetoric)
            ?: Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
        val slides = blockObject["slide_ids"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }?.mapNotNull(slidesById::get).orEmpty()
        val blockType = parseStoryBlockType(blockObject["block_type"]?.jsonObject, name)

        return when (blockType) {
            is StoryBlockType.Predefined -> StoryBlock.predefined(blockType.type, blockObject.string("main_idea").orEmpty(), slides).copy(
                id = blockId,
                name = name.ifBlank { blockType.type.displayName },
                description = blockObject.string("description").orEmpty().ifBlank { blockType.type.description },
                rhetoric = rhetoric,
                category = category
            )
            StoryBlockType.Custom -> StoryBlock.custom(
                name = name.ifBlank { "Custom Block" },
                description = blockObject.string("description").orEmpty(),
                mainIdea = blockObject.string("main_idea").orEmpty(),
                rhetoric = rhetoric,
                category = category,
                slides = slides
            ).copy(id = blockId)
        }
    }

    private fun parseStoryBlockType(blockTypeObject: JsonObject?, blockName: String): StoryBlockType {
        val predefinedName = blockTypeObject?.string("predefined_type") ?: blockName
        val predefinedType = parsePredefinedBlockType(predefinedName)
        return if (blockTypeObject?.string("type") == "predefined" && predefinedType != null) {
            StoryBlockType.Predefined(predefinedType)
        } else {
            StoryBlockType.Custom
        }
    }

    private fun parseTheme(themeObject: JsonObject): Theme {
        val textStyles = parseTextStyles(themeObject["textStyles"])
        return Theme(
            contentMargins = themeObject.float("contentMargins") ?: Theme.standard.contentMargins,
            elementSpacing = themeObject.float("elementSpacing") ?: Theme.standard.elementSpacing,
            altBackgroundColor = themeObject["altBackgroundColor"]?.jsonObject?.let(::parseColor) ?: Theme.standard.altBackgroundColor,
            backgroundColor = themeObject["backgroundColor"]?.jsonObject?.let(::parseColor) ?: Theme.standard.backgroundColor,
            textStyles = if (textStyles.isEmpty()) Theme.standard.textStyles else textStyles
        )
    }

    private fun parseTextStyles(element: JsonElement?): Map<BlockType, TextStyle> {
        val array = element as? JsonArray ?: return emptyMap()
        val styles = mutableMapOf<BlockType, TextStyle>()
        array.chunked(2).forEach { pair ->
            if (pair.size != 2) return@forEach
            val blockType = pair[0].jsonPrimitive.contentOrNull?.let(::parseBlockType) ?: return@forEach
            val styleObject = pair[1].jsonObject
            val fallback = Theme.standard.textStyles[blockType]
            styles[blockType] = TextStyle(
                fontSize = styleObject.float("fontSize") ?: fallback?.fontSize ?: 38f,
                fontWeight = parseFontWeight(styleObject["fontWeight"]?.jsonObject) ?: fallback?.fontWeight ?: FontWeight.regular,
                fontFamily = styleObject.string("fontFamily"),
                textColor = styleObject["textColor"]?.jsonObject?.let(::parseColor) ?: fallback?.textColor ?: RGBColor("000000"),
                altTextColor = styleObject["altTextColor"]?.jsonObject?.let(::parseColor) ?: fallback?.altTextColor ?: RGBColor("FFFFFF")
            )
        }
        return styles
    }

    private fun parseAudience(audienceObject: JsonObject?): Audience {
        audienceObject ?: return Audience()
        return Audience(
            groupSize = audienceObject.string("group_size")?.let(::parseGroupSize) ?: Audience.GroupSize.UNKNOWN,
            mainRole = audienceObject.string("main_role")?.let(::parseMainRole) ?: Audience.MainRole.MIXED_OTHER,
            ageGroup = audienceObject.string("age_group")?.let(::parseAgeGroup) ?: Audience.AgeGroup._25_TO_34,
            shortDescription = audienceObject.string("short_description").orEmpty(),
            knowledgeLevel = audienceObject.string("knowledge_level")?.let(::parseKnowledgeLevel) ?: Audience.KnowledgeLevel.MIXED,
            opinionOnTopic = audienceObject.string("opinion_on_topic").orEmpty(),
            mainMotivation = audienceObject.string("main_motivation")?.let(::parseMainMotivation) ?: Audience.MainMotivation.OTHER,
            goalOrPain = audienceObject.string("goal_or_pain").orEmpty()
        )
    }

    private fun parsePurpose(element: JsonElement?): List<PresentationPurpose> {
        return (element as? JsonArray)?.mapNotNull { item ->
            item.jsonPrimitive.contentOrNull?.let { raw ->
                PresentationPurpose.entries.firstOrNull { it.value.equals(raw, ignoreCase = true) }
            }
        }.orEmpty()
    }

    private fun parseRhetoric(rhetoricObject: JsonObject): Rhetoric {
        return Rhetoric(
            ethos = rhetoricObject.string("ethos")?.let(::parseRhetoricValue) ?: RhetoricValue.MIDDLE,
            logos = rhetoricObject.string("logos")?.let(::parseRhetoricValue) ?: RhetoricValue.MIDDLE,
            pathos = rhetoricObject.string("pathos")?.let(::parseRhetoricValue) ?: RhetoricValue.MIDDLE
        )
    }

    private fun buildManifestObject(presentation: Presentation, imageCount: Int): JsonObject = buildJsonObject {
        put("compression", "zip_deflate")
        put("created_with", "Lantr")
        put("format_version", "1.3.0")
        putJsonObject("file_count") {
            put("blocks", flattenBlocks(presentation.arc).size)
            put("images", imageCount)
            put("slides", collectSlides(presentation).size)
        }
    }

    private fun buildMetadataObject(presentation: Presentation): JsonObject = buildJsonObject {
        put("id", presentation.id.toString())
        put("title", presentation.title)
        put("author", presentation.author)
        put("description", presentation.description)
        put("duration", presentation.duration.value)
        put("created_at", formatDate(presentation.createdAt))
        put("modified_at", formatDate(Date()))
        putJsonObject("audience") {
            put("group_size", presentation.audience.groupSize.value)
            put("main_role", presentation.audience.mainRole.value)
            put("age_group", presentation.audience.ageGroup.value)
            put("short_description", presentation.audience.shortDescription)
            put("knowledge_level", presentation.audience.knowledgeLevel.value)
            put("opinion_on_topic", presentation.audience.opinionOnTopic)
            put("main_motivation", presentation.audience.mainMotivation.value)
            put("goal_or_pain", presentation.audience.goalOrPain)
        }
        putJsonArray("purpose") {
            presentation.purpose.forEach { add(JsonPrimitive(it.value)) }
        }
    }

    private fun buildThemeObject(theme: Theme): JsonObject = buildJsonObject {
        put("contentMargins", theme.contentMargins)
        put("elementSpacing", theme.elementSpacing)
        put("altBackgroundColor", buildColorObject(theme.altBackgroundColor))
        put("backgroundColor", buildColorObject(theme.backgroundColor))
        put("textStyles", buildTextStylesArray(theme.textStyles))
    }

    private fun buildBlocksIndexObject(presentation: Presentation): JsonObject = buildJsonObject {
        putJsonArray("block_ids") {
            flattenBlocks(presentation.arc).forEach { add(JsonPrimitive(it.id.toString())) }
        }
    }

    private fun buildBlockObject(block: StoryBlock): JsonObject = buildJsonObject {
        put("id", block.id.toString())
        put("name", block.name)
        put("description", block.description)
        put("main_idea", block.mainIdea)
        put("category", block.category.toArchiveValue())
        putJsonObject("rhetoric") {
            put("ethos", block.rhetoric.ethos.name.lowercase(Locale.US))
            put("logos", block.rhetoric.logos.name.lowercase(Locale.US))
            put("pathos", block.rhetoric.pathos.name.lowercase(Locale.US))
        }
        put("block_type", buildBlockTypeObject(block))
        putJsonArray("slide_ids") {
            block.slides.forEach { add(JsonPrimitive(it.id)) }
        }
    }

    private fun buildBlockTypeObject(block: StoryBlock): JsonObject = buildJsonObject {
        when (val type = block.blockType) {
            is StoryBlockType.Predefined -> {
                put("type", "predefined")
                put("predefined_type", type.type.displayName)
            }
            StoryBlockType.Custom -> put("type", "custom")
        }
    }

    private fun buildSlideObject(slide: Slide, imageNames: Map<String, String>): JsonObject = buildJsonObject {
        put("id", slide.id)
        put("content_arrangement", slide.contentArrangement.name)
        put("uses_alt_scheme", slide.usesAltScheme)
        slide.overrideColor?.let { put("override_color", buildColorObject(it)) }
        put("blocks", buildSlideBlocksArray(slide.blocks, imageNames))
    }

    private fun buildSlideBlocksArray(blocks: List<ContentBlock>, imageNames: Map<String, String>): JsonArray = buildJsonArray {
        blocks.forEach { block ->
            when (block) {
                is TextBlock -> add(buildJsonObject {
                    put("block_type", "text")
                    put("id", block.id.toString())
                    put("type", block.type.name)
                    put("content_alignment", block.contentAlignment.name)
                    put("content", block.content)
                    block.overrideColor?.let { put("override_color", buildColorObject(it)) }
                })
                is ImageBlock -> add(buildJsonObject {
                    put("block_type", "image")
                    put("id", block.id.toString())
                    put("type", block.type.name)
                    put("content_alignment", block.contentAlignment.name)
                    put("image_height", block.imageHeight)
                    put("corner_radius", block.cornerRadius)
                    put("image_path", imageNames[block.imagePath].orEmpty())
                })
            }
        }
    }

    private fun buildColorObject(color: RGBColor): JsonObject = buildJsonObject {
        put("red", color.red)
        put("green", color.green)
        put("blue", color.blue)
    }

    private fun buildTextStylesArray(textStyles: Map<BlockType, TextStyle>): JsonArray = buildJsonArray {
        listOf(
            BlockType.body, BlockType.quote, BlockType.h3, BlockType.h1,
            BlockType.h2, BlockType.bulletList, BlockType.numberedList, BlockType.presenterNotes
        ).forEach { blockType ->
            val style = textStyles[blockType] ?: return@forEach
            add(JsonPrimitive(blockType.name))
            add(buildJsonObject {
                put("fontSize", style.fontSize)
                style.fontFamily?.let { put("fontFamily", it) }
                put("fontWeight", buildJsonObject { putJsonObject(style.fontWeight.name) {} })
                put("textColor", buildColorObject(style.textColor))
                put("altTextColor", buildColorObject(style.altTextColor))
            })
        }
    }

    private fun collectSlides(presentation: Presentation): List<Slide> = flattenBlocks(presentation.arc).flatMap { it.slides }
    private fun flattenBlocks(arc: Arc): List<StoryBlock> = arc.sections.flatMap { it.blocks }

    private fun collectImageNames(presentation: Presentation): Map<String, String> {
        val names = linkedMapOf<String, String>()
        collectSlides(presentation).forEach { slide ->
            slide.blocks.filterIsInstance<ImageBlock>().forEach { imageBlock ->
                val imagePath = imageBlock.imagePath
                if (imagePath.isBlank() || names.containsKey(imagePath)) return@forEach
                val imageFile = File(imagePath)
                if (!imageFile.exists() || !imageFile.isFile) return@forEach
                names[imagePath] = uniqueImageName(imageFile, names.values.toSet())
            }
        }
        return names
    }

    private fun uniqueImageName(imageFile: File, usedNames: Set<String>): String {
        val original = imageFile.name
        if (original !in usedNames) return original
        val base = imageFile.nameWithoutExtension
        val ext = imageFile.extension.takeIf { it.isNotBlank() }?.let { ".$it" }.orEmpty()
        var suffix = 1
        while (true) {
            val candidate = "${base}_$suffix$ext"
            if (candidate !in usedNames) return candidate
            suffix += 1
        }
    }

    private fun readJsonObject(archive: ZipFile, path: String): JsonObject {
        val entry = archive.getEntry(path) ?: error("Missing required archive entry: $path")
        return readJsonObject(archive, entry)
    }

    private fun readJsonObjectOrNull(archive: ZipFile, path: String): JsonObject? {
        val entry = archive.getEntry(path) ?: return null
        return readJsonObject(archive, entry)
    }

    private fun readJsonObject(archive: ZipFile, entry: ZipEntry): JsonObject {
        return json.parseToJsonElement(
            readEntryBytes(archive, entry, MAX_JSON_ENTRY_BYTES).decodeToString()
        ).jsonObject
    }

    private fun validateArchive(archive: ZipFile) {
        val entries = archive.entries().asSequence().toList()
        require(entries.size <= MAX_ARCHIVE_ENTRIES) { "Archive contains too many entries" }

        val declaredSize = entries.sumOf { entry -> entry.size.coerceAtLeast(0L) }
        require(declaredSize <= MAX_ARCHIVE_BYTES) { "Archive expands beyond the supported size" }

        entries.forEach { entry ->
            require(!entry.name.contains('\\')) { "Archive entry uses an invalid path separator" }
            require(entry.name.split('/').none { it == ".." }) { "Archive entry escapes its directory" }
        }
    }

    private fun readEntryBytes(archive: ZipFile, entry: ZipEntry, maxBytes: Int): ByteArray {
        require(entry.size < 0 || entry.size <= maxBytes) { "Archive entry is too large: ${entry.name}" }
        return archive.getInputStream(entry).use { input ->
            val output = ByteArrayOutputStream(minOf(maxBytes, 8 * 1024))
            val buffer = ByteArray(8 * 1024)
            var total = 0
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                total += count
                require(total <= maxBytes) { "Archive entry is too large: ${entry.name}" }
                output.write(buffer, 0, count)
            }
            output.toByteArray()
        }
    }

    private fun writeJsonEntry(output: ZipOutputStream, path: String, value: JsonObject) {
        writeBinaryEntry(output, path, json.encodeToString(JsonObject.serializer(), value).encodeToByteArray())
    }

    private fun writeBinaryEntry(output: ZipOutputStream, path: String, bytes: ByteArray) {
        output.putNextEntry(ZipEntry(path))
        output.write(bytes)
        output.closeEntry()
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull
    private fun JsonObject.float(key: String): Float? = this[key]?.jsonPrimitive?.doubleOrNull?.toFloat()
    private fun JsonObject.boolean(key: String): Boolean? = this[key]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull()

    private fun archiveDateFormat(): SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

    private fun formatDate(value: Date): String = archiveDateFormat().format(value)

    private fun parseDate(value: String): Date = archiveDateFormat().parse(value) ?: Date()
    private fun safeUuid(value: String): UUID = try { UUID.fromString(value) } catch (_: Exception) { UUID.randomUUID() }
    private fun parseDuration(value: String): PresentationDuration = PresentationDuration.entries.firstOrNull { it.value.equals(value, true) } ?: PresentationDuration.FIFTEEN_MINUTES
    private fun parseBlockType(value: String): BlockType = BlockType.entries.firstOrNull { it.name.equals(value, true) } ?: BlockType.body
    private fun parseContentArrangement(value: String): ContentArrangement = ContentArrangement.entries.firstOrNull { it.name.equals(value, true) } ?: ContentArrangement.top
    private fun parseContentAlignment(value: String): ContentAlignment = ContentAlignment.entries.firstOrNull { it.name.equals(value, true) } ?: ContentAlignment.leading

    private fun parseColor(colorObject: JsonObject): RGBColor = RGBColor(
        red = colorObject.float("red") ?: 0f,
        green = colorObject.float("green") ?: 0f,
        blue = colorObject.float("blue") ?: 0f
    )

    private fun parseFontWeight(fontWeightObject: JsonObject?): FontWeight? {
        val key = fontWeightObject?.keys?.firstOrNull() ?: return null
        return FontWeight.entries.firstOrNull { it.name.equals(key, true) }
    }

    private fun parseGroupSize(value: String): Audience.GroupSize = Audience.GroupSize.entries.firstOrNull { it.value.equals(value, true) } ?: Audience.GroupSize.UNKNOWN
    private fun parseMainRole(value: String): Audience.MainRole = Audience.MainRole.entries.firstOrNull { it.value.equals(value, true) } ?: Audience.MainRole.MIXED_OTHER
    private fun parseAgeGroup(value: String): Audience.AgeGroup = Audience.AgeGroup.entries.firstOrNull { it.value.equals(value, true) } ?: Audience.AgeGroup._25_TO_34
    private fun parseKnowledgeLevel(value: String): Audience.KnowledgeLevel = Audience.KnowledgeLevel.entries.firstOrNull { it.value.equals(value, true) } ?: Audience.KnowledgeLevel.MIXED
    private fun parseMainMotivation(value: String): Audience.MainMotivation = Audience.MainMotivation.entries.firstOrNull { it.value.equals(value, true) } ?: Audience.MainMotivation.OTHER
    private fun parseRhetoricValue(value: String): RhetoricValue = RhetoricValue.entries.firstOrNull { it.name.equals(value, true) } ?: RhetoricValue.MIDDLE

    private fun parseStoryBlockCategory(value: String): StoryBlockCategory {
        return StoryBlockCategory.resolve(value) ?: StoryBlockCategory.OTHER
    }

    private fun StoryBlockCategory.toArchiveValue(): String = when (this) {
        StoryBlockCategory.HOOK -> "hook"
        StoryBlockCategory.STAKES -> "stakes"
        StoryBlockCategory.CORE -> "core"
        StoryBlockCategory.TURN -> "turn"
        StoryBlockCategory.AUDIENCE -> "audience"
        StoryBlockCategory.CLOSE -> "close"
        StoryBlockCategory.OTHER -> "other"
    }

    private fun StoryBlockCategory.toSectionType(): StorySectionType = when (this) {
        StoryBlockCategory.HOOK -> StorySectionType.hook
        StoryBlockCategory.STAKES -> StorySectionType.stakes
        StoryBlockCategory.CORE -> StorySectionType.core
        StoryBlockCategory.TURN -> StorySectionType.turn
        StoryBlockCategory.AUDIENCE -> StorySectionType.audience
        StoryBlockCategory.CLOSE -> StorySectionType.close
        StoryBlockCategory.OTHER -> StorySectionType.core
    }

    private fun parsePredefinedBlockType(value: String): PredefinedBlockType? {
        return PredefinedBlockType.resolve(value)
    }
}
