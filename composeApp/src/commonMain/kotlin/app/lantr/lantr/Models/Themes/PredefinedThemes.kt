package app.lantr.lantr.Models.Themes

import app.lantr.lantr.Models.BlockType
import kotlinx.serialization.Serializable
import app.lantr.lantr.Models.Themes.ContentMargins


@Serializable
enum class PredefinedThemes {
    VOID,
    ZEUS,
    CHRONOS,
    ODIN,
    RA,
    FREYJA,
    CERNUNNOS,
    NYX,
    ARAWN,
    DIONYSUS,
    ANUBIS,
    ARES;

    val displayName: String
        get() = when (this) {
            VOID -> "Void"
            ZEUS -> "Zeus"
            CHRONOS -> "Chronos"
            ODIN -> "Odin"
            RA -> "Ra"
            FREYJA -> "Freyja"
            CERNUNNOS -> "Cernunnos"
            NYX -> "Nyx"
            ARAWN -> "Arawn"
            DIONYSUS -> "Dionysus"
            ANUBIS -> "Anubis"
            ARES -> "Ares"
        }

    // MARK: - Content placement
    val contentMargins: ContentMargins
        get() = when (this) {
            VOID -> ContentMargins.medium
            ZEUS -> ContentMargins.large
            NYX -> ContentMargins.medium
            RA -> ContentMargins.large
            FREYJA -> ContentMargins.medium
            ARAWN -> ContentMargins.small
            DIONYSUS -> ContentMargins.medium
            CERNUNNOS -> ContentMargins.medium
            ANUBIS -> ContentMargins.large
            ODIN -> ContentMargins.medium
            ARES -> ContentMargins.small
            CHRONOS -> ContentMargins.medium
        }

    val elementSpacing: ElementSpacing
        get() = when (this) {
            VOID -> ElementSpacing.medium
            ZEUS -> ElementSpacing.large
            NYX -> ElementSpacing.medium
            RA -> ElementSpacing.extraLarge
            FREYJA -> ElementSpacing.medium
            ARAWN -> ElementSpacing.small
            DIONYSUS -> ElementSpacing.large
            CERNUNNOS -> ElementSpacing.medium
            ANUBIS -> ElementSpacing.large
            ODIN -> ElementSpacing.large
            ARES -> ElementSpacing.small
            CHRONOS -> ElementSpacing.medium
        }

    // MARK: - Background color
    val backgroundColor: RGBColor
        get() = when (this) {
            VOID -> RGBColor("FFFFFF")
            ZEUS -> RGBColor("FFFFFF")
            NYX -> RGBColor("0B0B0D")
            RA -> RGBColor("FFFBEA")
            FREYJA -> RGBColor("FFF6F9")
            ARAWN -> RGBColor("FFFFFF")
            DIONYSUS -> RGBColor("260A2C")
            CERNUNNOS -> RGBColor("F0F7F4")
            ANUBIS -> RGBColor("121212")
            ODIN -> RGBColor("F7F7F7")
            ARES -> RGBColor("F0EBEB")
            CHRONOS -> RGBColor("0D0D0D")
        }

    val altBackgroundColor: RGBColor
        get() = when (this) {
            VOID -> RGBColor("000000")
            ZEUS -> RGBColor("273242")
            NYX -> RGBColor("FFFFFF")
            RA -> RGBColor("191919")
            FREYJA -> RGBColor("2D2D2D")
            ARAWN -> RGBColor("0B0B0B")
            DIONYSUS -> RGBColor("F4F6FF")
            CERNUNNOS -> RGBColor("1D2D19")
            ANUBIS -> RGBColor("F5F5F5")
            ODIN -> RGBColor("1C1C1C")
            ARES -> RGBColor("300000")
            CHRONOS -> RGBColor("DEDEDE")
        }

    // MARK: - Text Styles
    val textStyles: Map<BlockType, TextStyle>
        get() = when (this) {
            VOID -> mapOf(
                BlockType.h1 to TextStyle(
                    fontSize = 84f,
                    fontWeight = FontWeight.bold,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.h2 to TextStyle(
                    fontSize = 60f,
                    fontWeight = FontWeight.bold,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.h3 to TextStyle(
                    fontSize = 48f,
                    fontWeight = FontWeight.regular,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.body to TextStyle(
                    fontSize = 32f,
                    fontWeight = FontWeight.regular,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.quote to TextStyle(
                    fontSize = 38f,
                    fontWeight = FontWeight.bold,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.bulletList to TextStyle(
                    fontSize = 32f,
                    fontWeight = FontWeight.regular,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                ),
                BlockType.numberedList to TextStyle(
                    fontSize = 32f,
                    fontWeight = FontWeight.regular,
                    fontFamily = "Inter",
                    textColor = RGBColor("000000"),
                    altTextColor = RGBColor("FFFFFF")
                )
            )

            ZEUS -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Inter", RGBColor("0A0A0A"), RGBColor("FFFFFF")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Inter", RGBColor("1C1C1C"), RGBColor("FFFFFF")),
                BlockType.h3 to TextStyle(48f, FontWeight.semibold, "Inter", RGBColor("3C3C3C"), RGBColor("FFFFFF")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("4A4A4A"), RGBColor("FFFFFF")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Inter", RGBColor("004DC2"), RGBColor("FFFB00")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0A0A0A"), RGBColor("FFFFFF")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0A0A0A"), RGBColor("FFFFFF"))
            )

            NYX -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Space Grotesk", RGBColor("FFFFFF"), RGBColor("0B0B0D")),
                BlockType.h2 to TextStyle(60f, FontWeight.bold, "Space Grotesk", RGBColor("FFFFFF"), RGBColor("0B0B0D")),
                BlockType.h3 to TextStyle(48f, FontWeight.bold, "Space Grotesk", RGBColor("FFFFFF"), RGBColor("0B0B0D")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("0B0B0D")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Space Grotesk", RGBColor("9F84E9"), RGBColor("8153FF")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("0B0B0D")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("0B0B0D"))
            )

            RA -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Poppins", RGBColor("191919"), RGBColor("FFFBEA")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Poppins", RGBColor("191919"), RGBColor("FFFBEA")),
                BlockType.h3 to TextStyle(48f, FontWeight.medium, "Poppins", RGBColor("191919"), RGBColor("FFFBEA")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("191919"), RGBColor("FFFBEA")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Poppins", RGBColor("D97706"), RGBColor("D97706")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("191919"), RGBColor("FFFBEA")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("191919"), RGBColor("FFFBEA"))
            )

            FREYJA -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Lora", RGBColor("2D2D2D"), RGBColor("FFF6F9")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Lora", RGBColor("2D2D2D"), RGBColor("FFF6F9")),
                BlockType.h3 to TextStyle(48f, FontWeight.medium, "Lora", RGBColor("2D2D2D"), RGBColor("FFF6F9")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Nunito", RGBColor("2D2D2D"), RGBColor("FFF6F9")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Nunito", RGBColor("D6336C"), RGBColor("F472B6")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Nunito", RGBColor("2D2D2D"), RGBColor("FFF6F9")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Nunito", RGBColor("2D2D2D"), RGBColor("FFF6F9"))
            )

            ARAWN -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.heavy, "Museo Moderno", RGBColor("0B0B0B"), RGBColor("FFF6F9")),
                BlockType.h2 to TextStyle(60f, FontWeight.bold, "Museo Moderno", RGBColor("0B0B0B"), RGBColor("FFF6F9")),
                BlockType.h3 to TextStyle(48f, FontWeight.semibold, "Museo Moderno", RGBColor("0B0B0B"), RGBColor("FFF6F9")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("FFF6F9")),
                BlockType.quote to TextStyle(38f, FontWeight.light, "Inter", RGBColor("D00000"), RGBColor("FF3B30")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("FFF6F9")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("FFF6F9"))
            )

            DIONYSUS -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Urbanist", RGBColor("FFFFFF"), RGBColor("260F26")),
                BlockType.h2 to TextStyle(60f, FontWeight.bold, "Urbanist", RGBColor("FFFFFF"), RGBColor("260F26")),
                BlockType.h3 to TextStyle(48f, FontWeight.regular, "Urbanist", RGBColor("FFFFFF"), RGBColor("260F26")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("260F26")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Lora", RGBColor("F4D35E"), RGBColor("A817B5")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("260F26")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("FFFFFF"), RGBColor("260F26"))
            )

            CERNUNNOS -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Work Sans", RGBColor("1F281D"), RGBColor("F0F7F4")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Work Sans", RGBColor("2E3D2B"), RGBColor("F0F7F4")),
                BlockType.h3 to TextStyle(48f, FontWeight.medium, "Work Sans", RGBColor("4A5C46"), RGBColor("F0F7F4")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Urbanist", RGBColor("2E3D2B"), RGBColor("F0F7F4")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Texturina", RGBColor("228B22"), RGBColor("DAA520")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Urbanist", RGBColor("2E3D2B"), RGBColor("F0F7F4")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Urbanist", RGBColor("2E3D2B"), RGBColor("F0F7F4"))
            )

            ANUBIS -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Texturina", RGBColor("F5F5F5"), RGBColor("121212")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Texturina", RGBColor("F5F5F5"), RGBColor("121212")),
                BlockType.h3 to TextStyle(48f, FontWeight.medium, "Texturina", RGBColor("F5F5F5"), RGBColor("121212")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("F5F5F5"), RGBColor("121212")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Space Grotesk", RGBColor("00E832"), RGBColor("BA8C00")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("F5F5F5"), RGBColor("121212")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("F5F5F5"), RGBColor("121212"))
            )

            ODIN -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.bold, "Merriweather", RGBColor("1C1C1C"), RGBColor("F7F7F7")),
                BlockType.h2 to TextStyle(60f, FontWeight.bold, "Inter", RGBColor("1C1C1C"), RGBColor("F7F7F7")),
                BlockType.h3 to TextStyle(48f, FontWeight.regular, "Inter", RGBColor("1C1C1C"), RGBColor("F7F7F7")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("1C1C1C"), RGBColor("F7F7F7")),
                BlockType.quote to TextStyle(38f, FontWeight.bold, "Merriweather", RGBColor("0D9488"), RGBColor("14B8A6")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("1C1C1C"), RGBColor("F7F7F7")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("1C1C1C"), RGBColor("F7F7F7"))
            )

            ARES -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.semibold, "Urbanist", RGBColor("0B0B0B"), RGBColor("F0EBEB")),
                BlockType.h2 to TextStyle(60f, FontWeight.semibold, "Urbanist", RGBColor("0B0B0B"), RGBColor("F0EBEB")),
                BlockType.h3 to TextStyle(48f, FontWeight.medium, "Urbanist", RGBColor("0B0B0B"), RGBColor("F0EBEB")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("F0EBEB")),
                BlockType.quote to TextStyle(38f, FontWeight.black, "Inter", RGBColor("B70000"), RGBColor("BEBEBE")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("F0EBEB")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("0B0B0B"), RGBColor("F0EBEB"))
            )

            CHRONOS -> mapOf(
                BlockType.h1 to TextStyle(84f, FontWeight.heavy, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C")),
                BlockType.h2 to TextStyle(60f, FontWeight.bold, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C")),
                BlockType.h3 to TextStyle(48f, FontWeight.semibold, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C")),
                BlockType.body to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C")),
                BlockType.quote to TextStyle(38f, FontWeight.medium, "JetBrains Mono", RGBColor("D4AF37"), RGBColor("1E9EB7")),
                BlockType.bulletList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C")),
                BlockType.numberedList to TextStyle(32f, FontWeight.regular, "Inter", RGBColor("EAEAEA"), RGBColor("1C1C1C"))
            )
        }
}
