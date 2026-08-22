package app.lantr.lantr.Util

import androidx.compose.runtime.Composable

@Composable
actual fun PlatformBackHandler(enabled: Boolean, onBack: () -> Unit) {
    // No hardware back button on Desktop — no-op
}
