package no.northernfield.mightybookshelf.camera

import androidx.compose.runtime.Composable
import no.northernfield.mightybookshelf.EventBus
import org.koin.compose.koinInject
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val CAMERA_EVENT_BUS = "CameraPreviewEventBus"

val cameraModule = module {

    factoryOf<TakePicture>(::TakePicture)

    single(named(CAMERA_EVENT_BUS)) {
        EventBus<CameraEvent>()
    }

    single {
        ImageAnalysis()
    }
}

@Composable
fun cameraEventBus() = koinInject<EventBus<CameraEvent>>(named(CAMERA_EVENT_BUS))