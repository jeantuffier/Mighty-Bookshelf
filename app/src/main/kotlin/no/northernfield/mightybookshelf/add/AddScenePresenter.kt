package no.northernfield.mightybookshelf.add

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.json.jsonPrimitive
import no.northernfield.mightybookshelf.camera.ImageAnalysis
import no.northernfield.mightybookshelf.database.AddDao
import no.northernfield.mightybookshelf.database.BookCreativeEntity
import no.northernfield.mightybookshelf.database.BookEntity
import no.northernfield.mightybookshelf.database.CreativeEntity
import no.northernfield.mightybookshelf.produceSaveableState
import org.koin.compose.koinInject

sealed interface AddSceneEvents {
    data class BookTypeChanged(val type: BookType) : AddSceneEvents
    data class TitleChanged(val title: String) : AddSceneEvents
    data object CreativesAdded : AddSceneEvents
    data class CreativesChanged(val index: Int, val creative: Creative) : AddSceneEvents
    data object CreativeRemoved : AddSceneEvents
    data class RewardChanged(val reward: String) : AddSceneEvents
    data class QuoteChanged(val quote: String) : AddSceneEvents
    data class PublisherChanged(val publisher: String) : AddSceneEvents
    data class LanguageChanged(val language: String) : AddSceneEvents
    data object SaveClicked : AddSceneEvents
}

@Parcelize
enum class BookType : Parcelable {
    BOOK, COMIC_BOOK
}

@Parcelize
enum class CreativeRoles : Parcelable {
    AUTHOR, WRITER, ARTIST, COLORIST, LETTERER, COVER_ARTISTS;

    fun displayName() = name.lowercase()
        .replace('_', ' ')
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}

@Parcelize
data class Creative(
    val role: CreativeRoles,
    val name: String,
) : Parcelable

@Parcelize
data class AddSceneState(
    val type: BookType,
    val title: String,
    val creatives: List<Creative>,
    val reward: String,
    val quote: String,
    val publisher: String,
    val language: String,
    val imageUri: String,
    val error: String?,
) : Parcelable {
    companion object {
        fun default(type: BookType) = AddSceneState(
            type = type,
            title = "",
            creatives = if (type == BookType.BOOK) {
                listOf(Creative(CreativeRoles.AUTHOR, ""))
            } else {
                listOf(Creative(CreativeRoles.WRITER, ""))
            },
            reward = "",
            quote = "",
            publisher = "",
            language = "",
            imageUri = "",
            error = null
        )
    }
}

@Composable
fun addScenePresenter(
    events: Flow<AddSceneEvents> = addSceneEventBus().events,
    imageAnalysis: ImageAnalysis = koinInject(),
    addDao: AddDao = koinInject<AddDao>(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): State<AddSceneState> =
    produceSaveableState(AddSceneState.default(BookType.BOOK)) {
        imageAnalysis.results.filter { it.data.isNotEmpty() }
            .onEach { (imageUri, data) ->
                val title = data["title"]?.jsonPrimitive?.content ?: ""
                val subtitle = data["subtitle"]?.jsonPrimitive?.content ?: ""
                val creatives = mapOf(
                    CreativeRoles.WRITER to (data["writer"]?.jsonPrimitive?.content ?: ""),
                    CreativeRoles.AUTHOR to (data["author"]?.jsonPrimitive?.content ?: ""),
                    CreativeRoles.ARTIST to (data["artist"]?.jsonPrimitive?.content ?: ""),
                    CreativeRoles.COLORIST to (data["colorist"]?.jsonPrimitive?.content ?: ""),
                ).filter { it.value.isNotEmpty() }
                    .map { Creative(it.key, it.value) }
                    .toList()

                value = value.copy(
                    title = if (subtitle.isNotEmpty()) {
                        "$title: $subtitle"
                    } else {
                        title
                    },
                    publisher = data["publisher"]?.jsonPrimitive?.content ?: "",
                    language = data["language"]?.jsonPrimitive?.content ?: "",
                    reward = data["rewards"]?.jsonPrimitive?.content ?: "",
                    quote = data["quote"]?.jsonPrimitive?.content ?: "",
                    creatives = creatives,
                    imageUri = imageUri,
                )
                imageAnalysis.reset()
            }.launchIn(this)

        events.onEach {
            when (it) {
                is AddSceneEvents.BookTypeChanged -> value = value.copy(type = it.type)
                is AddSceneEvents.TitleChanged -> value = value.copy(title = it.title)
                is AddSceneEvents.CreativesChanged -> {
                    if (it.index < 0 || it.index > value.creatives.size) {
                        value = value.copy(error = "Invalid creative index")
                        return@onEach
                    }
                    value = value.copy(
                        creatives = value.creatives
                            .toMutableList().apply {
                                this[it.index] = it.creative
                            }
                    )
                }

                is AddSceneEvents.CreativesAdded -> value = value.copy(
                    creatives = value.creatives + Creative(CreativeRoles.WRITER, "")
                )

                is AddSceneEvents.CreativeRemoved -> {
                    value = value.copy(
                        creatives = value.creatives.dropLast(1)
                    )
                }

                is AddSceneEvents.RewardChanged -> value = value.copy(reward = it.reward)
                is AddSceneEvents.QuoteChanged -> value = value.copy(quote = it.quote)
                is AddSceneEvents.PublisherChanged -> value = value.copy(publisher = it.publisher)
                is AddSceneEvents.LanguageChanged -> value = value.copy(language = it.language)
                is AddSceneEvents.SaveClicked -> {
                    withContext(dispatcher) {
                        val bookEntity = BookEntity(
                            type = value.type,
                            title = value.title,
                            reward = value.reward,
                            quote = value.quote,
                            publisher = value.publisher,
                            language = value.language,
                            imageUri = value.imageUri,
                        )
                        val bookId = addDao.insertBook(bookEntity)
                        val creativeIds = value.creatives.map {
                            val creativeEntity = CreativeEntity(
                                name = it.name,
                                roles = it.role,
                            )
                            addDao.insertCreative(creativeEntity)
                        }
                        creativeIds.forEach { creativeId ->
                            addDao.insertBookCreative(BookCreativeEntity(bookId, creativeId))
                        }
                        value = AddSceneState.default(value.type)
                    }
                }
            }
        }.launchIn(this)
    }
