package com.chaosnote.plugin

import com.jediterm.pty.PtyProcessTtyConnector
import com.jediterm.terminal.TerminalColor
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import com.pty4j.PtyProcessBuilder
import java.awt.BorderLayout
import java.nio.charset.Charset
import javax.swing.JPanel

val settings = object : DefaultSettingsProvider() {

    override fun getDefaultBackground(): TerminalColor =
        TerminalColor.BLACK

    override fun getDefaultForeground(): TerminalColor =
        TerminalColor.WHITE
}


fun createTerminalComponent(terminalType: String): JPanel {
    val frame = JPanel(BorderLayout())

    val terminal = JediTermWidget(settings)
    val process = PtyProcessBuilder()
        .setCommand(arrayOf(terminalType))
        .start()

    terminal.ttyConnector = PtyProcessTtyConnector(process, Charset.defaultCharset())
    terminal.start()
    frame.add(terminal, BorderLayout.CENTER)
    frame.isVisible = true
    return frame
}