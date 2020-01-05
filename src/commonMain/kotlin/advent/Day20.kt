package advent

class Day20 : Day {

    data class BoardBound(val left: Int, val top: Int, val right: Int, val bottom: Int)

    override suspend fun execute1() {
        val (board, correctPaths) = findPaths(false)
        println(correctPaths.map { it.filter { board[it.x to it.y] is Cell.Path }.count() - 1 })
    }

    private suspend fun findPaths(withLevels: Boolean): Pair<Map<Pair<Int, Int>, Cell>, List<Set<Step>>> {
        val input = loadFile("day20.txt").mapIndexed { y, s -> s.mapIndexed { x, c -> x to y to c } }.flatten().toMap()
        val boardBound = buildBoard(input)
        val paths = input.filterValues { it == '.' }
        val board = paths.entries.fold(emptyMap<Pair<Int, Int>, Cell>()) { wipBoard, (position, _) ->
            val base = wipBoard.plus(position to Cell.Path)
            findEntries(input, position, emptyList())?.let { base.plus(it) } ?: base
        }
        val end = board.entries.first { it.value == STARTING_CELL }.toPair()
        val correctPaths = board.findAllPaths(
                boardBound,
                end,
                setOf(Step(end.first.first, end.first.second, if (withLevels) 0 else -1)),
                emptyList())
        return Pair(board, correctPaths)
    }

    private fun buildBoard(input: Map<Pair<Int, Int>, Char>): BoardBound {
        return input.filterValues { it == '#' }.let { map ->
            val left = map.minBy { it.key.first }!!.key.first
            val top = map.minBy { it.key.second }!!.key.second
            val right = map.maxBy { it.key.first }!!.key.first
            val bottom = map.maxBy { it.key.second }!!.key.second
            BoardBound(left, top, right, bottom)
        }
    }

    data class Step(val x: Int, val y: Int, val level: Int)

    private fun Map<Pair<Int, Int>, Cell>.findAllPaths(
            bound: BoardBound,
            entry: Pair<Pair<Int, Int>, Cell>,
            // Set: use the hypothesis that a path never loop
            currentPath: Set<Step>,
            results: List<Set<Step>>
    ): List<Set<Step>> {
        val currentLevel = currentPath.last().level
        val newEntries = nextCells(entry.first.first, entry.first.second)
                // Remove all visited positions
                .filter { position ->
                    if (currentLevel < 0) {
                        !currentPath.map { it.x to it.y }.contains(position)
                    } else {
                        // Prevent looping in deeper through a path already used
                        val loopNested = this[position] is Cell.Entry && isToTheInside(position, bound) && currentPath.any { it.x == position.first && it.y == position.second && it.level <= currentLevel }
                        !loopNested && !currentPath.contains(Step(position.first, position.second, currentLevel))
                    }
                }
                .fold(emptyList<Set<Step>>()) { acc, position ->
                    val step = Step(position.first, position.second, currentLevel)
                    when (val cell = this[position]) {
                        Cell.Path -> findAllPaths(bound, position to cell, currentPath.plus(step), acc)
                        ENDING_CELL -> if (currentLevel <= 0) acc.plusElement(currentPath) else acc
                        STARTING_CELL -> acc
                        is Cell.Entry -> {
                            val newPosition = entries.first { it.key != position && it.value == cell }.toPair()
                            val nextLevel = when {
                                currentLevel < 0 -> currentLevel
                                else -> {
                                    val isToTheInside = isToTheInside(position, bound)
                                    if (currentLevel == 0 && !isToTheInside) return@fold acc
                                    if (isToTheInside) currentLevel + 1 else currentLevel - 1
                                }
                            }
                            val nextStep = Step(newPosition.first.first, newPosition.first.second, nextLevel)
                            val visitedPaths = currentPath.plus(step).plus(nextStep)
                            findAllPaths(bound, newPosition, visitedPaths, acc)
                        }
                        null -> acc
                    }
                }
        return newEntries.plus(results)
    }

    private fun isToTheInside(position: Pair<Int, Int>, bound: BoardBound) =
            (position.first in bound.left..bound.right
                    && position.second in bound.top..bound.bottom)


    private fun findEntries(input: Map<Pair<Int, Int>, Char>, position: Pair<Int, Int>,
                            entries: List<Pair<Pair<Int, Int>, Char>>): Pair<Pair<Int, Int>, Cell>? {
        if (entries.size == 2) return entries.first().first to Cell.Entry(entries.sortedWith(Comparator { a, b ->
            if (a.first.first == b.first.first) {
                a.first.second.compareTo(b.first.second)
            } else {
                a.first.first.compareTo(b.first.first)
            }
        }).joinToString("") { it.second.toString() })
        val x = position.first
        val y = position.second
        val entry = nextCells(x, y)
                // Uppercase ascii character
                .firstOrNull { entries.none { e -> e.first == it } && input[it]?.toInt() in (65..90) } ?: return null
        val value = input[entry]!!
        return findEntries(input, entry, entries.plus(entry to value))


    }

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
        println(correctPaths.map { it.filter { board[it.x to it.y] is Cell.Path }.count() - 1 })
    }

    private val STARTING_CELL = Cell.Entry("AA")
    private val ENDING_CELL = Cell.Entry("ZZ")

    sealed class Cell {
        object Path : Cell()
        data class Entry(val value: String) : Cell()
    }
}