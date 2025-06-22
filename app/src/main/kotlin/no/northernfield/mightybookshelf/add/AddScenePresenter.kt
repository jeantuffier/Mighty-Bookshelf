package no.northernfield.mightybookshelf.add

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import no.northernfield.mightybookshelf.produceSaveableState
import kotlin.text.lowercase

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
    val error: String?,
) : Parcelable

@Composable
fun addScenePresenter(
    events: Flow<AddSceneEvents> = addSceneEventBus().events
): State<AddSceneState> =
    produceSaveableState(
        AddSceneState(
            type = BookType.BOOK,
            title = "",
            creatives = listOf(Creative(CreativeRoles.AUTHOR, "")),
            reward = "",
            quote = "",
            publisher = "",
            language = "",
            error = null
        )
    ) {
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
                is AddSceneEvents.SaveClicked -> {}
            }
        }.launchIn(this)
    }
