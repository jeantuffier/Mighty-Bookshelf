package no.northernfield.mightybookshelf.camera

import android.content.Context
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
import androidx.navigation3.runtime.NavBackStack
import arrow.core.Either
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import no.northernfield.mightybookshelf.LocalBackStack
import no.northernfield.mightybookshelf.networking.ProcessImage
import no.northernfield.mightybookshelf.pop
import org.koin.compose.koinInject
import java.io.File

data class CameraPreviewStateError(
    val title: String,
    val message: String,
)

data class CameraPreviewState(
    val surfaceRequest: SurfaceRequest?,
    val capturedImage: File?,
    val isProcessingImage: Boolean,
    val error: CameraPreviewStateError?,
)

sealed interface CameraEvent {
    data object ShutterClicked : CameraEvent
    data object ErrorDialogDismissed : CameraEvent
}

@Composable
fun cameraPreviewPresenter(
    events: Flow<CameraEvent> = cameraEventBus().events,
    takePicture: TakePicture = koinInject(),
    processImage: ProcessImage = koinInject(),
    imageAnalysisEventBus: ImageAnalysisEventBus = koinInject(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    backStack: NavBackStack = LocalBackStack.current,
): State<CameraPreviewState> = produceState(CameraPreviewState(null, null, false, null)) {
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    var bindJob: Job? = null
    events.onEach { event ->
        when (event) {
            is CameraEvent.ShutterClicked -> {
                when (val result = takePicture(context, imageCapture)) {
                    is Either.Left -> {
                        when (val error = result.value) {
                            is CaptureImageError.ImageCaptureError -> {
                                value = value.copy(
                                    error = CameraPreviewStateError(
                                        title = "Image Capture Error",
                                        message = error.message ?: "Unknown error occurred"
                                    )
                                )
                            }

                            CaptureImageError.UriIsNull -> {
                                value = value.copy(
                                    error = CameraPreviewStateError(
                                        title = "Image Capture Error",
                                        message = "Captured image URI is null"
                                    )
                                )
                            }
                        }
                    }
                    is Either.Right -> {
                        value = value.copy(isProcessingImage = true)
                        when (val processingResult = processImage(result.value.file)) {
                            is Either.Left -> {
                                value = value.copy(
                                    error = CameraPreviewStateError(
                                        title = "Image Processing Error",
                                        message = processingResult.value.description
                                    )
                                )
                            }

                            is Either.Right -> {
                                bindJob?.cancel()
                                imageAnalysisEventBus.emitNewData(
                                    SuccessfulImageAnalysisEvent(
                                        imageUri = result.value.uri.toString(),
                                        data = processingResult.value,
                                    )
                                )
                                value = value.copy(isProcessingImage = false)
                                backStack.pop()
                            }
                        }
                    }
                }
            }

            CameraEvent.ErrorDialogDismissed -> value = value.copy(error = null)
        }
    }.launchIn(this)

    bindJob = launch {
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
