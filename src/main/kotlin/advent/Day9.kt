package advent

class Day9 : Day {
    override fun execute1() {
        val seq = loadFile("day9.txt").first().split(",")
        val intCodeMachine = IntCodeMachine(seq)
        while (true) {
            val result = intCodeMachine.runInstructions(listOf(1))
            when (result) {
                is IntCodeMachine.Result.Output -> Unit
                IntCodeMachine.Result.Terminal -> TODO()
            }.let { }
        }

    }

    override fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}