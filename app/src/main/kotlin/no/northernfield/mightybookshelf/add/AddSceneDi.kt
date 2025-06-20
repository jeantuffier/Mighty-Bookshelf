package no.northernfield.mightybookshelf.add

import androidx.compose.runtime.Composable
import no.northernfield.mightybookshelf.EventBus
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val ADD_SCENE_EVENT_BUS = "AddSceneEventBus"

val addSceneModule = module {

    single(named(ADD_SCENE_EVENT_BUS)) {
        EventBus<AddSceneEvents>()
    }
}

@Composable
fun addSceneEventBus() = koinInject<EventBus<AddSceneEvents>>(named(ADD_SCENE_EVENT_BUS))