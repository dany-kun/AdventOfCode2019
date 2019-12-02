package advent

interface Day {

    fun execute1()

    fun execute2()

    fun loadFile(name: String): List<String> {
        return Day::class.java.classLoader.getResource(name).readText().split("\n")
    }
}