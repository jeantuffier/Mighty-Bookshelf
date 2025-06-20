package no.northernfield.mightybookshelf.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.northernfield.mightybookshelf.R
import no.northernfield.mightybookshelf.ui.theme.MightyBookshelfTheme

@Composable
fun AddScene(modifier: Modifier, navigateToCamera: () -> Unit) {
    val eventBus = addSceneEventBus()
    val state by addScenePresenter(events = eventBus.events)
    AddSceneContent(
        modifier = modifier,
        state = state,
        navigateToCamera = navigateToCamera,
        onTitleChanged = { eventBus.produceEvent(AddSceneEvents.TitleChanged(it)) },
        onAuthorChanged = { eventBus.produceEvent(AddSceneEvents.AuthorChanged(it)) },
        onRewardChanged = { eventBus.produceEvent(AddSceneEvents.RewardChanged(it)) },
        onQuoteChanged = { eventBus.produceEvent(AddSceneEvents.QuoteChanged(it)) },
        onPublisherChanged = { eventBus.produceEvent(AddSceneEvents.PublisherChanged(it)) },
        onSaveClicked = { eventBus.produceEvent(AddSceneEvents.SaveClicked) },
    )
}

@Composable
fun AddSceneContent(
    modifier: Modifier,
    state: AddSceneState,
    navigateToCamera: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onAuthorChanged: (String) -> Unit,
    onRewardChanged: (String) -> Unit,
    onQuoteChanged: (String) -> Unit,
    onPublisherChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text("Add a book", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(24.dp))
        AddSceneOutlinedTextField(state.title, "Title", onTitleChanged)
        AddSceneOutlinedTextField(state.author, "Author", onAuthorChanged)
        AddSceneOutlinedTextField(state.reward, "Reward", onRewardChanged)
        AddSceneOutlinedTextField(state.quote, "Quote", onQuoteChanged)
        AddSceneOutlinedTextField(state.publisher, "Publisher", onPublisherChanged)
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
    MightyBookshelfTheme {
        Surface {
            AddSceneContent(
                modifier = Modifier,
                state = AddSceneState("Title", "Author", "Reward", "Quote", "Publisher", null),
                navigateToCamera = {},
                onTitleChanged = {},
                onAuthorChanged = {},
                onRewardChanged = {},
                onQuoteChanged = {},
                onPublisherChanged = {},
                onSaveClicked = {},
            )
        }
    }
}
