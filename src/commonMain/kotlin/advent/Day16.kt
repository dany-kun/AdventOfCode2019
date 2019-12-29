package advent

class Day16 : Day {

    private val pattern = listOf(0, 1, 0, -1)

    override suspend fun execute1() {
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

    override suspend fun execute2() {
        val input = loadFile("day16.txt").first()
        val s = "02935109699940807407585447034323"
        val msg = (0 until 10000).joinToString("") { s }
        // The rule for the last digit after half the string length is easier since all matrices coefficients == 1
        require(msg.take(7).toInt() > msg.length / 2)
        val start = msg.drop(msg.take(7).toInt()).reversed()

        // Recipe is to take the value at position i - 1 of current phase and add it to position i of previous phase
        // -> Empirical equation: f(phase, index) = f(phase - 1, index) + f(phase, index - 1)
        // This gives us the formula f(phase, index) = SUM[(phase^(index - k) * f(o, k)] with k from 0 to index
        // Using the fact that f(phase, 0) is constant over phase

        println("Sum: ${s.sumBy { it.toString().toInt() }}")
        // Working but not efficient
        val result = (0 until 100).fold(start) { reversedInput, _ ->
            // Recipe is to take the value at position i - 1 of current phase and add it to position i of previous phase
            println("${reversedInput[0]} - ${reversedInput[s.length]} - ${reversedInput[s.length * 2]} - ${reversedInput[s.length * 3]} - ${reversedInput[s.length * 4]}")
            reversedInput.fold("") { builtString, newChar ->
                val previouslyBuiltItem = builtString.lastOrNull() ?: return@fold newChar.toString()
                val newValue = previouslyBuiltItem.toString().toInt() + newChar.toString().toInt()
                builtString + newValue.toString().last()
            }
        }
//        println(result.take(10))
    }

}