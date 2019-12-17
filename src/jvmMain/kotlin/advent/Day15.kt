package advent

class Day15 : Day {


    override fun execute1() {
        val isa = loadFile("day15.txt").first().split(",")
        val machine = IntCodeMachine()

        var instruction = Instruction.Output.Input(0, isa, emptySequence(), 0, emptyMap())
        val visitedPositions = mutableMapOf<Pair<Int, Int>, String>()
        findShortestPath(visitedPositions, 0 to 0, emptyList()) {
            val input = Instruction.Output.Input(instruction.pointerPosition, instruction.sequence, sequenceOf(it), instruction.base, instruction.extraMemory)
            when (val out = machine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    instruction = out.input
                    out.value
                }
                IntCodeMachine.Result.Terminal -> TODO()
            }
        }
    }

    private fun findShortestPath(
            visitedPositions: MutableMap<Pair<Int, Int>, String>,
            currentPosition: Pair<Int, Int>,
            movedPath: List<Pair<Int, Int>>,
            evaluatePosition: (Int) -> Double): MutableMap<Pair<Int, Int>, String> {
        if (visitedPositions.any { it.value == "0" }) {
            println("Got it")
            return visitedPositions
        }
        val nextPositionNorth = nextPosition(visitedPositions, { evaluatePosition(1) }, currentPosition.first to currentPosition.second + 1)
        if (nextPositionNorth != null) {
            return findShortestPath(visitedPositions, nextPositionNorth, movedPath.plus(nextPositionNorth), evaluatePosition)
        }
        val nextPositionEast = nextPosition(visitedPositions, { evaluatePosition(3) }, currentPosition.first + 1 to currentPosition.second)
        if (nextPositionEast != null) {
            return findShortestPath(visitedPositions, nextPositionEast, movedPath.plus(nextPositionEast), evaluatePosition)
        }
        val nextPositionSouth = nextPosition(visitedPositions, { evaluatePosition(2) }, currentPosition.first to currentPosition.second - 1)
        if (nextPositionSouth != null) {
            return findShortestPath(visitedPositions, nextPositionSouth, movedPath.plus(nextPositionSouth), evaluatePosition)
        }
        val nextPositionWest = nextPosition(visitedPositions, { evaluatePosition(4) }, currentPosition.first - 1 to currentPosition.second)
        if (nextPositionWest != null) {
            return findShortestPath(visitedPositions, nextPositionWest, movedPath.plus(nextPositionWest), evaluatePosition)
        }
        return visitedPositions
    }

    private fun nextPosition(visitedPositions: MutableMap<Pair<Int, Int>, String>, output: () -> Double, position: Pair<Int, Int>): Pair<Int, Int>? {
        // Position already visited
        if (visitedPositions[position] != null) return null
        return when (output()) {
            0.0 -> {
                visitedPositions[position] = "X"
                null
            }
            1.0 -> {
                visitedPositions[position] = " "
                position
            }
            2.0 -> {
                visitedPositions[position] = "0"
                position
            }
            else -> throw IllegalArgumentException("Unknown result $output")
        }
    }

    override fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}