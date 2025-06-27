package no.northernfield.mightybookshelf.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun HomeScene(modifier: Modifier) {
    val context = LocalContext.current
    val state by homeScenePresenter()
    LazyColumn(modifier = modifier) {
        items(state.items, key = { it.id }) { book ->
            Card(modifier =
                Modifier.height(200.dp)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(book.imageUri.toUri())
                            .crossfade(true)
                            .build(),
                        contentDescription = "Book cover",
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = book.title)
                    }
                }
            }
        }
    }
}
