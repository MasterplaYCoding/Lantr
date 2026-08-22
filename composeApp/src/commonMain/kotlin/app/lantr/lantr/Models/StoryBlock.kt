package app.lantr.lantr.Models

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import app.lantr.lantr.Util.UUIDSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import app.lantr.lantr.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import java.util.UUID

fun normalizedArcLookupKey(value: String): String =
    value.trim().lowercase().filter { it.isLetterOrDigit() }

@Serializable
data class StoryBlock(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    var name: String = "",
    var description: String = "",
    var mainIdea: String = "",
    var rhetoric: Rhetoric,
    var category: StoryBlockCategory,
    var slides: List<Slide> = emptyList(),
    var blockType: StoryBlockType
) {
    val isCustomType: Boolean get() = blockType is StoryBlockType.Custom
    val predefinedType: PredefinedBlockType? get() = (blockType as? StoryBlockType.Predefined)?.type

    companion object {
        fun predefined(
            type: PredefinedBlockType,
            mainIdea: String = "",
            slides: List<Slide> = emptyList()
        ) = StoryBlock(
            name = type.displayName,
            description = type.description,
            mainIdea = mainIdea,
            rhetoric = type.rhetoric,
            category = type.category,
            slides = slides,
            blockType = StoryBlockType.Predefined(type)
        )

        fun custom(
            name: String,
            description: String,
            mainIdea: String = "",
            rhetoric: Rhetoric,
            category: StoryBlockCategory,
            slides: List<Slide> = emptyList()
        ) = StoryBlock(
            name = name,
            description = description,
            mainIdea = mainIdea,
            rhetoric = rhetoric,
            category = category,
            slides = slides,
            blockType = StoryBlockType.Custom
        )
    }
}

@Serializable(with = StoryBlockTypeSerializer::class)
sealed class StoryBlockType {
    data class Predefined(val type: PredefinedBlockType) : StoryBlockType()
    data object Custom : StoryBlockType()
}

object StoryBlockTypeSerializer : KSerializer<StoryBlockType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StoryBlockType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): StoryBlockType {
        val input = decoder as? JsonDecoder
            ?: error("StoryBlockTypeSerializer only supports JSON")

        val element = input.decodeJsonElement()

        if (element is JsonObject) {
            element["predefined"]?.let { predefined ->
                val raw = when (predefined) {
                    is JsonPrimitive -> predefined.content
                    is JsonObject -> {
                        predefined["_0"]?.jsonPrimitive?.contentOrNull
                            ?: predefined["type"]?.jsonPrimitive?.contentOrNull
                    }
                    else -> null
                }

                raw?.let {
                    return StoryBlockType.Predefined(
                        PredefinedBlockType.resolve(it)
                            ?: error("Unknown predefined block type: $it")
                    )
                }
            }

            if (element.containsKey("custom")) return StoryBlockType.Custom
        }

        return StoryBlockType.Custom
    }

    override fun serialize(encoder: Encoder, value: StoryBlockType) {
        val output = encoder as? JsonEncoder
            ?: error("StoryBlockTypeSerializer only supports JSON")

        val element = when (value) {
            is StoryBlockType.Predefined -> buildJsonObject {
                put("predefined", buildJsonObject {
                    put("_0", value.type.displayName)
                })
            }

            StoryBlockType.Custom -> buildJsonObject {
                put("custom", buildJsonObject {})
            }
        }

        output.encodeJsonElement(element)
    }
}

fun StoryBlockType.displayName(): String =
    when (this) {
        is StoryBlockType.Predefined -> type.displayName
        StoryBlockType.Custom -> ""
    }

@Composable
fun StoryBlockType.getIcon(): Painter =
    when (this) {
        is StoryBlockType.Predefined -> type.iconPainter()
        StoryBlockType.Custom -> painterResource(Res.drawable.domino_mask)
    }

@Serializable
data class Rhetoric(
    val ethos: RhetoricValue,
    val logos: RhetoricValue,
    val pathos: RhetoricValue
)

@Serializable(with = RhetoricValueSerializer::class)
enum class RhetoricValue {
    LOW, MIDDLE, HIGH;

    val jsonValue: String
        get() = when (this) {
            LOW -> "low"
            MIDDLE -> "middle"
            HIGH -> "high"
        }

    companion object {
        fun resolve(value: String): RhetoricValue? =
            when (normalizedArcLookupKey(value)) {
                "low" -> LOW
                "middle", "mid" -> MIDDLE
                "high" -> HIGH
                else -> null
            }
    }
}

object RhetoricValueSerializer : KSerializer<RhetoricValue> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RhetoricValue", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): RhetoricValue {
        val input = decoder as? JsonDecoder
        val element = input?.decodeJsonElement()

        if (element is JsonPrimitive) {
            return RhetoricValue.resolve(element.content)
                ?: error("Unknown rhetoric value: ${element.content}")
        }

        if (element is JsonObject) {
            element.keys.firstOrNull()?.let {
                return RhetoricValue.resolve(it)
                    ?: error("Unknown legacy rhetoric value: $it")
            }
        }

        val raw = decoder.decodeString()
        return RhetoricValue.resolve(raw)
            ?: error("Unknown rhetoric value: $raw")
    }

    override fun serialize(encoder: Encoder, value: RhetoricValue) {
        encoder.encodeString(value.jsonValue)
    }
}

@Serializable(with = StoryBlockCategorySerializer::class)
enum class StoryBlockCategory {
    HOOK, STAKES, CORE, TURN, AUDIENCE, CLOSE, OTHER;

    val jsonValue: String
        get() = when (this) {
            HOOK -> "hook"
            STAKES -> "stakes"
            CORE -> "core"
            TURN -> "turn"
            AUDIENCE -> "audience"
            CLOSE -> "close"
            OTHER -> "other"
        }

    val displayTitle: String
        get() = when (this) {
            HOOK -> "Hook Blocks"
            STAKES -> "Stakes Blocks"
            CORE -> "Core Blocks"
            TURN -> "Turn Blocks"
            AUDIENCE -> "Audience Blocks"
            CLOSE -> "Close Blocks"
            OTHER -> "Other Blocks"
        }

    val displaySubtitle: String
        get() = when (this) {
            HOOK -> "Attention & Orientation"
            STAKES -> "Problem & Urgency"
            CORE -> "Proof & Substance"
            TURN -> "Shift & Reframe"
            AUDIENCE -> "Identity & Ownership"
            CLOSE -> "Resolution & Movement"
            OTHER -> "Everything Else"
        }

    companion object {
        fun resolve(value: String): StoryBlockCategory? =
            when (normalizedArcLookupKey(value)) {
                "hook" -> HOOK
                "stakes", "tension" -> STAKES
                "core" -> CORE
                "turn", "turning" -> TURN
                "audience" -> AUDIENCE
                "close", "closing" -> CLOSE
                "other" -> OTHER
                else -> null
            }
    }
}

object StoryBlockCategorySerializer : KSerializer<StoryBlockCategory> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("StoryBlockCategory", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): StoryBlockCategory {
        val input = decoder as? JsonDecoder
        val element = input?.decodeJsonElement()

        if (element is JsonPrimitive) {
            return StoryBlockCategory.resolve(element.content)
                ?: error("Unknown story block category: ${element.content}")
        }

        if (element is JsonObject) {
            element.keys.firstOrNull()?.let {
                return StoryBlockCategory.resolve(it)
                    ?: error("Unknown legacy story block category: $it")
            }
        }

        val raw = decoder.decodeString()
        return StoryBlockCategory.resolve(raw)
            ?: error("Unknown story block category: $raw")
    }

    override fun serialize(encoder: Encoder, value: StoryBlockCategory) {
        encoder.encodeString(value.jsonValue)
    }
}

@Serializable(with = PredefinedBlockTypeSerializer::class)
enum class PredefinedBlockType(val displayName: String) {
    PATTERN_BREAK("Pattern Break"),
    HUMAN_MOMENT("Human Moment"),
    BIG_PROMISE("Big Promise"),
    HARD_QUESTION("Hard Question"),
    SHOCK_STAT("Shock Stat"),
    QUIET_BEAT("Quiet Beat"),

    FALSE_CHOICE("False Choice"),
    HIDDEN_ENEMY("Hidden Enemy"),
    SHARED_PAIN("Shared Pain"),
    REAL_COST("Real Cost"),
    TIME_PRESSURE("Time Pressure"),
    MORAL_COST("Moral Cost"),

    FIRST_TRUTH("First Truth"),
    PROOF_POINT("Proof Point"),
    CLEAR_PATH("Clear Path"),
    DEEP_DIVE("Deep Dive"),
    FAIR_OBJECTION("Fair Objection"),
    REVEAL_MOMENT("Reveal Moment"),
    CASE_STUDY("Case Study"),
    FAILURE_LESSON("Failure Lesson"),
    FRAMING_METAPHOR("Framing Metaphor"),

    NEW_LENS("New Lens"),
    KEY_INSIGHT("Key Insight"),
    BOLD_LEAP("Bold Leap"),
    UNEXPECTED_ALLY("Unexpected Ally"),
    COMIC_BEAT("Comic Beat"),

    DIRECT_MIRROR("Direct Mirror"),
    INNER_TRUTH("Inner Truth"),
    DIRECT_CHALLENGE("Direct Challenge"),
    JOIN_IN("Join In"),
    PULSE_CHECK("Pulse Check"),

    FULL_CIRCLE("Full Circle"),
    FUTURE_VIEW("Future View"),
    NEXT_STEP("Next Step"),
    FINAL_LINE("Final Line"),
    OPEN_LOOP("Open Loop"),
    CALL_BACK("Call Back");

    companion object {
        fun resolve(value: String): PredefinedBlockType? =
            when (normalizedArcLookupKey(value)) {
                "patternbreak", "flipthescript" -> PATTERN_BREAK
                "humanmoment", "humanspark" -> HUMAN_MOMENT
                "bigpromise", "thepromise" -> BIG_PROMISE
                "hardquestion", "provocation" -> HARD_QUESTION
                "shockstat" -> SHOCK_STAT
                "quietbeat", "pausehold" -> QUIET_BEAT

                "falsechoice", "forkintheroad" -> FALSE_CHOICE
                "hiddenenemy", "thevillain" -> HIDDEN_ENEMY
                "sharedpain", "sharedstruggle" -> SHARED_PAIN
                "realcost", "whatsatrisk" -> REAL_COST
                "timepressure" -> TIME_PRESSURE
                "moralcost" -> MORAL_COST

                "firsttruth" -> FIRST_TRUTH
                "proofpoint", "provenpath", "traction" -> PROOF_POINT
                "clearpath", "stepbystep" -> CLEAR_PATH
                "deepdive", "deconstructit" -> DEEP_DIVE
                "fairobjection", "steelman" -> FAIR_OBJECTION
                "revealmoment" -> REVEAL_MOMENT
                "casestudy" -> CASE_STUDY
                "failurelesson", "lessonfromfailure" -> FAILURE_LESSON
                "framingmetaphor", "metaphor" -> FRAMING_METAPHOR

                "newlens" -> NEW_LENS
                "keyinsight", "ahamoment" -> KEY_INSIGHT
                "boldleap", "theleap" -> BOLD_LEAP
                "unexpectedally" -> UNEXPECTED_ALLY
                "comicbeat", "comicrelief" -> COMIC_BEAT

                "directmirror", "audiencemirror" -> DIRECT_MIRROR
                "innertruth", "insidertruth" -> INNER_TRUTH
                "directchallenge" -> DIRECT_CHALLENGE
                "joinin" -> JOIN_IN
                "pulsecheck", "knowledgecheck" -> PULSE_CHECK

                "fullcircle" -> FULL_CIRCLE
                "futureview", "futurepicture" -> FUTURE_VIEW
                "nextstep", "calltoaction", "takeaway" -> NEXT_STEP
                "finalline", "micdrop" -> FINAL_LINE
                "openloop", "cliffhanger" -> OPEN_LOOP
                "callback" -> CALL_BACK

                else -> null
            }
    }

    val description: String
        get() = when (this) {
            PATTERN_BREAK -> "Break the default frame fast so the audience pays attention."
            HUMAN_MOMENT -> "Open with a lived moment that makes the topic feel human."
            BIG_PROMISE -> "Tell the audience why staying with you will pay off."
            HARD_QUESTION -> "Ask the question they cannot comfortably ignore."
            SHOCK_STAT -> "Lead with a number that snaps attention into focus."
            QUIET_BEAT -> "Use restraint, silence, or delayed release to create tension."

            FALSE_CHOICE -> "Show the trap, tradeoff, or limiting frame the audience is stuck inside."
            HIDDEN_ENEMY -> "Name the real obstacle behind the visible problem."
            SHARED_PAIN -> "Surface the frustration the audience already knows."
            REAL_COST -> "Make the cost of inaction concrete."
            TIME_PRESSURE -> "Explain why this matters now, not someday."
            MORAL_COST -> "Show the human or cultural loss beneath the practical one."

            FIRST_TRUTH -> "State the underlying principle beneath the noise."
            PROOF_POINT -> "Bring evidence that the idea holds in reality."
            CLEAR_PATH -> "Show the sequence, framework, or method clearly."
            DEEP_DIVE -> "Break down how something works so the audience really gets it."
            FAIR_OBJECTION -> "State the strongest objection honestly, then answer it."
            REVEAL_MOMENT -> "Bring the thing itself into view: the product, feature, idea, or hidden shape."
            CASE_STUDY -> "Use a concrete real example to make the abstract visible."
            FAILURE_LESSON -> "Teach through what went wrong and what it revealed."
            FRAMING_METAPHOR -> "Use one strong metaphor, image, or thought experiment to help the audience hold the idea."

            NEW_LENS -> "Reframe the whole issue in a stronger way."
            KEY_INSIGHT -> "Deliver the idea that changes interpretation."
            BOLD_LEAP -> "Show the commitment, risk, or decisive move."
            UNEXPECTED_ALLY -> "Introduce the surprising force, partner, or factor that changed things."
            COMIC_BEAT -> "Relieve pressure so the next point lands harder."

            DIRECT_MIRROR -> "Name what the audience is thinking, fearing, resisting, or wanting."
            INNER_TRUTH -> "Say the thing insiders know but rarely say aloud."
            DIRECT_CHALLENGE -> "Put agency or responsibility in the audience’s hands."
            JOIN_IN -> "Invite visible participation."
            PULSE_CHECK -> "Confirm the audience is still with you."

            FULL_CIRCLE -> "Return to the opening and complete the loop."
            FUTURE_VIEW -> "Make the better future visible."
            NEXT_STEP -> "Tell them what to do, decide, or remember next."
            FINAL_LINE -> "End with a distilled line that lands hard."
            OPEN_LOOP -> "End with a productive unresolved question."
            CALL_BACK -> "Reuse an earlier phrase, line, or image for extra force."
        }

    val category: StoryBlockCategory
        get() = when (this) {
            PATTERN_BREAK, HUMAN_MOMENT, BIG_PROMISE, HARD_QUESTION, SHOCK_STAT, QUIET_BEAT -> StoryBlockCategory.HOOK
            FALSE_CHOICE, HIDDEN_ENEMY, SHARED_PAIN, REAL_COST, TIME_PRESSURE, MORAL_COST -> StoryBlockCategory.STAKES
            FIRST_TRUTH, PROOF_POINT, CLEAR_PATH, DEEP_DIVE, FAIR_OBJECTION, REVEAL_MOMENT, CASE_STUDY, FAILURE_LESSON, FRAMING_METAPHOR -> StoryBlockCategory.CORE
            NEW_LENS, KEY_INSIGHT, BOLD_LEAP, UNEXPECTED_ALLY, COMIC_BEAT -> StoryBlockCategory.TURN
            DIRECT_MIRROR, INNER_TRUTH, DIRECT_CHALLENGE, JOIN_IN, PULSE_CHECK -> StoryBlockCategory.AUDIENCE
            FULL_CIRCLE, FUTURE_VIEW, NEXT_STEP, FINAL_LINE, OPEN_LOOP, CALL_BACK -> StoryBlockCategory.CLOSE
        }

    val rhetoric: Rhetoric
        get() = when (this) {
            PATTERN_BREAK -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            HUMAN_MOMENT -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.HIGH)
            BIG_PROMISE -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
            HARD_QUESTION -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            SHOCK_STAT -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.MIDDLE)
            QUIET_BEAT -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.HIGH)

            FALSE_CHOICE -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            HIDDEN_ENEMY -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.MIDDLE)
            SHARED_PAIN -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.HIGH)
            REAL_COST -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            TIME_PRESSURE -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            MORAL_COST -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.LOW, RhetoricValue.HIGH)

            FIRST_TRUTH -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.LOW)
            PROOF_POINT -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.LOW)
            CLEAR_PATH -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.HIGH, RhetoricValue.LOW)
            DEEP_DIVE -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.LOW)
            FAIR_OBJECTION -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.HIGH, RhetoricValue.MIDDLE)
            REVEAL_MOMENT -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            CASE_STUDY -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
            FAILURE_LESSON -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
            FRAMING_METAPHOR -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)

            NEW_LENS -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.HIGH, RhetoricValue.MIDDLE)
            KEY_INSIGHT -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.HIGH, RhetoricValue.HIGH)
            BOLD_LEAP -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            UNEXPECTED_ALLY -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
            COMIC_BEAT -> Rhetoric(RhetoricValue.LOW, RhetoricValue.LOW, RhetoricValue.HIGH)

            DIRECT_MIRROR -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.HIGH)
            INNER_TRUTH -> Rhetoric(RhetoricValue.HIGH, RhetoricValue.MIDDLE, RhetoricValue.MIDDLE)
            DIRECT_CHALLENGE -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            JOIN_IN -> Rhetoric(RhetoricValue.LOW, RhetoricValue.LOW, RhetoricValue.HIGH)
            PULSE_CHECK -> Rhetoric(RhetoricValue.LOW, RhetoricValue.LOW, RhetoricValue.MIDDLE)

            FULL_CIRCLE -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            FUTURE_VIEW -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            NEXT_STEP -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.HIGH, RhetoricValue.MIDDLE)
            FINAL_LINE -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.HIGH)
            OPEN_LOOP -> Rhetoric(RhetoricValue.LOW, RhetoricValue.MIDDLE, RhetoricValue.HIGH)
            CALL_BACK -> Rhetoric(RhetoricValue.MIDDLE, RhetoricValue.LOW, RhetoricValue.MIDDLE)
        }

    @Composable
    fun iconPainter(): Painter = painterResource(
        when (this) {
            PATTERN_BREAK -> Res.drawable.autorenew
            HUMAN_MOMENT -> Res.drawable.bolt
            BIG_PROMISE -> Res.drawable.featured_seasonal_and_gifts
            HARD_QUESTION -> Res.drawable.help
            SHOCK_STAT -> Res.drawable.bar_chart
            QUIET_BEAT -> Res.drawable.pause

            FALSE_CHOICE -> Res.drawable.alt_route
            HIDDEN_ENEMY -> Res.drawable.domino_mask
            SHARED_PAIN -> Res.drawable.group
            REAL_COST -> Res.drawable.warning
            TIME_PRESSURE -> Res.drawable.autorenew
            MORAL_COST -> Res.drawable.warning

            FIRST_TRUTH -> Res.drawable.lightbulb
            PROOF_POINT -> Res.drawable.verified
            CLEAR_PATH -> Res.drawable.format_list_numbered
            DEEP_DIVE -> Res.drawable.extension
            FAIR_OBJECTION -> Res.drawable.back_hand
            REVEAL_MOMENT -> Res.drawable.visibility
            CASE_STUDY -> Res.drawable.document_search
            FAILURE_LESSON -> Res.drawable.lightbulb
            FRAMING_METAPHOR -> Res.drawable.format_quote

            NEW_LENS -> Res.drawable.eyeglasses_2
            KEY_INSIGHT -> Res.drawable.candle
            BOLD_LEAP -> Res.drawable.paragliding
            UNEXPECTED_ALLY -> Res.drawable.person_add
            COMIC_BEAT -> Res.drawable.mood

            DIRECT_MIRROR -> Res.drawable.frame_person
            INNER_TRUTH -> Res.drawable.visibility
            DIRECT_CHALLENGE -> Res.drawable.back_hand
            JOIN_IN -> Res.drawable.diversity_3
            PULSE_CHECK -> Res.drawable.not_listed_location

            FULL_CIRCLE -> Res.drawable.refresh
            FUTURE_VIEW -> Res.drawable.landscape
            NEXT_STEP -> Res.drawable.arrow_circle_right
            FINAL_LINE -> Res.drawable.mic_external_on
            OPEN_LOOP -> Res.drawable.storm
            CALL_BACK -> Res.drawable.amend
        }
    )
}

object PredefinedBlockTypeSerializer : KSerializer<PredefinedBlockType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("PredefinedBlockType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): PredefinedBlockType {
        val input = decoder as? JsonDecoder
        val element = input?.decodeJsonElement()

        if (element is JsonPrimitive) {
            return PredefinedBlockType.resolve(element.content)
                ?: error("Unknown predefined block type: ${element.content}")
        }

        if (element is JsonObject) {
            element.keys.firstOrNull()?.let {
                return PredefinedBlockType.resolve(it)
                    ?: error("Unknown legacy predefined block type: $it")
            }
        }

        val raw = decoder.decodeString()
        return PredefinedBlockType.resolve(raw)
            ?: error("Unknown predefined block type: $raw")
    }

    override fun serialize(encoder: Encoder, value: PredefinedBlockType) {
        encoder.encodeString(value.displayName)
    }
}