package com.chaosnote.plugin

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.pty4j.PtyProcessBuilder
import java.nio.charset.Charset

fun createTerminal(terminalType: String): JediTermWidget? {

    try {
        val terminalWidget = JediTermWidget(settings)
        // Спроба запуску процесу
        val process = PtyProcessBuilder()
            .setCommand(arrayOf(terminalType))
            .start()

        // Якщо процес заустився, налаштовуємо конектор
        terminalWidget.ttyConnector = PtyProcessTtyConnector(process, Charset.defaultCharset())
        terminalWidget.start()
        return terminalWidget
    } catch (e: Exception) {
         return null
    }

}