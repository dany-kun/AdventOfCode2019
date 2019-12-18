package advent

class Day16 : Day {

    private val pattern = listOf(0, 1, 0, -1)

    override fun execute1() {
        val input = loadFile("day16.txt").first()
        val res = compute("12345678")
        println(res.take(8))
    }

    private fun compute(s: String): String {
        return (0 until 100).fold(s) { input: String, _ ->
            input.mapIndexed { index, _ -> transformInput(input, index, 1) }.joinToString("")
                    .also { println(it[0]) }
        }
    }

    private fun transformInput(input: String, row: Int, startOffset: Int): Char {
        return input.foldIndexed(0) { col, sum, c ->
            val value = c.toString().toInt()
            sum + value * pattern[((col + 1) / (row + 1)).rem(pattern.size)]
        }.toString().last()
    }

    override fun execute2() {
        val msg = loadFile("day16.txt").first()
        val input = (0 until 10000).joinToString("") { msg }
        val res = compute(input)
        println(res)
    }

}