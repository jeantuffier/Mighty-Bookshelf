package no.northernfield.mightybookshelf.camera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

data class SuccessfulImageAnalysisEvent(
    val imageUri: String,
    val data: JsonObject
) {
    companion object {
        val empty = SuccessfulImageAnalysisEvent(
            imageUri = "",
            data = JsonObject(emptyMap())
        )
    }
}

class ImageAnalysisEventBus {
    private val _data = MutableStateFlow(SuccessfulImageAnalysisEvent.empty)
    val results = _data.asStateFlow()

    fun emitNewData(newResult: SuccessfulImageAnalysisEvent) {
        _data.value = newResult
    }

    fun reset() {
        _data.value = SuccessfulImageAnalysisEvent.empty
    }
}
