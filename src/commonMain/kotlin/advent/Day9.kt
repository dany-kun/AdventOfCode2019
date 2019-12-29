package advent

class Day9 : Day {
    override suspend fun execute1() {
        run(1)
    }

    private suspend fun run(input: Int) {
        val seq = loadFile("day9.txt").first().split(",")
        val intCodeMachine = IntCodeMachine()
        var input = Instruction.Output.Input(
                0,
                seq,
                SingleInput(input),
                0,
                emptyMap()
        )
        while (true) {
            when (val out = intCodeMachine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    input = out.input
                }
                IntCodeMachine.Result.Terminal -> TODO()
            }
        }
    }

    override suspend fun execute2() {
        run(2)
    }
}