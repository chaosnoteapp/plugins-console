package com.chaosnote.plugin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.chaosnote.api.block.BlockHandle
import com.chaosnote.api.block.BlockPlugin
import kotlinx.coroutines.flow.debounce

class BasicTextPlugin: BlockPlugin {

    override val type: String = "com.chaosnote.plugin.BasicTextPlugin"
    override val shortName: String = "Text Block"

    @Composable
    override fun Render(blockHandle: BlockHandle) {
        var text by remember { mutableStateOf(blockHandle.payload) }

        LaunchedEffect(blockHandle.id) {
            snapshotFlow { text }
                .debounce(2000)
                .collect { latest ->
                    blockHandle.update(latest)
                }
        }

        BasicTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
            },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxSize(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {})
        )
    }

}