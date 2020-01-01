package advent

class Day19 : Day {
    override suspend fun execute1() {
        val intCodeMachine = IntCodeMachine()
        val puzzle = loadFile("day19.txt").first().split(",")
        val side = 100
        val points = ((0 until side).flatMap { x -> (0 until side).map { y -> x to y } })
        val map = addBeamPoints(points, emptySet(), puzzle, intCodeMachine)
        prettyPrint(map, 0 until 100)
        println(map.size)
    }

    private fun prettyPrint(map: Set<Pair<Int, Int>>, range: IntRange, highlight: Pair<Int, Int>? = null): String {
        return range.joinToString("\n") { y ->
            range.joinToString("") { x ->
                if (map.contains(x to y)) {
                    if (x to y == highlight) "O" else "#"
                } else {
                    " "
                }
            }
        }.also { println(it) }
    }

    override suspend fun execute2() {
        val intCodeMachine = IntCodeMachine()
        val puzzle = loadFile("day19.txt").first().split(",")
        // 5 -> 114, 99
        // 10 -> 171, 146
        // 15 -> 267, 228
        // 20 -> 363, 310
        // 25 -> 453, 387
        // 30 -> 548, 468
        // 35 -> 644, 550
        // 40 -> 734, 627
        // 50 -> 925, 790
        val side = 100
        val res = findByDichotomy(puzzle, intCodeMachine, side, 20, 500)
        println(res.first * 10000 + res.second)
    }

    private fun findByDichotomy(puzzle: List<String>, intCodeMachine: IntCodeMachine, side: Int, step: Int, startX: Int): Pair<Int, Int> {
        var x = startX
        var y = 0
        var newX = false
        // Smarter to directly asked for searched points rather than built the map first
        while (true) {
            val result = getPointValue(puzzle, x to y, intCodeMachine)
            if (result == 1) {
                newX = true
                if (listOf(x + side - 1 to y, x to y + side - 1).all { getPointValue(puzzle, it, intCodeMachine) == 1 }) {
                    if (step == 1) {
                        return (x to y)
                    }
                    break
                }
                y += 1
            } else if (newX) {
                x += step
                y = 0
                newX = false
            } else {
                y += 1
            }
        }
        return findByDichotomy(puzzle, intCodeMachine, side, step / 2,  x - 2 * step)
    }

    private fun addBeamPoints(newPoints: Iterable<Pair<Int, Int>>, map: Set<Pair<Int, Int>>, puzzle: List<String>, intCodeMachine: IntCodeMachine): Set<Pair<Int, Int>> {
        return newPoints.fold(map) { acc, point ->
            val count = getPointValue(puzzle, point, intCodeMachine)
            // Only add Beams point
            if (count == 1) {
                acc.plus(point)
            } else {
                acc
            }
        }
    }

    private fun getPointValue(puzzle: List<String>, point: Pair<Int, Int>, intCodeMachine: IntCodeMachine): Int {
        var input = Instruction.Output.Input(0, puzzle, InputQueue(listOf(point.first, point.second)), 0, emptyMap())
        var count = 0
        loop@ while (true) {
            when (val output = intCodeMachine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    count += output.value.toInt()
                    input = output.input
                }
                IntCodeMachine.Result.Terminal -> break@loop
            }.let { }
        }
        return count
    }

}