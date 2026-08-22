package app.lantr.lantr

import android.os.Build

// actual = implementează declarația din commonMain
actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}
