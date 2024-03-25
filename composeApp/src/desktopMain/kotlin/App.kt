import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

import dbgenerator.composeapp.generated.resources.Res
import dbgenerator.composeapp.generated.resources.abv
import dbgenerator.composeapp.generated.resources.category
import dbgenerator.composeapp.generated.resources.country
import dbgenerator.composeapp.generated.resources.delete
import dbgenerator.composeapp.generated.resources.export
import dbgenerator.composeapp.generated.resources.image_url
import dbgenerator.composeapp.generated.resources.load
import dbgenerator.composeapp.generated.resources.modify
import dbgenerator.composeapp.generated.resources.name_en
import dbgenerator.composeapp.generated.resources.name_ko
import dbgenerator.composeapp.generated.resources.save
import dbgenerator.composeapp.generated.resources.storage_dir_input_label
import model.Whisky
import org.jetbrains.compose.resources.stringResource
import java.util.UUID

@Composable
@Preview
fun App() {

    var whiskies by remember { mutableStateOf(listOf<Whisky>()) }
    var selectedWhisky by remember { mutableStateOf<Whisky?>(null) }

    MaterialTheme {
        Row {
            Input(
                modifier = Modifier.width(500.dp).fillMaxHeight(),
                whisky = selectedWhisky,
                onClickSave = { whisky ->
                    whiskies = whiskies.toMutableList().apply {
                        removeIf { it.id == whisky.id }
                        add(whisky)
                    }.toList()
                    selectedWhisky = null
                }
            )
            WhiskyList(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                whiskies = whiskies.toList(),
                onClickRemove = { index ->
                    whiskies = whiskies.toMutableList().apply {
                        removeAt(index)
                    }.toList()
                },
                onClickMofidy = { index ->
                    selectedWhisky = whiskies[index]
                }
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun WhiskyList(
    modifier: Modifier,
    whiskies: List<Whisky>,
    onClickRemove: (index: Int) -> Unit,
    onClickMofidy: (index: Int) -> Unit,
) {
    var storageDir by remember { mutableStateOf(getCurrentDate()) }

    val scrollState = rememberLazyListState()
    LazyColumn(
        modifier = modifier,
        state = scrollState,
    ) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextField(
                    modifier = Modifier.weight(1f),
                    value = storageDir,
                    label = {
                        Text(
                            text = stringResource(Res.string.storage_dir_input_label),
                            fontSize = 10.sp
                        )
                    },
                    maxLines = 1,
                    onValueChange = { storageDir = it })

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = { }
                ) {
                    Text(text = stringResource(Res.string.export))
                }
            }
        }
        itemsIndexed(
            items = whiskies,
            key = { index: Int, item: Whisky -> "${index}_${item.id}" }
        ) { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onClickRemove.invoke(index)
                    }
                ) {
                    Text(
                        fontSize = 12.sp,
                        text = stringResource(Res.string.delete)
                    )
                }
                Button(
                    onClick = {
                        onClickMofidy.invoke(index)
                    }
                ) {
                    Text(
                        fontSize = 12.sp,
                        text = stringResource(Res.string.modify)
                    )
                }
                Text(
                    text = "${index + 1}. " + item.toString(),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (index < whiskies.size - 1) {
                Divider(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    thickness = 1.dp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Input(
    whisky: Whisky? = null,
    modifier: Modifier,
    onClickSave: (Whisky) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(
            rememberScrollState()
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        var koName by remember(whisky) { mutableStateOf(whisky?.koName.orEmpty()) }
        var enName by remember(whisky) { mutableStateOf(whisky?.enName.orEmpty()) }
        var inputImageUrl by remember(whisky) { mutableStateOf(whisky?.originImageUrl.orEmpty()) }
        var originImageUrl by remember(whisky) { mutableStateOf("") }
        var country by remember(whisky) { mutableStateOf(whisky?.country.orEmpty()) }
        var category by remember(whisky) { mutableStateOf(whisky?.category.orEmpty()) }
        var abv by remember(whisky) { mutableStateOf(whisky?.abv.orEmpty()) }

        val painter = rememberImagePainter(originImageUrl)

        val saveButtonEnable by remember(whisky) {
            derivedStateOf {
                listOf(
                    koName,
                    enName,
                    originImageUrl,
                    country,
                    category,
                    abv
                ).all { it.isNotEmpty() }
            }
        }

        val innerModifier = Modifier.fillMaxWidth()
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(250.dp),
                    painter = painter,
                    contentDescription = ""
                )
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        originImageUrl = inputImageUrl
                    },
                ) {
                    Text(stringResource(Res.string.load))
                }
            }
        }
        TextField(
            modifier = innerModifier,
            value = inputImageUrl,
            label = {
                Text(text = stringResource(Res.string.image_url))
            },
            onValueChange = { inputImageUrl = it },
        )
        TextField(
            modifier = innerModifier,
            value = koName,
            label = {
                Text(text = stringResource(Res.string.name_ko))
            },
            onValueChange = { koName = it }
        )
        TextField(
            modifier = innerModifier,
            value = enName,
            label = {
                Text(text = stringResource(Res.string.name_en))
            },
            onValueChange = { enName = it })
        TextField(
            modifier = innerModifier,
            value = country,
            label = {
                Text(text = stringResource(Res.string.country))
            },
            onValueChange = { country = it })
        TextField(
            modifier = innerModifier,
            value = category,
            label = {
                Text(text = stringResource(Res.string.category))
            },
            onValueChange = { category = it })
        TextField(
            modifier = innerModifier,
            value = abv,
            label = {
                Text(text = stringResource(Res.string.abv))
            },
            onValueChange = { abv = it }
        )

        Button(
            modifier = innerModifier,
            enabled = saveButtonEnable,
            onClick = {
                val w = whisky?.copy(
                    koName = koName,
                    enName = enName,
                    country = country,
                    category = category,
                    abv = abv,
                    originImageUrl = originImageUrl,
                ) ?: Whisky(
                    id = UUID.randomUUID().toString(),
                    koName = koName,
                    enName = enName,
                    country = country,
                    category = category,
                    abv = abv,
                    originImageUrl = originImageUrl,
                )
                onClickSave.invoke(w)

                koName = ""
                enName = ""
                country = ""
                category = ""
                abv = ""
                originImageUrl = ""
                inputImageUrl = ""
            }
        ) {
            Text(stringResource(Res.string.save))
        }
    }
}