package app.lantr.lantr.Views.mainMenu.mainScreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import app.lantr.lantr.Models.WindowHeightSizeClass
import app.lantr.lantr.Models.WindowSizeClass
import app.lantr.lantr.Models.WindowWidthSizeClass
import java.awt.Toolkit

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val density = LocalDensity.current.density
    val screenSize = Toolkit.getDefaultToolkit().screenSize

    val widthDp = (screenSize.width / density).toInt()
    val heightDp = (screenSize.height / density).toInt()

    return WindowSizeClass(
        widthSizeClass = when {
            widthDp < 600 -> WindowWidthSizeClass.Compact
            widthDp < 840 -> WindowWidthSizeClass.Medium
            else -> WindowWidthSizeClass.Expanded
        },
        heightSizeClass = when {
            heightDp < 480 -> WindowHeightSizeClass.Compact
            heightDp < 900 -> WindowHeightSizeClass.Medium
            else -> WindowHeightSizeClass.Expanded
        }
    )
}

@Composable
actual fun lockOrientation(lockToPortrait: Boolean) {
    // Desktop doesn't need orientation locking - empty implementation
}