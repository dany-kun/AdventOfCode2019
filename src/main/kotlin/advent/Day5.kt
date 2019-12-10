package advent

class Day5 : Day {
    override fun execute1() {
        val input = 1
        traverse(input)
    }

    private fun traverse(input: Int) {
        val sequence = loadFile("day5.txt").first()
                .split(",")
        val intCodeMachine = IntCodeMachine()
        val results = intCodeMachine
                .runInstructions(Instruction.Output.Input(0, sequence,
                        listOf(input), 0, emptyMap()))
        println(results)
    }

    override fun execute2() {
        traverse(5)
    }
}