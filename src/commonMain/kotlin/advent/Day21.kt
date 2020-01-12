package advent

class Day21 : Day {
    override suspend fun execute1() {
        // Rules:
        // If hole in 1: Jump
        // If no holes in 1,2,3,4: Do not jump
        // If holes in 4: Jump
        val cmd = """
                OR B T
                AND C T
                OR T J
                NOT D T
                OR J T
                NOT T T
                NOT A J
                OR T J
                WALK
                
            """.trimIndent()
        runProgram(cmd)
    }

    private suspend fun runProgram(cmd: String) {
        val code = loadFile("day21.txt").first().split(",")
        val intCodeMachine = IntCodeMachine()

        val outputs = mutableListOf<Double>()
        var input = Instruction.Output.Input(0,
                code,
                Inputs(cmd),
                0,
                emptyMap()
        )
        loop@ while (true) {
            input = when (val out = intCodeMachine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    outputs.add(out.value)
                    out.input
                }
                IntCodeMachine.Result.Terminal -> {
                    val output = outputs.map { if (it < 256) it.toChar() else it.toInt() }.joinToString("")
                    println(output)
                    break@loop
                }
            }
        }
    }

    override suspend fun execute2() {
        // Rules:
        // If hole in 1: Jump
        // If no holes in 1,2,3,4, 5, 6, 7, 8, 9: Do not jump
        // If holes in 4: Jump
        val cmd = """
                OR B T
                AND C T
                OR T J
                NOT D T
                OR J T
                NOT E J
                NOT J J
                OR H J
                NOT J J
                OR T J
                NOT T T
                NOT A J
                OR T J
                RUN
                
            """.trimIndent()
        runProgram(cmd)
    }

    private class Inputs(private var cmd: String) : IntCodeInput {

        override fun next(): Int {
            return cmd.first().toInt().also {
                cmd = cmd.drop(1)
            }
        }

    }
}