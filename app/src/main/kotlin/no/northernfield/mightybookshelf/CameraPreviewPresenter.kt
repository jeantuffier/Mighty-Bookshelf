package no.northernfield.mightybookshelf

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.awaitCancellation

sealed interface CameraPreviewEvent {
    data class BindToCamera(
        val context: Context,
        val lifecycleOwner: LifecycleOwner,
    ) : CameraPreviewEvent
}

@Composable
fun cameraPreviewPresenter(): State<SurfaceRequest?> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    return produceSaveableState<SurfaceRequest?>(null) {
        bindToCamera(context, lifecycleOwner) {
            value = it
        }
    }
}

suspend fun bindToCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    updateSurfaceRequest: (SurfaceRequest) -> Unit,
) {
    val processCameraProvider = ProcessCameraProvider.awaitInstance(context)
    processCameraProvider.bindToLifecycle(
        lifecycleOwner,
        DEFAULT_BACK_CAMERA,
        Preview.Builder().build().apply {
            setSurfaceProvider {
                Log.d("PRESENTER", "SurfaceRequest: $it")
                updateSurfaceRequest(it)
            }
        },
    )
    try {
        awaitCancellation()
    } finally {
        processCameraProvider.unbindAll()
    }
}