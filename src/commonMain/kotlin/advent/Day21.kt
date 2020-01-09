package advent

class Day21 : Day {
    override suspend fun execute1() {
        val code = loadFile("day21.txt").first().split(",")
        val intCodeMachine = IntCodeMachine()

        val outputs = mutableListOf<Double>()
        var input = Instruction.Output.Input(0,
                code,
                Inputs(outputs),
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
                    val output = outputs.map { it.toChar() }.joinToString("")
                    println(output)
                    break@loop
                }
            }
        }

    }

    override suspend fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private class Inputs(private val outputs: List<Double>) : IntCodeInput {

        private var cmd = """
                NOT A J
                NOT D T
                NOT T T
                OR T J
                WALK
                
            """.trimIndent()

        override fun next(): Int {
            return cmd.first().toInt().also {
                cmd = cmd.drop(1)
            }
        }

    }
}