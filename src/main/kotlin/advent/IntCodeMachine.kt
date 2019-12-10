package advent

class IntCodeMachine {

    sealed class Result {

        data class Output(val value: Double, val input: Instruction.Output.Input) : Result()
        object Terminal : Result()
    }

    fun runInstructions(input1: Instruction.Output.Input): Result {
        var input = input1
        loop@ while (true) {
            input = when (val out = input.instruction.executeInstruction(input)) {
                is Instruction.Output.Value -> {
                    println("Got output ${out.value}")
                    return Result.Output(out.value, out.input)
                }
                Instruction.Output.Terminal -> return Result.Terminal
                is Instruction.Output.Input -> out
            }
        }
    }

}
