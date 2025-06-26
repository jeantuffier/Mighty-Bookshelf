package no.northernfield.mightybookshelf.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.northernfield.mightybookshelf.LocalBackStack
import no.northernfield.mightybookshelf.R
import no.northernfield.mightybookshelf.pop
import no.northernfield.mightybookshelf.ui.theme.MightyBookshelfTheme

@Composable
fun AddScene(modifier: Modifier, navigateToCamera: () -> Unit) {
    val eventBus = addSceneEventBus()
    val state by addScenePresenter(events = eventBus.events)
    AddSceneContent(
        modifier = modifier,
        state = state,
        navigateToCamera = navigateToCamera,
        onTypeChanged = { eventBus.produceEvent(AddSceneEvents.BookTypeChanged(it)) },
        onTitleChanged = { eventBus.produceEvent(AddSceneEvents.TitleChanged(it)) },
        onChangeCreatives = { index, creative ->
            eventBus.produceEvent(AddSceneEvents.CreativesChanged(index, creative))
        },
        onRemoveCreative = { eventBus.produceEvent(AddSceneEvents.CreativeRemoved) },
        onAddCreative = { eventBus.produceEvent(AddSceneEvents.CreativesAdded) },
        onRewardChanged = { eventBus.produceEvent(AddSceneEvents.RewardChanged(it)) },
        onQuoteChanged = { eventBus.produceEvent(AddSceneEvents.QuoteChanged(it)) },
        onPublisherChanged = { eventBus.produceEvent(AddSceneEvents.PublisherChanged(it)) },
        onLanguageChanged = { eventBus.produceEvent(AddSceneEvents.LanguageChanged(it)) },
        onSaveClicked = { eventBus.produceEvent(AddSceneEvents.SaveClicked) },
    )
}

@Composable
fun AddSceneContent(
    modifier: Modifier,
    state: AddSceneState,
    navigateToCamera: () -> Unit,
    onTypeChanged: (BookType) -> Unit,
    onTitleChanged: (String) -> Unit,
    onChangeCreatives: (Int, Creative) -> Unit,
    onRemoveCreative: () -> Unit,
    onAddCreative: () -> Unit,
    onRewardChanged: (String) -> Unit,
    onQuoteChanged: (String) -> Unit,
    onPublisherChanged: (String) -> Unit,
    onLanguageChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            val backstack = LocalBackStack.current
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = { backstack.pop() }) {
                Icon(
                    modifier = Modifier.padding(start = 16.dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
            Text(
                text = "Add a book",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.align(Alignment.Center),
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            FilterChip(
                onClick = { onTypeChanged(BookType.BOOK) },
                label = { Text("Book") },
                selected = state.type == BookType.BOOK,
            )
            Spacer(Modifier.width(16.dp))
            FilterChip(
                onClick = { onTypeChanged(BookType.COMIC_BOOK) },
                label = { Text("Comic book") },
                selected = state.type == BookType.COMIC_BOOK,
            )
        }
        AddSceneOutlinedTextField(state.title, "Title", onTitleChanged)

        if (state.type == BookType.COMIC_BOOK) {
            ComicBookCreatives(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                state = state,
                onChangeCreatives = onChangeCreatives,
                onRemoveCreative = onRemoveCreative,
                onAddCreative = onAddCreative,
            )
        } else {
            AddSceneOutlinedTextField(
                state.creatives.firstOrNull { it.role == CreativeRoles.AUTHOR }?.name ?: "",
                "Author"
            ) {
                onChangeCreatives(0, Creative(CreativeRoles.AUTHOR, it))
            }
        }

        AddSceneOutlinedTextField(state.reward, "Reward", onRewardChanged)
        AddSceneOutlinedTextField(state.quote, "Quote", onQuoteChanged)
        AddSceneOutlinedTextField(state.publisher, "Publisher", onPublisherChanged)
        AddSceneOutlinedTextField(state.language, "Language", onLanguageChanged)
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            Button(
                modifier = Modifier
                    .width(154.dp)
                    .height(74.dp),
                onClick = onSaveClicked,
                shape = RoundedCornerShape(6.dp),
            ) {
                Text(text = "Save", style = MaterialTheme.typography.titleSmall)
            }
            Spacer(Modifier.width(16.dp))
            Button(
                modifier = Modifier
                    .width(74.dp)
                    .height(74.dp),
                onClick = navigateToCamera,
                shape = RoundedCornerShape(6.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_a_photo),
                    contentDescription = "Open camera button icon"
                )
            }
        }
    }
}

@Composable
fun AddSceneOutlinedTextField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        value = value,
        placeholder = {
            Text(
                modifier = Modifier.alpha(0.8f),
                text = placeholder,
                style = MaterialTheme.typography.bodySmall,
            )
        },
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyMedium,
    )
}

@Preview(showBackground = true)
@Composable
private fun AddSceneContentPreview() {
    var type by remember { mutableStateOf(BookType.COMIC_BOOK) }
    MightyBookshelfTheme {
        Surface {
            AddSceneContent(
                modifier = Modifier,
                state = AddSceneState(
                    type = type,
                    title = "Title",
                    creatives = listOf(),
                    reward = "Reward",
                    quote = "Quote",
                    publisher = "Publisher",
                    language = "Language",
                    imageUri = "uri",
                    error = null
                ),
                onTypeChanged = { type = it },
                navigateToCamera = {},
                onTitleChanged = {},
                onChangeCreatives = { _, _ -> },
                onRemoveCreative = {},
                onAddCreative = {},
                onRewardChanged = {},
                onQuoteChanged = {},
                onPublisherChanged = {},
                onLanguageChanged = {},
                onSaveClicked = {},
            )
        }
    }
}
