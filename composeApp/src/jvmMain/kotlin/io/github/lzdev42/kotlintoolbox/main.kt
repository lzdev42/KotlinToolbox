package io.github.lzdev42.kotlintoolbox

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "kotlintoolbox",
    ) {
        App()
    }
}