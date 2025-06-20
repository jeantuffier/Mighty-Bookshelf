package no.northernfield.mightybookshelf.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import no.northernfield.mightybookshelf.camera.CameraEvent.ShutterClicked

@Composable
fun CameraPreviewContent(modifier: Modifier) {
    val previewState by cameraPreviewPresenter()
    val cameraEventBus = cameraEventBus()
    Box(modifier = Modifier.fillMaxSize()) {
        previewState.surfaceRequest?.let { request ->
            CameraXViewfinder(
                surfaceRequest = request,
                modifier = modifier
            )
        }
        Button(
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = { cameraEventBus.produceEvent(ShutterClicked) }
        ) {
            Text("Take picture")
        }
    }
}