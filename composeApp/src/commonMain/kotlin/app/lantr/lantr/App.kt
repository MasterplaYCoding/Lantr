package app.lantr.lantr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.lantr.lantr.Models.WindowWidthSizeClass
import app.lantr.lantr.Theme.DesktopFluentSurface
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.mainMenu.mainScreen.MainScreen
import app.lantr.lantr.Views.mainMenu.mainScreen.calculateWindowSizeClass
import app.lantr.lantr.Views.mainMenu.mainScreen.lockOrientation
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.lantr.lantr.Views.mainMenu.navbar.sampleMenuItems

@Composable
fun MainLayout() {

    val customWindowSize = calculateWindowSizeClass()

    // Lock phones to portrait orientation
    val shouldLockToPortrait = customWindowSize.widthSizeClass == WindowWidthSizeClass.Compact
    lockOrientation(shouldLockToPortrait)

    DesktopFluentSurface {
        LantrTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = if (PlatformUtil.isDesktop) Color.Transparent else MaterialTheme.colorScheme.background
            ) {
                MainScreen()
            }
        }
    }
}

@Composable
@Preview
fun App() {
    MainLayout()
}
