package advent

import kotlin.math.min

class Day18(private val emitter: suspend (UI) -> Unit = {}) : Day {


    sealed class UI {
        data class Graph(val graph: Map<Cell, Map<Cell, Int>>) : UI()
    }

    override suspend fun execute1() {
        val map = buildMap()

//        val bestPath = findBestPath(map, Cell.Character, mapOf(listOf(Cell.Character) to 0), listOf(Cell.Character))
// println(bestPath.minBy { it.value })
        val current = map.entries.single { it.value == Cell.Character }

        // LinkedList
        // println(createNodes(map, Node(Cell.Character, emptyMap()), listOf(current.key), emptyMap()))

        val scannedMap = scanMap(map, current.value, mapOf(current.value to emptyMap()), listOf(current.key))
        // emitter(UI.Graph(scannedMap))
        val result = bruteForceScannedMap(scannedMap, Cell.Character, 0, Int.MAX_VALUE)
        println(result)
    }

    private suspend fun bruteForceScannedMap(scannedMap: Map<Cell, Map<Cell, Int>>, current: Cell, count: Int, best: Int): Int {
        val reachableNodes = scannedMap[current]!!
        val keys = reachableNodes.keys.filterIsInstance<Cell.Key>()
        val doors = reachableNodes.keys.filterIsInstance<Cell.Door>()
        val smartEntries = keys.filter { scannedMap[it]?.size == 1 }
        val e = when {
            // Prioritize leafs
            smartEntries.isNotEmpty() -> smartEntries
            // When all nodes are keys, start with the closest ones
            // or when all doors in nodes can be opened by keys in nodes, start with the closest key
            reachableNodes.size == keys.size || doors.all { keys.contains(Cell.Key(it.value.toLowerCase())) } -> keys.sortedBy { reachableNodes[it]!! }.take(1)
            else -> {
                keys
            }
        }
        // println(e)
        return e.fold(best) { acc, key ->
            val distance = reachableNodes[key]!!
            val newCount = count + distance
            if (newCount >= acc) return@fold acc
            val mapWithoutDoor = popNodeAndRelink(scannedMap, Cell.Door(key.value.toUpperCase()))
            val mapWithoutCurrent = popNodeAndRelink(mapWithoutDoor, current).filterValues { it.values.isNotEmpty() }
            if (mapWithoutCurrent.isEmpty()) return@fold newCount.also { println(it) }
            // emitter(UI.Graph(mapWithoutCurrent))
            bruteForceScannedMap(mapWithoutCurrent, key, newCount, acc)
        }

    }

    private fun doorsOpeningFromAnotherBranch(node: Node, keys: Set<Cell.Key>, doors: Set<Cell.Door>): Set<Cell.Door> {
        return node.links.entries.fold(doors) { acc, (node, _) ->
            val updatedKeys = if (node.cell is Cell.Key) keys.plus(node.cell) else keys
            val updatedDoors = if (node.cell is Cell.Door) acc.plus(node.cell) else acc
            val filteredDoors = updatedDoors.filter { !updatedKeys.contains(Cell.Key(it.value.toLowerCase())) }.toSet()
            doorsOpeningFromAnotherBranch(node, updatedKeys, filteredDoors)
        }
    }

    private fun buildNode(start: Cell, map: Map<Cell, Map<Cell, Int>>, visitedNode: Set<Cell>): Node {
        return Node(start, map[start]!!
                .filterKeys { !visitedNode.contains(it) }
                .map { buildNode(it.key, map, visitedNode.plus(it.key).toSet()) to it.value }.toMap())
    }


    private fun popNodeAndRelink(scannedMap: Map<Cell, Map<Cell, Int>>, door: Cell): Map<Cell, Map<Cell, Int>> {
        val unlockedDoor = scannedMap[door] ?: emptyMap()
        return scannedMap.mapValues {
            val previousDistance = it.value[door]
            // Node contains a link to the door; add newly accessible keys
            if (previousDistance != null) {
                it.value.minus(door).plus(unlockedDoor.filterKeys { k -> k != it.key }.mapValues { (k, d) ->
                    // If a link already exists, use the shortest path
                    min(it.value[k] ?: Int.MAX_VALUE, d + previousDistance)
                })
            } else {
                it.value
            }
        }.minus(door)
    }

    private fun scanMap(map: Map<Pair<Int, Int>, Cell>,
                        rootCell: Cell,
                        visitedNodes: Map<Cell, Map<Cell, Int>>,
                        visitedCells: List<Pair<Int, Int>>): Map<Cell, Map<Cell, Int>> {
        val nextPositions = nextVisitableCells(visitedCells.last(), visitedCells)
        return nextPositions.fold(visitedNodes) { acc, el ->
            when (val cell = map[el]) {
                is Cell.Key,
                is Cell.Character,
                is Cell.Door -> {
                    // Link already created
                    val rootNode = acc[rootCell]!!
                    val distance = visitedCells.count()
                    val shortestDistance = rootNode[cell]?.let { min(it, distance) } ?: distance
                    val updatedLinks = rootNode.minus(cell).plus(cell to shortestDistance)
                    val updatedVisitedNodes = acc.minus(rootCell).plus(rootCell to updatedLinks) // Update previous root links
                    // Add a new root
                    if (acc[cell] != null) return@fold updatedVisitedNodes
                    scanMap(map, cell, updatedVisitedNodes.plus(cell to emptyMap()), listOf(el))

                }
                Cell.Path -> scanMap(map, rootCell, acc, visitedCells.plusElement(el))
                null -> acc
            }
        }
    }

    data class Node(val cell: Cell, val links: Map<Node, Int>)


    private fun nextVisitableCells(currentCell: Pair<Int, Int>, visitedCells: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        return listOf(
                currentCell.first to currentCell.second + 1,
                currentCell.first to currentCell.second - 1,
                currentCell.first + 1 to currentCell.second,
                currentCell.first - 1 to currentCell.second
        ).filter { !visitedCells.contains(it) }
    }

    private suspend fun buildMap(): Map<Pair<Int, Int>, Cell> {
        return loadFile("day18.txt")
                .mapIndexed { y, s ->
                    s.mapIndexedNotNull { x, c ->
                        val coord = x to y
                        when {
                            c == '@' -> coord to Cell.Character
                            c == '.' -> coord to Cell.Path
                            c.toInt() in (97..122) -> coord to Cell.Key(c.toString())
                            c.toInt() in (65..90) -> coord to Cell.Door(c.toString())
                            else -> null
                        }
                    }
                }
                .flatten()
                .associate { it }
    }

    sealed class Cell {
        object Character : Cell()
        data class Key(val value: String) : Cell()
        data class Door(val value: String) : Cell()
        object Path : Cell()
    }

    override suspend fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}