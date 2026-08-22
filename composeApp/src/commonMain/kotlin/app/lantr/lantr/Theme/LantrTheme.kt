package app.lantr.lantr.Theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

private val LantrLightColorScheme = lightColorScheme(
    primary = Gold,                           // 0xCB9D08
    onPrimary = Color.White,
    primaryContainer = GoldLight,             // 0xF2E7C1
    onPrimaryContainer = Color(0xFF3D2E00),
    secondary = CrimsonDark,                  // 0xB01433
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = CrimsonDark,
    tertiary = Color(0xFF520B31),             // Dark purple (same in both modes)
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color.Gray,
    outlineVariant = Color(0xFFE0E0E0),
    error = Color(0xFFB3261E),
    onError = Color.White,
)

private val LantrDarkColorScheme = darkColorScheme(
    primary = Gold,                           // Keep gold as primary
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3D3111),
    onPrimaryContainer = GoldLight,
    secondary = CrimsonDark,                  // 0xB01433 (same in both modes)
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF442929),
    onSecondaryContainer = Color(0xFFFFDAD6),
    tertiary = Color(0xFF520B31),             // Dark purple (same in both modes)
    onTertiary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF1C1C1E),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF2C2C2E),
    onSurfaceVariant = Color(0xFFC4C4C6),
    outline = Color(0xFF8E8E93),
    outlineVariant = Color(0xFF38383A),
    error = Color(0xFFEF5350),
    onError = Color.White,
)

@Composable
fun LantrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LantrDarkColorScheme else LantrLightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    CompositionLocalProvider(
        LocalLantrColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

// Convenience accessor so views can do `LantrTheme.extendedColors.goldPrimary`
object LantrTheme {
    val extendedColors: LantrExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLantrColors.current
}
