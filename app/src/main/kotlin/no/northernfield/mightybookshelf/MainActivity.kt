package no.northernfield.mightybookshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import no.northernfield.mightybookshelf.Scenes.Companion.topLevelScenes
import no.northernfield.mightybookshelf.add.AddScene
import no.northernfield.mightybookshelf.camera.CameraPreviewScene
import no.northernfield.mightybookshelf.ui.theme.MightyBookshelfTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MightyBookshelfTheme {
                val backStack = rememberNavBackStack<Scenes>(Home)
                CompositionLocalProvider(LocalBackStack provides backStack) {
                    Scaffold(
                        bottomBar = {
                            if (topLevelScenes.contains(backStack.last())) {
                                NavigationBar {
                                    topLevelScenes.forEachIndexed { index, item ->
                                        NavigationBarItem(
                                            icon = {
                                                Icon(
                                                    if (backStack.last() == topLevelScenes[index]) topLevelScenes[index].selectedIcon else topLevelScenes[index].unselectedIcon,
                                                    contentDescription = item::class.java.simpleName
                                                )
                                            },
                                            label = { Text(item::class.java.simpleName) },
                                            selected = backStack.last() == topLevelScenes[index],
                                            onClick = { backStack.add(item) }
                                        )
                                    }
                                }
                            }
                        }
                    ) { padding ->
                        NavDisplay(
                            modifier = Modifier.padding(padding),
                            backStack = backStack,
                            entryDecorators = listOf(
                                rememberSavedStateNavEntryDecorator(),
                            ),
                            entryProvider = entryProvider {
                                entry<Home> {
                                    // Home screen content goes here
                                    Text(text = "Home Screen")
                                }
                                entry<Search> {
                                    // Search screen content goes here
                                    Text(text = "Search Screen")
                                }
                                entry<Add> {
                                    AddScene(Modifier.fillMaxSize()) {
                                        backStack.add(Camera)
                                    }
                                }
                                entry<Camera> {
                                    CameraPreviewScene(Modifier.fillMaxSize())
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
