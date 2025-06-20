package no.northernfield.mightybookshelf.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import arrow.core.Either
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

sealed interface CameraError {
    data class ImageCaptureError(val message: String?) : CameraError
    data object UriIsNull : CameraError
}

data class CameraPreviewState(
    val surfaceRequest: SurfaceRequest?,
    val capturedImage: Uri?,
    val error: CameraError?,
)

sealed interface CameraEvent {
    data object ShutterClicked : CameraEvent
}

@Composable
fun cameraPreviewPresenter(
    events: Flow<CameraEvent> = cameraEventBus().events,
    takePicture: TakePicture = koinInject<TakePicture>(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
): State<CameraPreviewState> = produceState(CameraPreviewState(null, null, null)) {
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    events.onEach { event ->
        when (event) {
            is CameraEvent.ShutterClicked -> {
                value = when (val result = takePicture(context, imageCapture)) {
                    is Either.Left -> value.copy(error = result.value)
                    is Either.Right -> value.copy(capturedImage = result.value)
                }
            }
        }
    }.launchIn(this)

    launch {
        bindToCamera(context, lifecycleOwner, imageCapture) {
            value = value.copy(surfaceRequest = it)
        }
    }
}

private suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    imageCaptureUseCase: ImageCapture,
    updateSurfaceRequest: (SurfaceRequest) -> Unit,
) {
    val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
    val previewUseCase = Preview.Builder().build().apply {
        setSurfaceProvider(updateSurfaceRequest::invoke)
    }
    processCameraProvider.bindToLifecycle(
        lifecycleOwner,
        DEFAULT_BACK_CAMERA,
        previewUseCase,
        imageCaptureUseCase,
    )
    try {
        awaitCancellation()
    } finally {
        processCameraProvider.unbindAll()
    }
}
