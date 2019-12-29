package advent

interface Instruction {

    sealed class Output {

        object Terminal : Output()

        data class Value(val value: Double, val input: Input) : Output()

        data class Input(
                val pointerPosition: Int,
                val sequence: List<String>,
                val inputValues: IntCodeInput,
                val base: Int,
                val extraMemory: Map<Int, String>) : Output() {

            private fun getAt(index: Int): Double {
                return (sequence.getOrNull(index) ?: extraMemory[index] ?: "0").toDouble()
            }

            private val code = sequence[pointerPosition].toDouble().toInt().toString()

            val instruction: Instruction
                get() = when (val i = code.takeLast(2).toInt()) {
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


            fun updateFromParamPosition(offset: Int, value: String): Pair<List<String>, Map<Int, String>> {
                val param = writeParameter(offset)
                return if (param < sequence.size) {
                    ArrayList(sequence).apply { set(param, value) } to extraMemory
                } else {
                    sequence to HashMap(extraMemory).apply { set(param, value) }
                }
            }

            fun parameter(offset: Int): Double {
                return when (val firstParameterMode = code.getOrNull(code.length - 2 - offset) ?: '0') {
                    '0' -> getAt(getAt(pointerPosition + offset).toInt())
                    '1' -> getAt(pointerPosition + offset)
                    '2' -> getAt(getAt(pointerPosition + offset).toInt() + base)
                    else -> throw IllegalArgumentException("Unknown mode $firstParameterMode")
                }
            }

            private fun writeParameter(offset: Int): Int {
                return when (val firstParameterMode = code.getOrNull(code.length - 2 - offset) ?: '0') {
                    '0' -> getAt(pointerPosition + offset).toInt()
                    '2' -> getAt(pointerPosition + offset).toInt() + base
                    else -> throw IllegalArgumentException("Unknown mode $firstParameterMode")
                }
            }
        }
    }

    fun executeInstruction(input: Output.Input): Output
}

class SumInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val (outputSequence, extra) = input.updateFromParamPosition(3, "${firstParameter + secondParameter}")
        return Instruction.Output.Input(input.pointerPosition + 4, outputSequence, input.inputValues, input.base, extra)
    }
}


class ProductInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val (outputSequence, extra) = input.updateFromParamPosition(3, "${firstParameter * secondParameter}")
        return Instruction.Output.Input(input.pointerPosition + 4, outputSequence.toList(), input.inputValues, input.base, extra)
    }
}

class SimpleInputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val value = input.inputValues.next()
        val (outputSequence, extra) = input.updateFromParamPosition(1, "$value")
        return Instruction.Output.Input(input.pointerPosition + 2,
                outputSequence,
                input.inputValues,
                input.base,
                extra)
    }
}

class SimpleOutputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val value = input.parameter(1)
        val updatedInput = Instruction.Output.Input(
                input.pointerPosition + 2,
                input.sequence,
                input.inputValues,
                input.base,
                input.extraMemory)
        return Instruction.Output.Value(value, updatedInput)
    }
}

class ShiftingInstructionNonZero : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val param = input.parameter(1)
        val shift = if (param != 0.0) input.parameter(2).toInt() else input.pointerPosition + 3
        return Instruction.Output.Input(shift, input.sequence, input.inputValues, input.base, input.extraMemory)
    }
}

class ShiftingInstructionZero : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val param = input.parameter(1)
        val shift = if (param == 0.0) input.parameter(2).toInt() else input.pointerPosition + 3
        return Instruction.Output.Input(shift, input.sequence, input.inputValues, input.base, input.extraMemory)
    }
}

class LessThanInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val value = if (firstParameter < secondParameter) 1.0 else 0.0
        val (outputSequence, extra) = input.updateFromParamPosition(3, "$value")
        return Instruction.Output.Input(input.pointerPosition + 4, outputSequence, input.inputValues, input.base, extra)
    }
}

class EqualInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val value = if (firstParameter == secondParameter) 1.0 else 0.0
        val (outputSequence, extra) = input.updateFromParamPosition(3, "$value")
        return Instruction.Output.Input(input.pointerPosition + 4, outputSequence, input.inputValues, input.base, extra)
    }
}

class TerminalInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        return Instruction.Output.Terminal
    }
}

class RelativeBaseInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Output.Input): Instruction.Output {
        val base = input.base + input.parameter(1).toInt()
        return Instruction.Output.Input(input.pointerPosition + 2,
                input.sequence,
                input.inputValues,
                base,
                input.extraMemory)
    }

}