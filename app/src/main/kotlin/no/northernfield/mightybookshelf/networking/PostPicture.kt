package no.northernfield.mightybookshelf.networking


import android.net.Uri
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import java.io.File

fun interface PostPicture {
    suspend operator fun invoke(input: File): JsonObject
}

@Serializable
data class UploadFileResponse(val id: String)

@Serializable
data class SignedUrlResponse(val url: String)

@Serializable
private data class Message(
    val role: String,
    val content: List<JsonObject>,
)

@Serializable
data class ResponseFormat(val type: String)

@Serializable
private data class MistralOcrBodyRequest(
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

fun PostPicture(
    client: HttpClient
) = PostPicture { input ->
    val uploadResponse = client.submitFormWithBinaryData(
        url = "/v1/files",
        formData = formData {
            append("purpose", "ocr")
            append("file", input.readBytes(), Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${input.name}\"")
            })
        }
    ).body<UploadFileResponse>()
    val signedUrl =
        client.get("/v1/files/${uploadResponse.id}/url?expiry=24").body<SignedUrlResponse>()
    val response = client.post("/v1/chat/completions") {
        headers.append(HttpHeaders.ContentType, "application/json")
        setBody(
            MistralOcrBodyRequest(
                model = "mistral-small-latest",
                messages = listOf(
                    Message(
                        role = "user",
                        content = listOf(
                            JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("text"),
                                    "text" to JsonPrimitive(
                                        """
                                    The picture should contain either a book cover, a comic book 
                                    cover or a page listing all the creatives involved. Extract 
                                    the following information, if available, from the picture and 
                                    structure them with key-value pairs for each information 
                                    extracted.
                                    - key "title", value : title and subtitle as one string.
                                    - key "publisher", value: the publisher of the book.
                                    - key "quote", value: the content of the quote, if any, and who 
                                      wrote it. Both element in one single string separated by a 
                                      hyphen character. Don't include double quotes around the
                                      quote.
                                    - key "reward", value: rewards won by the book, if any.
                                    - key "language", value: the language of the book.
                                    
                                    If the picture contains a book extract the following:
                                    - key "author", value: the author of the book.
                                    
                                    If the picture contains a comic-book extract the following:
                                    - key "writer", value: the write of the book.
                                    - key "artist", value: the artist of the book. If several 
                                      artists are involved, create a key value pair for each of them.
                                    - key "colorist", value: the colorist of the book. If several 
                                      colorists are involved, create a key value pair for each of 
                                      them. Don't add a key value pair if you don't find any.
                                    - key "letterer", value: the colorist of the book. If several 
                                      colorists are involved, create a key value pair for each of 
                                      them. Don't add a key value pair if you don't find any.
                                 
                                    Create a list containing all the key-value pairs and format it in json
                                """.trimIndent()
                                    )
                                ),
                            ),
                            JsonObject(
                                mapOf(
                                    "type" to JsonPrimitive("image_url"),
                                    "image_url" to JsonPrimitive(signedUrl.url)
                                )
                            ),
                        )
                    )
                ),
                documentImageLimit = 8,
                documentPageLimit = 64,
                responseFormat = ResponseFormat("json_object"),
            )
        )
    }.body<MistralChatResponse>()
    Log.d("PostPicture", "response: $response")
    val sanitizedString = response.choices.first().message.content
        .replace("\n", "")
    Log.d("PostPicture", "sanitized: $sanitizedString")
    Json.parseToJsonElement(sanitizedString).jsonObject
}
