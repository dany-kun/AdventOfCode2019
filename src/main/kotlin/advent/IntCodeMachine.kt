package advent

class IntCodeMachine(
        private var instructions: List<String>,
        private var base: Int = 0) {

    sealed class Result {

        data class Output(val value: Double) : Result()
        object Terminal : Result()
    }

    private var pointerPosition = 0

    fun runInstructions(inputs: List<Int>): Result {
        var input = Instruction.Input(pointerPosition, ArrayList(instructions), inputs, base)
        loop@ while (true) {
            val out = convert(input.instruction).executeInstruction(input)
            input = when (out) {
                is Instruction.Output.Value -> {
                    println("Got output ${out.value.toInt()}")
                    pointerPosition = out.pointerPosition
                    instructions = out.updatedSequence
                    this.base = input.base
                    return Result.Output(out.value)

                }
                is Instruction.Output.PointedSequence -> Instruction.Input(out.pointerPosition, ArrayList(out.updatedSequence), out.inputs, out.base)
                Instruction.Output.Terminal -> return Result.Terminal
            }
        }
    }

    private fun convert(instructionCode: String): Instruction {
        return when (val i = instructionCode.takeLast(2).toInt()) {
            1 -> SumInstruction()
            2 -> ProductInstruction()
            3 -> SimpleInputInstruction()
            4 -> SimpleOutputInstruction()
            5 -> ShiftingInstructionNonZero()
            6 -> ShiftingInstructionZero()
            7 -> LessThanInstruction()
            8 -> EqualInstruction()
            9 -> RelativeBaseInstruction()
            99 -> TerminalInstruction()
            else -> throw IllegalArgumentException("Unknown instruction $i")
        }
    }
}
