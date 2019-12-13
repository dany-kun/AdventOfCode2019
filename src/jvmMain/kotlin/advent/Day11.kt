package advent

class Day11 : Day {

    override fun execute1() {
        val map = drawMap(0)
        println(map.size)
    }

    private fun drawMap(start: Int): Map<Pair<Int, Int>, Int> {
        val machine = IntCodeMachine()
        var input = Instruction.Output.Input(0, loadFile("day11.txt").first().split(","),
                sequenceOf(start), 0, emptyMap())
        val instruction = mutableListOf<Int>()
        val map = mutableMapOf<Pair<Int, Int>, Int>()
        var position = 0 to 0
        var angle = 0
        loop@ while (true) {
            when (val out = machine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    val output = out.value
                    instruction.add(output.toInt())
                    if (instruction.size % 2 == 0) {
                        angle += (if (output == 0.0) 270 else if (output == 1.0) 90 else throw IllegalArgumentException("Unknown output $output"))
                        angle %= 360
                        val (dx, dy) = when (angle) {
                            0 -> 0 to -1
                            90 -> 1 to 0
                            180 -> 0 to 1
                            270 -> -1 to 0
                            else -> throw IllegalArgumentException("Unknown angle $angle")
                        }
                        position = position.first + dx to position.second + dy
                        input = Instruction.Output.Input(
                                out.input.pointerPosition,
                                out.input.sequence,
                                sequenceOf(map[position] ?: start),
                                out.input.base, out.input.extraMemory
                        )
                    } else {
                        map[position] = output.toInt()
                        input = out.input
                    }
                }
                IntCodeMachine.Result.Terminal -> break@loop
            }
        }
        return map
    }

    override fun execute2() {
        val map = drawMap(1)
        val orderedMap = map.entries.groupBy {
            it.key.second
        }.mapValues { (y, xs) ->
            xs.groupBy { it.key.first }.mapValues { it.value.first().value }
        }
        val maxY = orderedMap.keys.max()!!
        val maxX = orderedMap.values.flatMap { it.keys }.max()!!

        for (y in (0 until maxY)) {
            val row = (0 until maxX).map {
                val value = orderedMap[y]?.get(it)
                if (value == 1) "X" else " "
            }
            println(row)
        }
    }
}