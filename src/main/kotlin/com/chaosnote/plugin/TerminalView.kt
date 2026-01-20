package com.chaosnote.plugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.pty4j.PtyProcessBuilder
import java.nio.charset.Charset

@Composable
fun TerminalView(
    terminalType: String,
    controller: TerminalController, // Передаємо контролер
    modifier: Modifier = Modifier
) {
    val terminalWidget = remember { JediTermWidget(settings) }

    DisposableEffect(terminalType) {
        // Прив'язуємо віджет до контролера
        controller.bind(terminalWidget)

        val process = PtyProcessBuilder()
            .setCommand(arrayOf(terminalType))
            .start()

        terminalWidget.ttyConnector = PtyProcessTtyConnector(process, Charset.defaultCharset())
        terminalWidget.start()

        onDispose {
            terminalWidget.stop()
            process.destroy()
        }
    }

    SwingPanel(
        factory = { terminalWidget },
        modifier = modifier
    )
}