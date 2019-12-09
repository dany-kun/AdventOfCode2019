package advent

interface Instruction {

    sealed class Output {
        data class Value(val pointerPosition: Int, val updatedSequence: List<String>, val value: Double, val inputs: List<Int>) : Output()
        data class PointedSequence(val pointerPosition: Int, val updatedSequence: List<String>, val inputs: List<Int>) : Output()
        object Terminal : Output()
    }

    data class Input(val pointerPosition: Int, val originalSequence: List<String>, val values: List<Int>) {
        val instruction: String = originalSequence[pointerPosition]
    }

    fun executeInstruction(input: Input): Output


    fun parameter(formula: Input, offset: Int): Double {
        val instruction = formula.instruction
        return when (val firstParameterMode = instruction.getOrNull(instruction.length - 2 - offset) ?: '0') {
            '0' -> formula.originalSequence[formula.originalSequence[formula.pointerPosition + offset].toInt()].toDouble()
            '1' -> formula.originalSequence[formula.pointerPosition + offset].toDouble()
            else -> throw IllegalArgumentException("Unknown mode $firstParameterMode")
        }
    }

    fun updateSequence(seq: List<String>, position: Int, value: Double): List<String> {
        return seq.toMutableList().apply { set(position, "$value") }.toList()
    }
}

class SumInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = parameter(input, 1)
        val secondParameter = parameter(input, 2)
        val outputAddress = input.originalSequence[input.pointerPosition + 3].toInt()
        val outputSequence = updateSequence(input.originalSequence, outputAddress, firstParameter + secondParameter)
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, outputSequence, input.values)
    }
}


class ProductInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = parameter(input, 1)
        val secondParameter = parameter(input, 2)
        val outputAddress = input.originalSequence[input.pointerPosition + 3].toInt()
        val outputSequence = updateSequence(input.originalSequence, outputAddress, firstParameter * secondParameter)
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, outputSequence.toList(), input.values)
    }
}

class SimpleInputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val outputAddress = input.originalSequence[input.pointerPosition + 1].toInt()
        val value = input.values.first().toDouble()
        return Instruction.Output.PointedSequence(input.pointerPosition + 2,
                updateSequence(input.originalSequence, outputAddress, value),
                input.values.drop(1))
    }
}

class SimpleOutputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val output = parameter(input, 1)
        return Instruction.Output.Value(input.pointerPosition + 2,
                input.originalSequence,
                output,
                input.values)
    }
}

class ShiftingInstructionNonZero : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val param = parameter(input, 1)
        val shift = if (param != 0.0) parameter(input, 2).toInt() else input.pointerPosition + 3
        return Instruction.Output.PointedSequence(shift, input.originalSequence, input.values)
    }
}

class ShiftingInstructionZero : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val param = parameter(input, 1)
        val shift = if (param == 0.0) parameter(input, 2).toInt() else input.pointerPosition + 3
        return Instruction.Output.PointedSequence(shift, input.originalSequence, input.values)
    }
}

class LessThanInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = parameter(input, 1)
        val secondParameter = parameter(input, 2)
        val value = if (firstParameter < secondParameter) 1.0 else 0.0
        val position = input.originalSequence[input.pointerPosition + 3].toInt()
        val updatedSequence = updateSequence(input.originalSequence, position, value)
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, updatedSequence, input.values)
    }
}

class EqualInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = parameter(input, 1)
        val secondParameter = parameter(input, 2)
        val value = if (firstParameter == secondParameter) 1.0 else 0.0
        val updatedSequence = updateSequence(input.originalSequence, input.originalSequence[input.pointerPosition + 3].toInt(), value)
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, updatedSequence, input.values)
    }
}

class TerminalInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        return Instruction.Output.Terminal
    }
}