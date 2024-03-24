import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.seiko.imageloader.rememberImagePainter
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview

import dbgenerator.composeapp.generated.resources.Res
import dbgenerator.composeapp.generated.resources.abv
import dbgenerator.composeapp.generated.resources.category
import dbgenerator.composeapp.generated.resources.country
import dbgenerator.composeapp.generated.resources.image_url
import dbgenerator.composeapp.generated.resources.load
import dbgenerator.composeapp.generated.resources.name_en
import dbgenerator.composeapp.generated.resources.name_ko
import dbgenerator.composeapp.generated.resources.save
import model.Whisky
import org.jetbrains.compose.resources.stringResource

@Composable
@Preview
fun App() {
    MaterialTheme {
        Row {
            Input(
                modifier = Modifier.width(500.dp).fillMaxHeight(),
                onClickSave = {}
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun Input(
    modifier: Modifier,
    onClickSave: (Whisky) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(
            rememberScrollState()
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        var koName by remember { mutableStateOf("") }
        var enName by remember { mutableStateOf("") }
        var inputImageUrl by remember { mutableStateOf("") }
        var originImageUrl by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var abv by remember { mutableStateOf("") }

        val painter = rememberImagePainter(originImageUrl)


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
            onClick = {
                val id = "sdsadads"
                val whisky = Whisky(
                    id = id,
                    koName = koName,
                    enName = enName,
                    country = country,
                    category = category,
                    abv = abv,
                    originImageUrl = originImageUrl,
                )
                onClickSave.invoke(whisky)
            }
        ) {
            Text(stringResource(Res.string.save))
        }
    }
}