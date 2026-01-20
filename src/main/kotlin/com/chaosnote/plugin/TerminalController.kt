package com.chaosnote.plugin

import com.jediterm.terminal.ui.JediTermWidget

class TerminalController {
    private var terminalWidget: JediTermWidget? = null

    // Прив'язуємо віджет до контролера
    fun bind(widget: JediTermWidget) {
        this.terminalWidget = widget
    }

    // Метод для відправки команди
    fun sendCommand(command: String, vars: String) {
        val variables = vars.lines()
            .mapNotNull { line ->
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) parts[0].trim() to parts[1].trim() else null
            }
            .toMap()
        var endCommand = command
        variables.forEach { (k, v) ->
            endCommand = endCommand.replace("\${$k}", v)
        }


        terminalWidget?.ttyConnector?.write("$endCommand\r")
    }
}