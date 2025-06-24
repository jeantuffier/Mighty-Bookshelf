package no.northernfield.mightybookshelf

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Scenes : NavKey {
    companion object {
        val topLevelScenes = listOf<TopLevelScenes>(Home, Search, Add)
    }
}

sealed interface TopLevelScenes : Scenes {
    val unselectedIcon: ImageVector
    val selectedIcon: ImageVector
}

@Serializable
data object Home : TopLevelScenes {
    override val unselectedIcon = Icons.Outlined.Home
    override val selectedIcon = Icons.Filled.Home
}

@Serializable
data object Search : TopLevelScenes {
    override val unselectedIcon = Icons.Outlined.Search
    override val selectedIcon = Icons.Filled.Search
}

@Serializable
data object Add : TopLevelScenes {
    override val unselectedIcon = Icons.Outlined.Add
    override val selectedIcon = Icons.Filled.Add
}

@Serializable
data object Camera : Scenes

val LocalBackStack = staticCompositionLocalOf<NavBackStack> { NavBackStack() }