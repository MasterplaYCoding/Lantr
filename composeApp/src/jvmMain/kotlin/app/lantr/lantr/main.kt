package app.lantr.lantr

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.lantr.lantr.Theme.DesktopFluentSurface
import app.lantr.lantr.Theme.LantrTheme
import app.lantr.lantr.Util.DesktopLtrnSingleInstance
import app.lantr.lantr.Util.PresentationWindowSession
import app.lantr.lantr.Util.PresentationWindowState
import app.lantr.lantr.Util.WindowThemeUtil
import app.lantr.lantr.Util.loadLtrnSessionsFromArgs
import app.lantr.lantr.Views.Presentations.PresentationEditView

fun main(args: Array<String>) {
    if (DesktopLtrnSingleInstance.handOffToRunningInstance(args)) {
        return
    }

    DesktopLtrnSingleInstance.startListening()

    val launchSessions = loadLtrnSessionsFromArgs(args)
    PresentationWindowState.openAll(launchSessions)

    application {
        val shouldShowHomeWindow = launchSessions.isEmpty()

        if (shouldShowHomeWindow) {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Lantr",
            ) {
                val isDark = isSystemInDarkTheme()
                SideEffect {
                    WindowThemeUtil.applyTheme(window, isDark)
                }

                App()
            }
        }

        PresentationWindowState.openPresentations.forEach { session ->
            key(session.presentation.id.toString()) {
                PresentationWindow(session = session)
            }
        }
    }
}

@Composable
private fun PresentationWindow(session: PresentationWindowSession) {
    Window(
        onCloseRequest = { PresentationWindowState.close(session) },
        title = session.presentation.title,
    ) {
        val isDark = isSystemInDarkTheme()
        SideEffect {
            WindowThemeUtil.applyTheme(window, isDark)
        }

        DesktopFluentSurface {
            LantrTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Transparent
                ) {
                    PresentationEditView(
                        presentation = session.presentation,
                        initialFilePath = session.filePath,
                        onClose = { PresentationWindowState.close(session) }
                    )
                }
            }
        }
    }
}
