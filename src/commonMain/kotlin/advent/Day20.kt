package advent

typealias BoardNodes = Map<Day20.Cell.Road, Map<Day20.Cell.Road, Int>>

class Day20 : Day {

    data class BoardBound(val left: Int, val top: Int, val right: Int, val bottom: Int)

    override suspend fun execute1() {
        val (board, correctPaths) = findPaths(false)
        println(correctPaths.map { it.pathLength(board) })
    }

    private suspend fun findPaths(withLevels: Boolean): Pair<BoardNodes, List<Set<Step>>> {
        val board = createBoard()
        val startingLevel = if (withLevels) 0 else -1
        val result = board.findAllPaths(setOf(Step(Cell.Road.Out("AA"), startingLevel)), emptyList(), withLevels)
        return board to result
    }

    private fun Set<Step>.pathLength(boardNodes: BoardNodes): Int {
        return zipWithNext { a, b -> boardNodes[a.node]!![b.node] ?: 0 }.sum() - 1
    }

    private fun BoardNodes.findAllPaths(steps: Set<Step>, result: List<Set<Step>>, withLevels: Boolean): List<Set<Step>> {
        val previousStep = steps.last()
        val level = previousStep.level
        return this[previousStep.node]!!.keys.fold(result) { acc, node ->
            val step = Step(node, level)
            val updatedSteps = steps.plus(step)
            when {
                // steps.contains(step) -> acc
                steps.any { it.node == node && level > it.level } -> acc
                node.value == "ZZ" -> if (level <= 0) acc.plusElement(updatedSteps) else acc
                node.value == "AA" -> acc
                else -> {
                    val nextStep = when (node) {
                        is Cell.Road.In -> Step(Cell.Road.Out(node.value), if (level < 0) level else level + 1)
                        is Cell.Road.Out -> Step(Cell.Road.In(node.value), if (level < 0) level else level - 1)
                    }
                    findAllPaths(updatedSteps.plus(nextStep), acc, withLevels)
                }
            }
        }
    }

    private suspend fun createBoard(): BoardNodes {
        val input = loadFile("day20.txt").mapIndexed { y, s -> s.mapIndexed { x, c -> x to y to c } }.flatten().toMap()
        val boardBound = buildBoardBound(input)
        val characters = createPortals(input, input.filter { it.value.toInt() in (65..90) }.keys, boardBound)
        val paths = input.filterValues { it == '.' }.mapValues { Cell.Path }
        return characters.map { it.value to findLink(paths.plus(characters), setOf(it.key), 0, emptyMap()) }.toMap()
    }

    private fun findLink(paths: Map<Pair<Int, Int>, Cell>, visitedLinks: Set<Pair<Int, Int>>, distance: Int, result: Map<Cell.Road, Int>): Map<Cell.Road, Int> {
        val start = visitedLinks.last()
        return nextCells(start.first, start.second)
                .filter { !visitedLinks.contains(it) }
                .fold(result) { acc, coord ->
                    when (val cell = paths[coord]) {
                        Cell.Path -> findLink(paths, visitedLinks.plus(coord), distance + 1, acc)
                        is Cell.Road -> acc.plus(cell to distance)
                        null -> acc
                    }

                }
    }

    private fun createPortals(input: Map<Pair<Int, Int>, Char>, chars: Set<Pair<Int, Int>>, boardBound: BoardBound): Map<Pair<Int, Int>, Cell.Road> {
        return chars.mapNotNull { (x, y) ->
            val nextCells = nextCells(x, y)
            val otherChar = nextCells.singleOrNull { input[it]?.toInt() in (65..90) }
            val road = nextCells.singleOrNull { input[it] == '.' }
            if (otherChar != null && road != null) {
                val value = "${input[otherChar]}${input[x to y]}"
                val cell = when {
                    road.first == x && road.second > y -> if (y < boardBound.top) Cell.Road.Out(value) else Cell.Road.In(value)
                    road.first == x -> if (y > boardBound.bottom) Cell.Road.Out(value.reversed()) else Cell.Road.In(value.reversed())
                    road.first > x -> if (x < boardBound.left) Cell.Road.Out(value) else Cell.Road.In(value)
                    else -> if (x > boardBound.right) Cell.Road.Out(value.reversed()) else Cell.Road.In(value.reversed())
                }
                (x to y) to cell
            } else {
                null
            }
        }.toMap()
    }

    private fun buildBoardBound(input: Map<Pair<Int, Int>, Char>): BoardBound {
        return input.filterValues { it == '#' }.let { map ->
            val left = map.minBy { it.key.first }!!.key.first
            val top = map.minBy { it.key.second }!!.key.second
            val right = map.maxBy { it.key.first }!!.key.first
            val bottom = map.maxBy { it.key.second }!!.key.second
            BoardBound(left, top, right, bottom)
        }
    }

    data class Step(val node: Cell.Road, val level: Int)

    private fun nextCells(x: Int, y: Int): List<Pair<Int, Int>> {
        return listOf(
                x to y + 1,
                x to y - 1,
                x + 1 to y,
                x - 1 to y
        )
    }

    override suspend fun execute2() {
        val (board, correctPaths) = findPaths(true)
        println(correctPaths.map { it.pathLength(board) })
    }

    sealed class Cell {
        object Path : Cell()
        sealed class Road : Cell() {
            abstract val value: String

            data class In(override val value: String) : Road()
            data class Out(override val value: String) : Road()
        }
    }
}