package app.lantr.lantr.Navigation

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
import app.lantr.lantr.Util.requestOpenPresentationWindow
import app.lantr.lantr.Views.Presentations.PresentationEditView
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    if (PlatformUtil.isDesktop) {
        DesktopNavigationRoot(modifier)
    } else {
        AndroidNavigationRoot(modifier)
    }
}
@Composable
private fun DesktopNavigationRoot(modifier: Modifier = Modifier) {
    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Home::class, Route.Home.serializer())
                }
            }
        },
        Route.Home
    )

    NavDisplay(
        backStack = rootBackStack,
        onBack = { if (rootBackStack.size > 1) rootBackStack.removeAt(rootBackStack.lastIndex) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeNavigation(
                    onOpenPresentation = { presentation ->
                        requestOpenPresentationWindow(presentation)
                    }
                )
            }
        }
    )
}

@Composable
private fun AndroidNavigationRoot(modifier: Modifier = Modifier) {
    var canvasPresentation by remember { mutableStateOf<Presentation?>(null) }

    val rootBackStack = rememberNavBackStack(
        configuration = SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Route.Home::class, Route.Home.serializer())
                    subclass(Route.Canvas::class, Route.Canvas.serializer())
                }
            }
        },
        Route.Home
    )

    NavDisplay(
        backStack = rootBackStack,
        onBack = { if (rootBackStack.size > 1) rootBackStack.removeAt(rootBackStack.lastIndex) },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Route.Home> {
                HomeNavigation(
                    onOpenPresentation = { presentation ->
                        canvasPresentation = presentation
                        rootBackStack.add(Route.Canvas)
                    }
                )
            }
            entry<Route.Canvas> {
                canvasPresentation?.let { presentation ->
                    PresentationEditView(
                        presentation = presentation,
                        onClose = { rootBackStack.removeAt(rootBackStack.lastIndex) }
                    )
                }
            }
        }
    )
}
