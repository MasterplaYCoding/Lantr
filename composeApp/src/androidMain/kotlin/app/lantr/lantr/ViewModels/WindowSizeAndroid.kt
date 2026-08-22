package app.lantr.lantr.Views.mainMenu.mainScreen

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import app.lantr.lantr.Models.WindowHeightSizeClass
import app.lantr.lantr.Models.WindowSizeClass
import app.lantr.lantr.Models.WindowWidthSizeClass

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp

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
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(lockToPortrait) {
        val activity = context as? Activity

        // Check if device is a phone (< 600dp width)
        val isPhone = configuration.screenWidthDp < 600

        activity?.requestedOrientation = when {
            // Only lock orientation on phones
            isPhone && lockToPortrait -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            // For tablets or when not locking, allow all orientations
            else -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}