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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.ui.tooling.preview.Preview


class ConsolePlugin : BlockPlugin {

    override val type: String = this.javaClass.name
    override val shortName = "Plugin Management"

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
        var output by remember { mutableStateOf("") }
        val logScroll = rememberScrollState()

        // ===============================
        // Code editor state
        // ===============================
        var codeValue by remember { mutableStateOf(TextFieldValue(payload.code)) }
        var terminalValue by remember { mutableStateOf(TextFieldValue("")) }

        // поточний selection (може бути пустий)
        var currentSelection by remember { mutableStateOf(codeValue.selection) }

        // останній НЕ-порожній selection
        var prevSelection by remember { mutableStateOf(codeValue.selection) }

        val codeScroll = rememberScrollState()

        var selectedTab by remember { mutableStateOf(0) }
        val tabs = listOf("Shell", "Variables", "Code")

        val terminalController = remember { TerminalController() }
        var commandText by remember { mutableStateOf("ls -la") }

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
                        onValueChange = { payload = payload.copy(shell = it) },
                        height = 56.dp
                    )
                    1 -> ScrollableTextField(
                        value = payload.vars,
                        onValueChange = { payload = payload.copy(vars = it) },
                        height = 120.dp
                    )

                    2 ->       Column(
                        Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF2A2A2A))
                            .padding(8.dp)
                    ) {
                        TextField(
                            value = payload.code,
                            onValueChange = { payload = payload.copy(code = it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)          // 🔒 НЕ росте
                                .verticalScroll(codeScroll),
                            textStyle = TextStyle(
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            ),
                            colors = darkTextFieldColors()
                        )

                        Button(
                            onClick = {},
                            modifier = Modifier.fillMaxWidth()
                        ){
                            Text("Run Selected")
                        }
                    }

                }
            }

            Spacer(Modifier.height(12.dp))

            TerminalView(
                terminalType = "powershell", // або "powershell.exe"
                controller = terminalController,
                modifier = Modifier.fillMaxSize()
            )

            // ===============================
            // BOTTOM: Terminal (fills rest)
            // ===============================

//            SwingPanel(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f),   // 🔥 ось тут ключ
//                factory = {
//                    createTerminalComponent(payload.shell)
//                }
//            )
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