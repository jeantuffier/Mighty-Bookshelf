package no.northernfield.mightybookshelf.camera

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
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
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import no.northernfield.mightybookshelf.EventBus
import no.northernfield.mightybookshelf.LocalBackStack
import no.northernfield.mightybookshelf.networking.PostPicture
import org.koin.compose.koinInject
import java.io.File

sealed interface CameraError {
    data class ImageCaptureError(val message: String?) : CameraError
    data object UriIsNull : CameraError
}

data class CameraPreviewState(
    val surfaceRequest: SurfaceRequest?,
    val capturedImage: File?,
    val error: CameraError?,
)

sealed interface CameraEvent {
    data object ShutterClicked : CameraEvent
}

@Composable
fun cameraPreviewPresenter(
    events: Flow<CameraEvent> = cameraEventBus().events,
    takePicture: TakePicture = koinInject(),
    postPicture: PostPicture = koinInject(),
    aiAnalysis: AiAnalysis = koinInject(),
    context: Context = LocalContext.current,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    backStack: NavBackStack = LocalBackStack.current,
): State<CameraPreviewState> = produceState(CameraPreviewState(null, null, null)) {
    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()
    events.onEach { event ->
        when (event) {
            is CameraEvent.ShutterClicked -> {
                when (val result = takePicture(context, imageCapture)) {
                    is Either.Left -> value = value.copy(error = result.value)
                    is Either.Right -> {
                        /*val base64 = getBase64ForUriAndPossiblyCrash(result.value, context.contentResolver)
                        println(base64)
                        if (base64.isNotEmpty()) {
                            postPicture(base64)
                        }*/
                        val result = postPicture(result.value)
                        aiAnalysis.emitNewData(result)
                        backStack.removeAt(backStack.lastIndex)
                    }
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

    try { awaitCancellation() } finally { processCameraProvider.unbindAll() }
}


private fun getBase64ForUriAndPossiblyCrash(uri: Uri, contentResolver: ContentResolver): String {
    return try {
        val bytes = contentResolver.openInputStream(uri)?.readBytes()
        Base64.encodeToString(bytes, Base64.DEFAULT)
    } catch (error: Exception) {
        error.printStackTrace()
        ""// This exception always occurs
    }
}
