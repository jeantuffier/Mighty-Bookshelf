package no.northernfield.mightybookshelf

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import no.northernfield.mightybookshelf.add.AddScene
import no.northernfield.mightybookshelf.camera.CameraPreviewScene
import no.northernfield.mightybookshelf.ui.theme.MightyBookshelfTheme

@Composable
fun MainContent() {
    MightyBookshelfTheme {
        val backStack = rememberNavBackStack<Scenes>(Home)
        CompositionLocalProvider(LocalBackStack provides backStack) {
            Scaffold(
                floatingActionButton = {
                    FloatingActionButton(onClick = { backStack.add(Add) }) {
                        Icon(Icons.Filled.Add, "Add button")
                    }
                },
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