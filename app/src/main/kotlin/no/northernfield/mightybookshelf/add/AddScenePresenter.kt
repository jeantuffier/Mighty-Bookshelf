package no.northernfield.mightybookshelf.add

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.parcelize.Parcelize
import no.northernfield.mightybookshelf.produceSaveableState

sealed interface AddSceneEvents {
    data class TitleChanged(val title: String) : AddSceneEvents
    data class AuthorChanged(val author: String) : AddSceneEvents
    data class RewardChanged(val reward: String) : AddSceneEvents
    data class QuoteChanged(val quote: String) : AddSceneEvents
    data class PublisherChanged(val publisher: String) : AddSceneEvents
    data object SaveClicked : AddSceneEvents
}

@Parcelize
data class AddSceneState(
    val title: String,
    val author: String,
    val reward: String,
    val quote: String,
    val publisher: String,
    val error: String?,
): Parcelable

@Composable
fun addScenePresenter(
    events: Flow<AddSceneEvents> = addSceneEventBus().events
): State<AddSceneState> = produceSaveableState(AddSceneState("", "", "", "", "", null)) {
    events.onEach {
        when (it) {
            is AddSceneEvents.TitleChanged -> value = value.copy(title = it.title)
            is AddSceneEvents.AuthorChanged -> value = value.copy(author = it.author)
            is AddSceneEvents.RewardChanged -> value = value.copy(reward = it.reward)
            is AddSceneEvents.QuoteChanged -> value = value.copy(quote = it.quote)
            is AddSceneEvents.PublisherChanged -> value = value.copy(publisher = it.publisher)
            is AddSceneEvents.SaveClicked -> {}
        }
    }.launchIn(this)
}