package app.lantr.lantr

// Interfața TA, comună, nu cea din Firebase
interface Platform {
    val name: String
}

// Funcție expect: fiecare target (Android/JVM/iOS) oferă o implementare actual
expect fun getPlatform(): Platform
