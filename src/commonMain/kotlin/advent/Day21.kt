package advent

class Day21 : Day {
    override suspend fun execute1() {
        // Rules:
        // If hole in 1: Jump
        // If no holes in 1,2,3,4: Do not jump
        // If holes in 4: Jump
        val cmd = """
                NOT B T
                NOT C J
                OR T J
                AND D J
                NOT A T
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
        // Logic to simplify (beginning same as ex 1)
        // If hole in A -> Jump
        // Elif no hole in B & C -> dont jump
        // Elif hole in D -> dont jump
        // Elif no hole in E & H -> dont jump
        // -> !A || ( (!B || !C) && D && (E || H) )
        val cmd = """
                NOT B T
                NOT C J
                OR T J
                AND D J
                NOT E T
                NOT T T
                OR H T
                AND T J
                NOT A T
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