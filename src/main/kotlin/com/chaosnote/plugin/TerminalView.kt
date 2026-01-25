package com.chaosnote.plugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.chaosnote.api.block.BlockHandle
import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.TerminalColor
import com.jediterm.terminal.TextStyle
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcessBuilder
import java.nio.charset.Charset


val settings = object : DefaultSettingsProvider() {

    override fun getDefaultStyle(): TextStyle {
        // Встановлюємо білий текст на чорному фоні
        return TextStyle(
            TerminalColor.WHITE, // Колір тексту (Foreground)
            TerminalColor.BLACK  // Колір фону (Background)
        )
    }
}


@Composable
fun TerminalView(
    terminalType: String,
    blockHandle: BlockHandle,
    controller: TerminalController,
    modifier: Modifier = Modifier
) {
    val terminalWidget = remember(blockHandle.id, terminalType) {
        val existing = blockHandle.getState() as? JediTermWidget
        if (existing != null) return@remember existing
        val newTerminal = createTerminal(terminalType)
        newTerminal?.also {
            blockHandle.saveState(it as Object) // Зберігаємо як Any/Object
        }
    }

    if(terminalWidget != null) {
        controller.bind(terminalWidget)
        SwingPanel(
            factory = { terminalWidget },
            modifier = modifier
        )
    }
}