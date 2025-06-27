package no.northernfield.mightybookshelf

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Scenes : NavKey

@Serializable
data object Home : Scenes

@Serializable
data object Add : Scenes

@Serializable
data object Camera : Scenes

val LocalBackStack = staticCompositionLocalOf<NavBackStack> { NavBackStack() }

fun NavBackStack.pop() = removeAt(lastIndex)
