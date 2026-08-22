package app.lantr.lantr.Util

import com.sun.jna.Native
import com.sun.jna.Platform
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.ptr.IntByReference
import com.sun.jna.win32.StdCallLibrary
import java.awt.Window

object WindowThemeUtil {

    private interface Dwmapi : StdCallLibrary {
        fun DwmSetWindowAttribute(
            hwnd: WinDef.HWND,
            dwAttribute: Int,
            pvAttribute: IntByReference,
            cbAttribute: Int
        ): Int

        companion object {
            val INSTANCE: Dwmapi = Native.load("dwmapi", Dwmapi::class.java)
        }
    }

    // Makes the title bar follow dark or light mode (Windows 10 20H1+ / Windows 11)
    private const val DWMWA_USE_IMMERSIVE_DARK_MODE = 20

    fun applyTheme(window: Window, isDark: Boolean) {
        if (!Platform.isWindows()) return
        try {
            val hwnd = WinDef.HWND(Native.getWindowPointer(window))
            val value = IntByReference(if (isDark) 1 else 0)
            Dwmapi.INSTANCE.DwmSetWindowAttribute(hwnd, DWMWA_USE_IMMERSIVE_DARK_MODE, value, 4)
        } catch (_: Exception) {
            // Silently fail on unsupported Windows versions
        }
    }
}
