package com.chaosnote.plugin

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.chaosnote.api.block.BlockHandle


val payload = """
    {
        "shell": "powershell",
        "vars": "test=HelloWorld",
        "code": "echo ${"$"}{test}"
    }
""".trimIndent()

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Chaosnote Plugin Store") {
        MaterialTheme {
            ConsolePlugin().Render(FakeBlockHandle(payload))
        }
    }
}

//fun main() {
//   Terminal()
//}



class FakeBlockHandle(override val payload: String) : BlockHandle {
    override val id: String = "plugin_management"
    override fun update(value: String) {
        println("Updated payload: $value")
    }

    var globalState: Object? = null

    override fun saveState(state: Object) {
        globalState = state
    }

    override fun getState(): Object? {
        return globalState
    }


}