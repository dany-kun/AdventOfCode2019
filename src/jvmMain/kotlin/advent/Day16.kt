package advent

class Day16 : Day {

    private val pattern = listOf(0, 1, 0, -1)

    override fun execute1() {
        val res = compute(loadFile("day16.txt").first())
        println(res.take(8))
    }

    private fun compute(s: String): String {
        return (0 until 100).fold(s) { input: String, _ ->
            input.mapIndexed { index, _ -> transformInput(input, index, 1) }.joinToString("")
        }
    }

    private fun transformInput(input: String, row: Int, startOffset: Int): Char {
        val updatedPattern = pattern.flatMap { v -> (0..row).map { v } }
        return input.foldIndexed(0) { col, sum, c ->
            val value = c.toString().toInt()
            sum + value * updatedPattern[(col + startOffset) % updatedPattern.size]
        }.toString().last()
    }

    override fun execute2() {
        val msg = loadFile("day16.txt").first()
        val input = (0 until 10000).joinToString("") { msg }
        val res = compute(input)
        println(res)
    }

}