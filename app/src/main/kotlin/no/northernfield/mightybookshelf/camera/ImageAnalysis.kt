package no.northernfield.mightybookshelf.camera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

data class ImageAnalysisResult(
    val imageUri: String,
    val data: JsonObject
) {
    companion object {
        val empty = ImageAnalysisResult(
            imageUri = "",
            data = JsonObject(emptyMap())
        )
    }
}

class ImageAnalysis {
    private val _data = MutableStateFlow(ImageAnalysisResult.empty)
    val results = _data.asStateFlow()

    fun emitNewData(newResult: ImageAnalysisResult) {
        _data.value = newResult
    }

    fun reset() {
        _data.value = ImageAnalysisResult.empty
    }
}
