package advent

interface Instruction {

    sealed class Output {
        data class Value(val pointerPosition: Int, val updatedSequence: List<String>, val value: Double, val inputs: List<Int>) : Output()
        data class PointedSequence(val pointerPosition: Int, val updatedSequence: List<String>, val inputs: List<Int>, val base: Int) : Output()
        object Terminal : Output()
    }

    data class Input(val pointerPosition: Int, private val originalSequence: MutableList<String>, val values: List<Int>, val base: Int) {
        val instruction: String = originalSequence[pointerPosition]

        fun getAt(index: Int): Double {
            return if (index >= originalSequence.size) {
                val newItemsCount = 0..(index - originalSequence.size)
                originalSequence.addAll(newItemsCount.map { "0" })
                0.0
            } else {
                originalSequence[index].toDouble()
            }
        }

        val sequence: List<String> = originalSequence

        fun updateFromParamPosition(offset: Int, value: String): List<String> {
            val param = originalSequence[originalSequence[pointerPosition + offset].toInt()]
            originalSequence[param.toInt()] = value
            return originalSequence
        }
    }

    fun executeInstruction(input: Input): Output
}

fun Instruction.Input.parameter(offset: Int): Double {
    return when (val firstParameterMode = instruction.getOrNull(instruction.length - 2 - offset) ?: '0') {
        '0' -> getAt(getAt(pointerPosition + offset).toInt())
        '1' -> getAt(pointerPosition + offset)
        '2' -> getAt(getAt(pointerPosition + offset).toInt() + base)
        else -> throw IllegalArgumentException("Unknown mode $firstParameterMode")
    }
}

class SumInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val outputSequence = input.updateFromParamPosition(3, "${firstParameter + secondParameter}")
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, outputSequence, input.values, input.base)
    }
}


class ProductInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val outputSequence = input.updateFromParamPosition(3, "${firstParameter * secondParameter}")
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, outputSequence.toList(), input.values, input.base)
    }
}

class SimpleInputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val value = input.values.first().toDouble()
        return Instruction.Output.PointedSequence(input.pointerPosition + 2,
                input.updateFromParamPosition(1, "$value"),
                input.values.drop(1), input.base)
    }
}

class SimpleOutputInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val output = input.parameter(1)
        return Instruction.Output.Value(input.pointerPosition + 2,
                input.sequence,
                output,
                input.values)
    }
}

class ShiftingInstructionNonZero : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val param = input.parameter(1)
        val shift = if (param != 0.0) input.parameter(2).toInt() else input.pointerPosition + 3
        return Instruction.Output.PointedSequence(shift, input.sequence, input.values, input.base)
    }
}

class ShiftingInstructionZero : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val param = input.parameter(1)
        val shift = if (param == 0.0) input.parameter(2).toInt() else input.pointerPosition + 3
        return Instruction.Output.PointedSequence(shift, input.sequence, input.values, input.base)
    }
}

class LessThanInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val value = if (firstParameter < secondParameter) 1.0 else 0.0
        val updatedSequence = input.updateFromParamPosition(3, "$value")
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, updatedSequence, input.values, input.base)
    }
}

class EqualInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val firstParameter = input.parameter(1)
        val secondParameter = input.parameter(2)
        val value = if (firstParameter == secondParameter) 1.0 else 0.0
        val updatedSequence = input.updateFromParamPosition(3, "$value")
        return Instruction.Output.PointedSequence(input.pointerPosition + 4, updatedSequence, input.values, input.base)
    }
}

class TerminalInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        return Instruction.Output.Terminal
    }
}

class RelativeBaseInstruction : Instruction {
    override fun executeInstruction(input: Instruction.Input): Instruction.Output {
        val base = input.base + input.parameter(1).toInt()
        return Instruction.Output.PointedSequence(input.pointerPosition + 2,
                input.sequence,
                input.values,
                base)
    }

}