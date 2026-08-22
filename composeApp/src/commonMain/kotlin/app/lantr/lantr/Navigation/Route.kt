package app.lantr.lantr.Navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Home : Route, NavKey {

        @Serializable
        data object Presentations : Route

        @Serializable
        data object Resources : Route

        @Serializable
        data object Settings : Route

        @Serializable
        data object CreateProject : Route
    }

    @Serializable
    data object Canvas : Route
}
