package advent

import kotlin.math.abs

class Day15 : Day {


    override suspend fun execute1() {
        val result = loadMap()
        drawMap(result)
    }

    private suspend fun loadMap(): Map<Pair<Int, Int>, String> {
        val isa = loadFile("day15.txt").first().split(",")
        val machine = IntCodeMachine()

        var instruction = Instruction.Output.Input(0, isa, IntCodeInput(), 0, emptyMap())
        val visitedPositions = mutableMapOf((0 to 0) to "D")
        return findShortestPath(0, visitedPositions, 0 to 0, emptyList(), emptyList()) {
            val input = Instruction.Output.Input(instruction.pointerPosition, instruction.sequence, SingleInput(it), instruction.base, instruction.extraMemory)
            when (val out = machine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    instruction = out.input
                    out.value
                }
                IntCodeMachine.Result.Terminal -> TODO()
            }
        }
    }

    private fun drawMap(result: Map<Pair<Int, Int>, String>) {
        val (minY, maxY) = result.keys.map { it.second }.let { it.min()!! to it.max()!! }
        val (minX, maxX) = result.keys.map { it.first }.let { it.min()!! to it.max()!! }

        val map = (minY..maxY).joinToString("\n") { y ->
            (minX..maxX).joinToString("") { x ->
                result[x to y] ?: " "
            }
        }
        println(map)

    }

    private fun moveInDirection(count: Int,
                                visitedPositions: MutableMap<Pair<Int, Int>, String>,
                                currentPosition: Pair<Int, Int>,
                                positionToTry: Pair<Int, Int>,
                                movedPath: List<Pair<Int, Int>>,
                                nodesToRevisit: List<Pair<Int, Int>>,
                                command: Int,
                                evaluatePosition: (Int) -> Double): Map<Pair<Int, Int>, String>? {
        val nextPosition = nextPosition(visitedPositions, { evaluatePosition(command) }, positionToTry)
        return if (nextPosition != null) {
            if (visitedPositions[nextPosition] == "0") println(count + 1)
            findShortestPath(
                    count + 1,
                    visitedPositions,
                    nextPosition,
                    movedPath.plus(nextPosition),
                    nodesToRevisit.plus(currentPosition),
                    evaluatePosition)
        } else {
            null
        }
    }

    private fun findShortestPath(
            count: Int,
            visitedPositions: MutableMap<Pair<Int, Int>, String>,
            currentPosition: Pair<Int, Int>,
            movedPath: List<Pair<Int, Int>>,
            nodesToRevisit: List<Pair<Int, Int>>,
            evaluatePosition: (Int) -> Double): Map<Pair<Int, Int>, String> {
        return moveInDirection(count, visitedPositions,
                currentPosition,
                currentPosition.first to currentPosition.second + 1,
                movedPath,
                nodesToRevisit, 1, evaluatePosition)
                ?: moveInDirection(count, visitedPositions,
                        currentPosition,
                        currentPosition.first to currentPosition.second - 1,
                        movedPath,
                        nodesToRevisit, 2, evaluatePosition)
                ?: moveInDirection(count, visitedPositions,
                        currentPosition,
                        currentPosition.first - 1 to currentPosition.second,
                        movedPath,
                        nodesToRevisit, 3, evaluatePosition)
                ?: moveInDirection(count, visitedPositions,
                        currentPosition,
                        currentPosition.first + 1 to currentPosition.second,
                        movedPath,
                        nodesToRevisit, 4, evaluatePosition)
                ?: tryPreviousPosition(count, visitedPositions, currentPosition, nodesToRevisit, movedPath, evaluatePosition)
    }

    private fun tryPreviousPosition(count: Int,
                                    visitedPositions: MutableMap<Pair<Int, Int>, String>, currentPosition: Pair<Int, Int>, nodesToRevisit: List<Pair<Int, Int>>, movedPath: List<Pair<Int, Int>>, evaluatePosition: (Int) -> Double): Map<Pair<Int, Int>, String> {
        if (nodesToRevisit.isEmpty()) {
            return visitedPositions
        }
        require(movedPath.last() == currentPosition)

        // Turn back to last visited node
        val previousNode = nodesToRevisit.last()

        val direction = when {
            currentPosition.first == previousNode.first && currentPosition.second == previousNode.second - 1 -> 1
            currentPosition.first == previousNode.first && currentPosition.second == previousNode.second + 1 -> 2
            currentPosition.first == previousNode.first - 1 && currentPosition.second == previousNode.second -> 4
            currentPosition.first == previousNode.first + 1 && currentPosition.second == previousNode.second -> 3
            else -> throw IllegalStateException("Not adjacent cells: $currentPosition vs $previousNode")
        }
        evaluatePosition(direction)

        return findShortestPath(count - 1, visitedPositions,
                previousNode,
                movedPath.dropLast(1),
                nodesToRevisit.dropLast(1),
                evaluatePosition)
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
                visitedPositions[position] = "."
                position
            }
            2.0 -> {
                visitedPositions[position] = "0"
                position
            }
            else -> throw IllegalArgumentException("Unknown result $output")
        }
    }

    override suspend fun execute2() {
        val result = loadMap()
        val start = result.entries.first { it.value == "0" }.key
        val paths = result.filterValues { it != "X" }.keys
        val distances = findAllDistances(start, 0, mapOf(start to 0), paths)
        println(distances.values.max())
    }

    private fun findAllDistances(start: Pair<Int, Int>, distance: Int, distances: Map<Pair<Int, Int>, Int>, paths: Set<Pair<Int, Int>>): Map<Pair<Int, Int>, Int> {
        if (paths.isEmpty()) return distances
        val closePoints = paths.filter { it.isAdjacentTo(start) }
        val newDistances = distances.plus(closePoints.map { it to distance + 1 })
        val newPaths = paths.minus(closePoints.toSet())
        return closePoints.fold(distances) { acc, pair ->
            acc.plus(findAllDistances(pair, distance + 1, newDistances, newPaths))
        }
    }

    private fun Pair<Int, Int>.distanceTo(point: Pair<Int, Int>) = abs(point.first - first) + abs(point.second - second)
    private fun Pair<Int, Int>.isAdjacentTo(point: Pair<Int, Int>) = distanceTo(point) == 1
}