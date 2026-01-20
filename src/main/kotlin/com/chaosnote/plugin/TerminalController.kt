package com.chaosnote.plugin

import com.jediterm.terminal.ui.JediTermWidget

class TerminalController {
    private var terminalWidget: JediTermWidget? = null

    // Прив'язуємо віджет до контролера
    fun bind(widget: JediTermWidget) {
        this.terminalWidget = widget
    }

    // Метод для відправки команди
    fun sendCommand(command: String) {
        val connector = terminalWidget?.ttyConnector
        if (connector != null) {
            // Додаємо \n, щоб команда спрацювала (як натискання Enter)
            connector.write("$command\n")
        }
    }
}