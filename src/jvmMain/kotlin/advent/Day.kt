package advent

import java.io.File

interface Day {

    fun execute1()

    fun execute2()

    fun loadFile(name: String): List<String> {
        return File("src/jvmMain/resources/$name").readText().split("\n")
    }
}