package advent


class Day7 : Day {
    override fun execute1() {
        val permutations = mutableListOf<List<Int>>()
        perm(mutableListOf(0, 1, 2, 3, 4), 5, permutations)

        val result = permutations.maxBy { computeOutput(it.toList()) }!!
        println(result)
        println(computeOutput(result))
    }

    private fun computeOutput(phases: List<Int>, loop: Boolean = false): Int {

        val input = loadFile("day7.txt").first()
                .split(",")
        val machine = IntCodeMachine()
        val machineCount = 5

        val machines = (0 until machineCount).map { Instruction.Output.Input(0, input, listOf(phases[it], 0), 0, emptyMap()) }.toMutableList()
        var count = 0
        var lastOutput = 0
        while (true) {
            when (val out = machine.runInstructions(machines[count % machineCount])) {
                is IntCodeMachine.Result.Output -> {
                    lastOutput = out.value.toInt()
                    if (count == machineCount - 1 && !loop) return lastOutput
                    // Store the current machine state
                    machines[count % machineCount] = out.input
                    // Update the next machine
                    count += 1
                    val prev = machines[count % machineCount]
                    val values = if (count >= machineCount) listOf(lastOutput) else listOf(phases[count], lastOutput)
                    machines[count % machineCount] = Instruction.Output.Input(prev.pointerPosition, prev.sequence,
                            values,
                            prev.base, prev.extraMemory)
                }
                IntCodeMachine.Result.Terminal -> return lastOutput
            }
        }


//        val machines = (0 until machineCount).map { Instruction.Output.Input(0, input) }
//        val maxMachine = if (loop) Int.MAX_VALUE else machines.count()
//        return (0 until maxMachine).fold(0) { acc, index ->
//            val inputs = if (index >= 5) listOf(acc) else listOf(phases[index % machines.count()], acc)
//            val machine = machines[index % machines.count()]
//            val result = machine.runInstructions(Instruction.Output.Input(machine.pointerPosition, ArrayList(machine.instructions), inputs, machine.base, emptyMap()))
//            println("Got output $result at $index")
//            when (result) {
//                is IntCodeMachine.Result.Output -> result.value.toInt()
//                is IntCodeMachine.Result.Terminal -> return acc
//            }
//        }
    }

    override fun execute2() {
        val permutations = mutableListOf<List<Int>>()
        perm(mutableListOf(5, 6, 7, 8, 9), 5, permutations)

        val max = permutations.maxBy { computeOutput(it, true) }!!
        println(computeOutput(max, true))
    }


    //Heap's Algorithm
    private fun perm(list: MutableList<Int>, n: Int, output: MutableList<List<Int>>) {
        if (n == 1) {
            output.add(ArrayList(list))
        } else {
            for (i in 0 until n) {
                perm(list, n - 1, output)
                val j = if (n % 2 == 0) i else 0
                val t = list[n - 1]
                list[n - 1] = list[j]
                list[j] = t
            }
        }
    }
}