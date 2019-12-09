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
        val machineCount = 5
        val machines = (0 until machineCount).map { IntCodeMachine(input) }
        val maxMachine = if (loop) Int.MAX_VALUE else machines.count()
        return (0 until maxMachine).fold(0) { acc, index ->
            val inputs = if (index >= 5) listOf(acc) else listOf(phases[index % machines.count()], acc)
            val machine = machines[index % machines.count()]
            val result = machine.runInstructions(inputs)
            println("Got output $result at $index")
            when (result) {
                is IntCodeMachine.Result.Output -> result.value.toInt()
                is IntCodeMachine.Result.Terminal -> return acc
            }
        }
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