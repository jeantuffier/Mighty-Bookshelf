package no.northernfield.mightybookshelf.add

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import no.northernfield.mightybookshelf.ui.theme.MightyBookshelfTheme

@Composable
fun ComicBookCreatives(
    modifier: Modifier = Modifier,
    state: AddSceneState,
    onAddCreative: () -> Unit,
    onChangeCreatives: (Int, Creative) -> Unit,
    onRemoveCreative: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        state.creatives.forEachIndexed { index, creative ->
            var expanded by remember { mutableStateOf(false) }
            val placeholder = creative.role.displayName()
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = creative.name,
                    placeholder = {
                        Text(
                            modifier = Modifier.alpha(0.8f),
                            text = placeholder,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    onValueChange = { onChangeCreatives(index, Creative(creative.role, it)) },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    trailingIcon = {
                        Box {
                            TextButton(onClick = { expanded = !expanded }) {
                                Row {
                                    Text(creative.role.displayName())
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = "Localized description",
                                        Modifier.size(InputChipDefaults.IconSize)
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                CreativeRoles.entries.forEach {
                                    DropdownMenuItem(
                                        text = { Text(it.displayName()) },
                                        onClick = {
                                            onChangeCreatives(index, Creative(it, creative.name))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
                if (index > 0 && index == state.creatives.size - 1) {
                    IconButton(onClick = onRemoveCreative) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove creative role",
                        )
                    }
                }
            }
            if (index == state.creatives.size - 1) {
                IconButton(
                    onClick = {
                        if (creative.name.isNotEmpty()) {
                            onAddCreative()
                        }
                    },
                    enabled = creative.name.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add creative role",
                    )
                }
            } else Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ComicBookCreativesPreview() {
    var type by remember { mutableStateOf(BookType.COMIC_BOOK) }
    MightyBookshelfTheme {
        Surface {
            ComicBookCreatives(
                state = AddSceneState(
                    type = type,
                    title = "Title",
                    creatives = listOf(),
                    reward = "Reward",
                    quote = "Quote",
                    publisher = "Publisher",
                    language = "Language",
                    error = null
                ),
                onChangeCreatives = { _, _ -> },
                onRemoveCreative = {},
                onAddCreative = {},
            )
        }
    }
}
