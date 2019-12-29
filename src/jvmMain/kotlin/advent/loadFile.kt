package advent

import java.io.File

actual suspend fun Day.loadFile(name: String): List<String> {
    return File("src/commonMain/resources/$name").readText().split("\n")
}