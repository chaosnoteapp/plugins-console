package com.chaosnote.plugin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRow
import androidx.compose.material3.Button
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chaosnote.api.block.BlockHandle
import com.chaosnote.api.block.BlockPlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview


class ConsolePlugin : BlockPlugin {

    override val type: String = this.javaClass.name
    override val shortName = "Console"

    @Serializable
    data class CodePayload(
        val shell: String = "",
        val vars: String = "",
        val code: String = ""
    )

    @Preview
    @Composable
    override fun Render(blockHandle: BlockHandle) {
        // ===============================
        // Payload
        // ===============================
        var payload by remember(blockHandle.id) {
            mutableStateOf(
                runCatching { Json.decodeFromString<CodePayload>(blockHandle.payload) }
                    .getOrElse { CodePayload() }
            )
        }

        // ===============================
        // Output log
        // ===============================

        // ===============================
        // Code editor state
        // ===============================
        var codeValue by remember { mutableStateOf(TextFieldValue(payload.code)) }

        // поточний selection (може бути пустий)
        var currentSelection by remember { mutableStateOf(codeValue.selection) }

        // останній НЕ-порожній selection
        var prevSelection by remember { mutableStateOf(codeValue.selection) }

        val codeScroll = rememberScrollState()
        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Shell", "Variables", "Code")

        val terminalController = remember { TerminalController() }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF2A2A2A))
                .padding(8.dp)
        ) {

            // ===============================
            // TOP: Tabs + TextField
            // ===============================
            Column(
                Modifier.fillMaxWidth()
            ) {

                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color(0xFF1E1E1E),
                    contentColor = Color.White
                ) {
                    tabs.forEachIndexed { index, title ->
                        androidx.compose.material3.Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                when (selectedTab) {
                    0 -> ScrollableTextField(
                        value = payload.shell,
                        onValueChange = {
                            payload = payload.copy(shell = it)
                            blockHandle.update(Json.encodeToString(payload))
                        },
                        height = 56.dp
                    )

                    1 -> ScrollableTextField(
                        value = payload.vars,
                        onValueChange = {
                            payload = payload.copy(vars = it)
                            blockHandle.update(Json.encodeToString(payload))

                        },
                        height = 120.dp
                    )

                    2 -> Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A2A2A))
                            .padding(8.dp)
                    ) {
                        TextField(
                            value = codeValue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)          // 🔒 НЕ росте
                                .verticalScroll(codeScroll),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            ),
                            colors = darkTextFieldColors(),
                            onValueChange = { newValue ->

                                // Selection змінився?
                                if (newValue.selection != currentSelection) {
                                    currentSelection = newValue.selection

                                    // Якщо виділення НЕ пусте — оновлюємо prevSelection
                                    if (!newValue.selection.collapsed) {
                                        prevSelection = newValue.selection
                                    }
                                }

                                // Текст змінився
                                if (newValue.text != codeValue.text) {
                                    payload = payload.copy(code = newValue.text)
                                    blockHandle.update(Json.encodeToString(payload))
                                }

                                codeValue = newValue
                            },
                        )

                        Button(
                            onClick = {
                                val sel = prevSelection

                                val codeSelection = if (!sel.collapsed) {
                                    codeValue.text.substring(minOf(sel.start, sel.end), maxOf(sel.start, sel.end))
                                } else codeValue.text

                                if (payload.shell.isNotBlank() && codeSelection.isNotBlank()) {
                                    CoroutineScope(Dispatchers.Default).launch {
                                        terminalController.sendCommand(codeSelection, payload.vars)
                                    }
                                }

                                // ❗️Після виконання — повертаємо виділення назад
                                codeValue = codeValue.copy(selection = prevSelection)
                                currentSelection = prevSelection
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Run Selected")
                        }
                    }

                }
            }

            Spacer(Modifier.height(12.dp))

            TerminalView(
                terminalType = payload.shell,
                blockHandle = blockHandle,// або "powershell.exe"
                controller = terminalController,
                modifier = Modifier.fillMaxSize()
            )
        }

    }
}

@Composable
fun darkTextFieldColors() = TextFieldDefaults.colors(
    unfocusedContainerColor = Color(0xFF1E1E1E),
    focusedContainerColor = Color(0xFF1E1E1E),
    cursorColor = Color.White,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent
)

@Composable
fun ScrollableTextField(
    value: String,
    onValueChange: (String) -> Unit,
    height: Dp,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(color = Color.White)
) {
    val scrollState = rememberScrollState()

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(height)              // 🔒 фіксована висота
            .verticalScroll(scrollState),// 🔽 скрол
        textStyle = textStyle,
        colors = darkTextFieldColors()
    )
}