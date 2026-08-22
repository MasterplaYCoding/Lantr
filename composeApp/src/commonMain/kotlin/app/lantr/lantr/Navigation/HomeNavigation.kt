package app.lantr.lantr.Navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import app.lantr.lantr.Models.Presentation
import app.lantr.lantr.Util.PlatformUtil
import app.lantr.lantr.Views.CreationFlow.CreateProjectFlow
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.presentations.PresentationsScreen
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.resources.ResourcesScreen
import app.lantr.lantr.Views.mainMenu.mainScreen.screens.settings.SettingsScreen
import app.lantr.lantr.Views.mainMenu.navbar.AdaptiveNavigationLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun HomeNavigation(
    onOpenPresentation: (Presentation) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Home.Presentations::class, Route.Home.Presentations.serializer())
                    subclass(Route.Home.Resources::class, Route.Home.Resources.serializer())
                    subclass(Route.Home.Settings::class, Route.Home.Settings.serializer())
                    subclass(Route.Home.CreateProject::class, Route.Home.CreateProject.serializer())
                }
            }
        },
        Route.Home.Presentations
    )

    var showCreationDialog by remember { mutableStateOf(false) }
    val currentRoute = backStack.lastOrNull()
    val isInCreationFlow = currentRoute is Route.Home.CreateProject
    val shouldHideNavbar = isInCreationFlow && PlatformUtil.isPhone

    val selectedMenuItem = when (currentRoute) {
        is Route.Home.Presentations -> "presentations"
        is Route.Home.Resources -> "resources"
        is Route.Home.Settings -> "settings"
        else -> "presentations"
    }

    val navContent: @Composable () -> Unit = {
        NavDisplay(
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Route.Home.Presentations> {
                    PresentationsScreen(
                        onCreateProject = { backStack.add(Route.Home.CreateProject) },
                        onOpenPresentation = onOpenPresentation
                    )
                }
                entry<Route.Home.Resources> {
                    ResourcesScreen()
                }
                entry<Route.Home.Settings> {
                    SettingsScreen()
                }
                entry<Route.Home.CreateProject> {
                    CreateProjectFlow(
                        onCancelingCreationFlow = { backStack.removeAt(backStack.lastIndex) }
                    )
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (shouldHideNavbar) {
            navContent()
        } else {
            AdaptiveNavigationLayout(
                windowSizeClass = PlatformUtil.material3WindowSizeClass,
                selectedMenuItem = selectedMenuItem,
                onItemClick = { routeStr ->
                    val target: Route = when (routeStr) {
                        "resources" -> Route.Home.Resources
                        "settings" -> Route.Home.Settings
                        else -> Route.Home.Presentations
                    }
                    while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    backStack[0] = target
                },
                onCreateProject = { showCreationDialog = true },
                content = navContent
            )
        }

        if (showCreationDialog) {
            AlertDialog(
                onDismissRequest = { showCreationDialog = false },
                text = {
                    CreateProjectFlow(onCancelingCreationFlow = { showCreationDialog = false })
                },
                confirmButton = {},
                containerColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}