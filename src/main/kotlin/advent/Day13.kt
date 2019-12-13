package advent

class Day13 : Day {
    override fun execute1() {
        val input = loadFile("day13.txt").first().split(",")

        val machine = IntCodeMachine()

        var state = Instruction.Output.Input(0, input, emptyList(), 0, emptyMap())

        val outputs = mutableListOf<Int>()
        loop@ while (true) {
            when (val out = machine.runInstructions(state)) {
                is IntCodeMachine.Result.Output -> {
                    outputs.add(out.value.toInt())
                    state = out.input
                }
                IntCodeMachine.Result.Terminal -> break@loop
            }.let { }
        }
        val instructions = outputs.chunked(3)
        val result = instructions.fold(mutableMapOf<Pair<Int, Int>, Int>()) { acc, el ->
            val coord = el[0] to el[1]
            if (acc[coord] != null) {
                throw IllegalArgumentException("Position already filled $coord with ${acc[coord]}")
            }
            acc[coord] = el[2]
            acc
        }
        println(result.count { it.value == 2 })
    }

    override fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}