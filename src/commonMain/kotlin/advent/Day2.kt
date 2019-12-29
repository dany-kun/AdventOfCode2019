package advent

class Day2 : Day {

    override suspend fun execute1() {
        val output = computeOutput(12, 2)
        println(output)
    }

    override suspend fun execute2() {
        for (noun in 0 until 100) {
            for (verb in 0 until 100) {
                try {
                    if (computeOutput(noun, verb) == 19690720) {
                        println(100 * noun + verb)
                        break;
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    private suspend fun computeOutput(noun: Int, verb: Int): Int {
        val input = loadFile("day2.txt").first().split(",").map { it.toInt() }.toMutableList()
        val inputs = input.also {
            it[1] = noun
            it[2] = verb
        }
        for (i in 0 until inputs.size step 4) {
            val action = inputs[i]
            if (action == 99) {
                break
            };
            val firstValue = inputs[inputs[i + 1]]
            val secondValue = inputs[inputs[i + 2]]
            inputs[inputs[i + 3]] = when (action) {
                1 -> firstValue + secondValue
                2 -> firstValue * secondValue
                else -> throw IllegalArgumentException("Unknown action $action")
            }
        }
        return inputs[0]
    }
}