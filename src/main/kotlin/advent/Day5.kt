package advent

class Day5 : Day {
    override fun execute1() {
        val input = 1
        traverse(input)
    }

    private fun traverse(input: Int) {
        val values = loadFile("day5.txt").first()
                .split(",").map { it.toInt() }
        val sequence = values.toMutableList()

        var index = 0

        while (index < sequence.size) {
            val instruction = "${sequence[index]}"
            when (instruction.takeLast(2).toInt()) {
                1 -> {
                    val firstParameter = parameter(instruction, sequence, index, 1)
                    val secondParameter = parameter(instruction, sequence, index, 2)
                    val outputAddress = sequence[index + 3]
                    sequence[outputAddress] = firstParameter + secondParameter
                    index += 4
                }
                2 -> {
                    val firstParameter = parameter(instruction, sequence, index, 1)
                    val secondParameter = parameter(instruction, sequence, index, 2)
                    val outputAddress = sequence[index + 3]
                    sequence[outputAddress] = firstParameter * secondParameter
                    index += 4
                }
                3 -> {
                    val address = sequence[index + 1]
                    sequence[address] = input
                    index += 2
                }
                4 -> {
                    val address = sequence[index + 1]
                    println(sequence[address])
                    index += 2
                }
                5 -> {
                    val firstParam = parameter(instruction, sequence, index, 1)
                    if (firstParam != 0) {
                        index = parameter(instruction, sequence, index, 2)
                    } else {
                        index += 3
                    }
                }
                6 -> {
                    val firstParam = parameter(instruction, sequence, index, 1)
                    if (firstParam == 0) {
                        index = parameter(instruction, sequence, index, 2)
                    } else {
                        index += 3
                    }
                }
                7 -> {
                    val firstParam = parameter(instruction, sequence, index, 1)
                    val secondParam = parameter(instruction, sequence, index, 2)
                    sequence[sequence[index + 3]] = if (firstParam < secondParam) 1 else 0
                    index += 4
                }
                8 -> {
                    val firstParam = parameter(instruction, sequence, index, 1)
                    val secondParam = parameter(instruction, sequence, index, 2)
                    sequence[sequence[index + 3]] = if (firstParam == secondParam) 1 else 0
                    index += 4
                }
                99 -> TODO()
            }
        }
    }

    private fun parameter(instruction: String, sequence: MutableList<Int>, index: Int, offset: Int): Int {
        return when (val firstParameterMode = instruction.getOrNull(instruction.length - 2 - offset) ?: '0') {
            '0' -> sequence[sequence[index + offset]]
            '1' -> sequence[index + offset]
            else -> throw IllegalArgumentException("Unknown mode $firstParameterMode")
        }
    }

    override fun execute2() {
        traverse(5)
    }
}