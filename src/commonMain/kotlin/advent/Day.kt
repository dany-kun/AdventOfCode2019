package advent

interface Day {

    suspend fun execute1()

    suspend fun execute2()
}

expect suspend fun Day.loadFile(name: String): List<String>
