package advent

class Day11 : Day {
    override fun execute1() {
        val machine = IntCodeMachine()
        var input = Instruction.Output.Input(0, loadFile("day11.txt").first().split(","),
                listOf(0), 0, emptyMap())
        val instruction = mutableListOf<Int>()
        val map = mutableMapOf<Pair<Int, Int>, Int>()
        var position = 0 to 0
        while (true) {
            when (val out = machine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    instruction.add(out.value.toInt())
                    if (instruction.size == 2) {
                        position = if (out.value == 0.0) position.first - 1 to position.second else
                        input = Instruction.Output.Input(
                                out.input.pointerPosition,
                                out.input.sequence,
                                instruction, out.input.base, out.input.extraMemory
                        )
                        instruction.clear()
                    } else {
                        map[position] = out.value.toInt()
                        input = out.input
                    }
                }
                IntCodeMachine.Result.Terminal -> TODO()
            }
        }
    }

    override fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}