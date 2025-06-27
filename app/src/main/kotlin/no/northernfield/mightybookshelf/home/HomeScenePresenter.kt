package no.northernfield.mightybookshelf.home

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import no.northernfield.mightybookshelf.add.BookType
import no.northernfield.mightybookshelf.add.CreativeRoles
import no.northernfield.mightybookshelf.database.SelectDao
import no.northernfield.mightybookshelf.produceSaveableState
import org.koin.compose.koinInject

@Parcelize
data class Creative(
    val id: Long,
    val name: String,
    val role: CreativeRoles,
) : Parcelable

@Parcelize
data class Book(
    val id: Long,
    val title: String,
    val type: BookType,
    val reward: String,
    val quote: String,
    val publisher: String,
    val language: String,
    val imageUri: String,
    val creatives: List<Creative>
) : Parcelable

@Parcelize
data class HomePresenterState(
    val items: List<Book>,
) : Parcelable {
    companion object {
        val Empty = HomePresenterState(emptyList())
    }
}

@Composable
fun homeScenePresenter(
    selectDao: SelectDao = koinInject(),
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
): State<HomePresenterState> =
    produceSaveableState(HomePresenterState.Empty) {
        withContext(dispatcher) {
            val data = selectDao.selectAllBooks()
                .groupBy { it.bookId }
                .map { bookWithCreatives ->
                    val bookData = bookWithCreatives.value.first()
                    Book(
                        id = bookData.bookId,
                        title = bookData.title,
                        type = BookType.valueOf(bookData.type),
                        reward = bookData.reward ?: "",
                        quote = bookData.quote ?: "",
                        publisher = bookData.publisher,
                        language = bookData.language,
                        imageUri = bookData.imageUri,
                        creatives = bookWithCreatives.value.map {
                            Creative(
                                id = it.creativeId,
                                name = it.name,
                                role = CreativeRoles.valueOf(it.roles.uppercase())
                            )
                        }
                    )
                }
            value = value.copy(items = data)
        }
    }