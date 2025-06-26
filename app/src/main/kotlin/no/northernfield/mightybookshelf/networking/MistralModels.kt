package no.northernfield.mightybookshelf.networking

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class UploadFileResponse(val id: String)

@Serializable
data class SignedUrlResponse(val url: String)

@Serializable
data class Message(
    val role: String,
    val content: List<JsonObject>,
)

@Serializable
data class ResponseFormat(val type: String)

@Serializable
data class MistralOcrBodyRequest(
    val model: String,
    val messages: List<Message>,
    @SerialName("document_image_limit") val documentImageLimit: Int,
    @SerialName("document_page_limit") val documentPageLimit: Int,
    @SerialName("response_format") val responseFormat: ResponseFormat,
    //@SerialName("include_image_base64") val includeImageBase64: Boolean,
)

@Serializable
data class ChoiceMessage(
    val content: String,
)

@Serializable
data class Choice(
    val message: ChoiceMessage,
)

@Serializable
data class MistralChatResponse(
    val choices: List<Choice>,
)