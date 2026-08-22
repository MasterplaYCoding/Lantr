package app.lantr.lantr

actual fun getPlatform(): Platform = object : Platform {
    override val name: String = "Desktop"
}
