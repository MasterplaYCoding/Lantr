package app.lantr.lantr.Models

import androidx.compose.runtime.Composable

enum class WindowWidthSizeClass {
    Compact,    // < 600dp
    Medium,     // 600dp - 839dp
    Expanded    // >= 840dp
}

enum class WindowHeightSizeClass {
    Compact,    // < 480dp
    Medium,     // 480dp - 899dp
    Expanded    // >= 900dp
}

data class WindowSizeClass(
    val widthSizeClass: WindowWidthSizeClass,
    val heightSizeClass: WindowHeightSizeClass
)
