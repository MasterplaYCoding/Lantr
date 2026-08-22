package app.lantr.lantr.Views.mainMenu.mainScreen

import androidx.compose.runtime.Composable
import app.lantr.lantr.Models.WindowSizeClass
import app.lantr.lantr.Navigation.NavigationRoot

@Composable
expect fun lockOrientation(lockToPortrait: Boolean)

@Composable
expect fun calculateWindowSizeClass(): WindowSizeClass

@Composable
fun MainScreen() {
    NavigationRoot()
}
