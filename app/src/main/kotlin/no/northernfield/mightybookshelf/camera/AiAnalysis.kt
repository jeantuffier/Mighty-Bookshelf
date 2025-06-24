package no.northernfield.mightybookshelf.camera

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonObject

class AiAnalysis {
    private val _data = MutableStateFlow<JsonObject>(JsonObject(emptyMap()))
    val data = _data.asStateFlow()

    fun emitNewData(newData: JsonObject) {
        _data.value = newData
    }

    fun reset() {
        _data.value = JsonObject(emptyMap())
    }
}