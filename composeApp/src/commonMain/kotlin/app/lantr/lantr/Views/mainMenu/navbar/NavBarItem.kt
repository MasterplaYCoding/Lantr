package app.lantr.lantr.Views.mainMenu.navbar

import app.lantr.lantr.generated.resources.Res
import app.lantr.lantr.generated.resources.podium
import app.lantr.lantr.generated.resources.puzzle
import app.lantr.lantr.generated.resources.settings

import org.jetbrains.compose.resources.DrawableResource
import java.util.UUID

data class MenuItem(
    val label: String,
    val icon: DrawableResource,
    val route: String
)

// Example menu items
val sampleMenuItems = listOf(
    MenuItem("Presentations", Res.drawable.podium, "presentations"),
    MenuItem(label = "Resources", Res.drawable.puzzle, "resources"),
    MenuItem(label = "Settings", Res.drawable.settings, "settings")
)