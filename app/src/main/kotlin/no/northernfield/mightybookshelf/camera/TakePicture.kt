package no.northernfield.mightybookshelf.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import arrow.core.Either
import arrow.core.left
import arrow.core.right
import no.northernfield.mightybookshelf.camera.CameraError.ImageCaptureError
import no.northernfield.mightybookshelf.camera.CameraError.UriIsNull
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


fun interface TakePicture {
    suspend operator fun invoke(
        context: Context,
        imageCapture: ImageCapture
    ): Either<CameraError, File>
}

fun TakePicture() = TakePicture { context, imageCapture ->
    suspendCoroutine<Either<CameraError, File>> { continuation ->
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

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = outputFileResults.savedUri
                if (uri == null) {
                    continuation.resume(UriIsNull.left())
                } else {
                    val file = getFile(context, uri)
                    continuation.resume(file.right())
                }
            }
        }
        imageCapture.takePicture(outputOptions, executor, callback)
    }
}

private fun getFile(context: Context, uri: Uri): File {
    val destinationFilename =
        File(context.filesDir.path + File.separatorChar + queryName(context, uri))

    try {
        context.contentResolver.openInputStream(uri).use { ins ->
            if (ins == null) {
                throw IllegalArgumentException("InputStream is null for URI: $uri")
            }
            createFileFromStream(ins, destinationFilename)
        }
    } catch (ex: Exception) {
        Log.e("Save File", ex.message!!)
        ex.printStackTrace()
    }
    return destinationFilename
}

fun createFileFromStream(ins: InputStream, destination: File) {
    try {
        FileOutputStream(destination).use { os ->
            val buffer = ByteArray(4096)
            var length: Int
            while ((ins.read(buffer).also { length = it }) > 0) {
                os.write(buffer, 0, length)
            }
            os.flush()
        }
    } catch (ex: java.lang.Exception) {
        Log.e("Save File", ex.message!!)
        ex.printStackTrace()
    }
}

private fun queryName(context: Context, uri: Uri): String? {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = cursor.getString(nameIndex)
            }
        }
    }
    if (result == null) {
        result = uri.lastPathSegment
    }
    return result
}
