package no.northernfield.mightybookshelf.camera

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            modifier = Modifier
                .padding(bottom = 32.dp)
                .width(154.dp)
                .height(74.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(6.dp),
            onClick = { cameraEventBus.produceEvent(ShutterClicked) }
        ) {
            Text("Take picture")
        }
        if (previewState.isProcessingImage) {
            ImageProcessingDialog()
        }
    }
}

@Preview
@Composable
private fun CameraPreviewContentPreview() {
    CameraPreviewContent(modifier = Modifier.fillMaxSize())
}