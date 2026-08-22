package app.lantr.lantr.Theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lantr.lantr.Util.PlatformUtil
import io.github.composefluent.background.Mica

@Composable
fun DesktopFluentSurface(
    content: @Composable () -> Unit
) {
    if (PlatformUtil.isDesktop) {
        Mica(Modifier.fillMaxSize()) {
            content()
        }
    } else {
        content()
    }
}
