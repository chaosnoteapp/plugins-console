package com.chaosnote.plugin

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.TerminalColor
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcessBuilder
import java.nio.charset.Charset


val settings = object : DefaultSettingsProvider() {

    override fun getDefaultBackground(): TerminalColor =
        TerminalColor.BLACK

    override fun getDefaultForeground(): TerminalColor =
        TerminalColor.WHITE
}


@Composable
fun TerminalView(
    terminalType: String,
    controller: TerminalController,
    modifier: Modifier = Modifier
) {
    // Стан, який визначає, чи вдалося успішно ініціалізувати термінал
    var isInitialized by remember { mutableStateOf(false) }

    // Створюємо віджет (він легкий, поки не запущений процес)
    val terminalWidget = remember { JediTermWidget(settings) }

    DisposableEffect(terminalType) {
        var process: Process? = null

        try {
            // Спроба запуску процесу
            process = PtyProcessBuilder()
                .setCommand(arrayOf(terminalType))
                .start()

            // Якщо процес заустився, налаштовуємо конектор
            terminalWidget.ttyConnector = PtyProcessTtyConnector(process, Charset.defaultCharset())
            terminalWidget.start()

            // Тільки тепер прив'язуємо контролер і показуємо UI
            controller.bind(terminalWidget)
            isInitialized = true

        } catch (e: Exception) {
            // Якщо сталася помилка (наприклад, IOException: Cannot run program)
            println("Не вдалося запустити термінал: ${e.message}")
            isInitialized = false
        }

        onDispose {
            isInitialized = false
            terminalWidget.stop() // Використовуйте stop() замість close(), якщо це JediTerm
            process?.destroy()
        }
    }

    // Відображаємо SwingPanel тільки якщо ініціалізація пройшла успішно
    if (isInitialized) {
        SwingPanel(
            factory = { terminalWidget },
            modifier = modifier
        )
    }
}