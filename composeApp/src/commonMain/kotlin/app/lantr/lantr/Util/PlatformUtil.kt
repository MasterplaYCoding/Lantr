package app.lantr.lantr.Util

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.lantr.lantr.Models.WindowWidthSizeClass
import app.lantr.lantr.Views.mainMenu.mainScreen.calculateWindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass as Material3WindowSizeClass


object PlatformUtil {
    val isDesktop: Boolean = isDesktopPlatform

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    val material3WindowSizeClass: WindowSizeClass
        @Composable
        get() {
            val customWindowSize = calculateWindowSizeClass()
            val material3WindowSize = Material3WindowSizeClass.calculateFromSize(
                DpSize(
                    width = when(customWindowSize.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> 400.dp
                        WindowWidthSizeClass.Medium -> 700.dp
                        WindowWidthSizeClass.Expanded -> 1000.dp
                    },
                    height = 800.dp
                )
            )
            return material3WindowSize
        }

    val isPhone : Boolean
        @Composable
        get()
        {
            val customWindowSize = calculateWindowSizeClass()
            return customWindowSize.widthSizeClass == WindowWidthSizeClass.Compact
        }

}
