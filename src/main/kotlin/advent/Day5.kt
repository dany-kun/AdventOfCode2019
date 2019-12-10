package advent

class Day5 : Day {
    override fun execute1() {
        val input = 1
        traverse(input)
    }

    private fun traverse(input: Int) {
        val results = IntCodeMachine(loadFile("day5.txt").first()
                .split(",")
                .map { it.toInt() }).runInstructions(
                listOf(input))
        println(results)
    }

    override fun execute2() {
        traverse(5)
    }
}