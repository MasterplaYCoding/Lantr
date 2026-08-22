package app.lantr.lantr.Theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================
// RAW COLOR PALETTE (private, not used directly by views)
// ============================================================

// Brand Gold
val Gold = Color(0xFFCB9D08)
val GoldLight = Color(0xFFF2E7C1)
val GoldSemiTransparent = Color(0x80CB9D08)
val GoldSubtle = Color(0x33CB9D08)

// Brand Reds
val CrimsonDark = Color(0xFFB01433)
val Crimson = Color(0xFFDC143C)
val CrimsonMedium = Color(0xFFDF2348)
val CurtainRed = Color(0xFFE57373)
val DarkMaroon = Color(0xFF852727)

// Brand Purples
val Lavender = Color(0xFF9370DB)
val PurpleMaterial = Color(0xFF9C27B0)
val WallPurple = Color(0xFF520B31)
val WallPurpleMedium = Color(0xFF731B49)
val WallPurpleDark = Color(0xFF77204E)

// Accent Blue
val AccentBlue = Color(0xFF2196F3)

// Canvas Gradient (stays the same in both modes -- intentionally always dark)
val CanvasGradientCenter = Color(0xFF594B53)
val CanvasGradientEdge = Color(0xFF2E1F27)

// Navigation warm cream
val NavWarmCream = Color(0xFFFFF7DD)

// Search bar
val SearchBackground = Color(0xFFF0EBD9)
val SearchText = Color(0xFF513F07)

// Story Section Category Colors (used as semantic "category identity" --
// same in light and dark because they're content identity, not UI surface)
val StoryCategoryRed = Color(0xFFD32F2F)
val StoryCategoryPurple = Color(0xFF7B1FA2)
val StoryCategoryBlue = Color(0xFF03A9F4)

// ============================================================
// EXTENDED COLOR TOKENS (brand-specific, via CompositionLocal)
// ============================================================

@Immutable
data class LantrExtendedColors(
    // Gold brand
    val goldPrimary: Color,
    val goldLight: Color,
    val goldSemiTransparent: Color,
    val goldSubtle: Color,

    // Narrative identity colors (unchanged across modes)
    val curtainRed: Color,
    val wallPurple: Color,
    val accentBlue: Color,

    // Destructive / error tints
    val destructive: Color,              // for delete buttons bg
    val destructiveForeground: Color,    // for delete button icon
    val destructiveMuted: Color,         // translucent delete bg

    // Navigation
    val navBackground: Color,
    val navIndicator: Color,

    // Search bar
    val searchBackground: Color,
    val searchForeground: Color,

    // Canvas gradient (intentionally the same in both modes)
    val canvasGradientCenter: Color,
    val canvasGradientEdge: Color,

    // Canvas card surface
    val canvasCardSurface: Color,

    // Inspector / popover surface
    val inspectorBackground: Color,

    // Creation flow
    val selectionBackground: Color,
    val selectionBorder: Color,
    val selectionIcon: Color,
    val chipBackground: Color,

    // Toolbar
    val toolbarButtonInactiveBackground: Color,
    val toolbarControlBackground: Color,
    val toolbarControlHovered: Color,

    // Misc
    val secondaryText: Color,
    val tertiaryText: Color,
    val placeholderText: Color,
    val thumbnailBackground: Color,
    val inputFieldBackground: Color,
    val separatorColor: Color,
    val onGold: Color,                   // text/icon color ON gold backgrounds

    val arcInspectorToggleMenuSelected: Color,
    val arcInspectorToggleMenuUnselected: Color,
    val arcInspectorSearchBar: Color,

    )

val LightExtendedColors = LantrExtendedColors(
    goldPrimary = Gold,
    goldLight = GoldLight,
    goldSemiTransparent = GoldSemiTransparent,
    goldSubtle = GoldSubtle,
    curtainRed = Crimson,
    wallPurple = Lavender,
    accentBlue = AccentBlue,
    destructive = CrimsonDark,
    destructiveForeground = CrimsonDark,
    destructiveMuted = Color(0x66B01433),
    navBackground = NavWarmCream,
    navIndicator = GoldSemiTransparent,
    searchBackground = SearchBackground,
    searchForeground = SearchText,
    canvasGradientCenter = CanvasGradientCenter,
    canvasGradientEdge = CanvasGradientEdge,
    canvasCardSurface = Color(0xFFF5F5F5),
    inspectorBackground = Color.White,
    selectionBackground = Color(0x40852727),
    selectionBorder = Color(0x80852727),
    selectionIcon = DarkMaroon,
    chipBackground = Color(0xFFEFEFEF),
    toolbarButtonInactiveBackground = Color(0xFFF1F1F1),
    toolbarControlBackground = Color(0xFFF5F5F5),
    toolbarControlHovered = Color(0xFFE0E0E0),
    secondaryText = Color.Gray,
    tertiaryText = Color(0xFF858585),
    placeholderText = Color(0xFF919191),
    thumbnailBackground = Color(0xFFE8E8E9),
    inputFieldBackground = Color(0xFFF2F2F2),
    separatorColor = Color(0xFFEDEDED),
    onGold = Color.White,
    arcInspectorToggleMenuSelected = Color.White,
    arcInspectorToggleMenuUnselected = Color(0xffe0d6ca),
    arcInspectorSearchBar = Color(0xffe6e0db),
)

val DarkExtendedColors = LantrExtendedColors(
    goldPrimary = Gold,                              // Gold stays gold
    goldLight = Color(0xFF3D3111),                   // darker tinted gold
    goldSemiTransparent = Color(0x80CB9D08),         // same alpha trick
    goldSubtle = Color(0x33CB9D08),
    curtainRed = Crimson,                            // narrative identity stays
    wallPurple = Lavender,
    accentBlue = AccentBlue,
    destructive = Color(0xFFEF5350),                 // slightly lighter for visibility
    destructiveForeground = Color(0xFFEF5350),
    destructiveMuted = Color(0x44EF5350),
    navBackground = Color(0xFF1E1B16),               // dark warm surface
    navIndicator = Color(0x80CB9D08),
    searchBackground = Color(0xFF2C2820),            // dark warm input
    searchForeground = Color(0xFFD4C68A),            // warm gold text
    canvasGradientCenter = CanvasGradientCenter,     // unchanged
    canvasGradientEdge = CanvasGradientEdge,         // unchanged
    canvasCardSurface = Color(0xFF000000),
    inspectorBackground = Color(0xFF1C1C1E),
    selectionBackground = Color(0x40CB9D08),
    selectionBorder = Color(0x80CB9D08),
    selectionIcon = Gold,
    chipBackground = Color(0xFF2C2C2E),
    toolbarButtonInactiveBackground = Color(0xFF2C2C2E),
    toolbarControlBackground = Color(0xFF2C2C2E),
    toolbarControlHovered = Color(0xFF3A3A3C),
    secondaryText = Color(0xFF8E8E93),
    tertiaryText = Color(0xFF8E8E93),
    placeholderText = Color(0xFF636366),
    thumbnailBackground = Color(0xFF3A3A3C),
    inputFieldBackground = Color(0xFF2C2C2E),
    separatorColor = Color(0xFF38383A),
    onGold = Color.White,
    arcInspectorToggleMenuSelected = Color(0xff655d66),
    arcInspectorToggleMenuUnselected = Color(0xff322a33),
    arcInspectorSearchBar = Color(0xff312c2d),
)

val LocalLantrColors = staticCompositionLocalOf { LightExtendedColors }
