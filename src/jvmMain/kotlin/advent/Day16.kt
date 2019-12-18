package advent

class Day16 : Day {

    private val pattern = listOf(0, 1, 0, -1)

    override fun execute1() {
        val input = loadFile("day16.txt").first()
        val res = compute(input)
        println(res.take(8))
    }

    private fun compute(s: String): String {
        return (0 until 100).fold(s) { input: String, _ ->
            input.mapIndexed { index, _ -> transformInput(input, index) }.joinToString("")
                    .also { println(it[0]) }
        }
    }

    private fun transformInput(input: String, row: Int): Char {
        return input.foldIndexed(0) { col, sum, c ->
            val value = c.toString().toInt()
            sum + value * pattern[((col + 1) / (row + 1)).rem(pattern.size)]
        }.toString().last()
    }

    override fun execute2() {
        val input = loadFile("day16.txt").first()
        val msg = (0 until 10000).joinToString("") { input /* "02935109699940807407585447034323" */ }
        // The rule for the last digit after half the string length is easier since all matrices coefficients == 1
        require(msg.take(7).toInt() > msg.length / 2)
        val start = msg.drop(msg.take(7).toInt()).reversed()

        // Recipe is to take the value at position i - 1 of current phase and add it to position i of previous phase
        // -> Empirical equation: f(phase, index) = f(phase - 1, index) + f(phase, index - 1)

//        Working but not efficient
        val result = (0 until 100).fold(start) { reversedInput, _ ->
            println("new phase")
            // Recipe is to take the value at position i - 1 of current phase and add it to position i of previous phase
            reversedInput.fold("") { builtString, newChar ->
                val previouslyBuiltItem = builtString.lastOrNull() ?: return@fold newChar.toString()
                val newValue = previouslyBuiltItem.toString().toInt() + newChar.toString().toInt()
                builtString + newValue.toString().last()
            }
        }

        println(result.reversed().take(8))
    }

}