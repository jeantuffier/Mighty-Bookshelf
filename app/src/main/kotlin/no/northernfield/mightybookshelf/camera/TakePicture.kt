package no.northernfield.mightybookshelf.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import no.northernfield.mightybookshelf.camera.CameraError.ImageCaptureError
import no.northernfield.mightybookshelf.camera.CameraError.UriIsNull
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun interface TakePicture {
    suspend operator fun invoke(
        context: Context,
        imageCapture: ImageCapture
    ): Either<CameraError, Uri>
}

fun TakePicture() = TakePicture { context, imageCapture ->
    suspendCoroutine<Either<CameraError, Uri>> { continuation ->
        val name = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Mighty Bookshelf")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()
        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : ImageCapture.OnImageSavedCallback {
            override fun onError(error: ImageCaptureException) =
                continuation.resume(ImageCaptureError(error.message).left())

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) =
                continuation.resume(outputFileResults.savedUri?.right() ?: UriIsNull.left())
        }
        imageCapture.takePicture(outputOptions, executor, callback)
    }
}
