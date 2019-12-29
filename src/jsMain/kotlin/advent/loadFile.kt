package advent

import kotlinx.coroutines.await
import kotlin.browser.window

actual suspend fun Day.loadFile(name: String): List<String> {
    return window.fetch(name).then { it.text() }.then { it.lines() }.await()
}