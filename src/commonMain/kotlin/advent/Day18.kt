package advent

import kotlin.math.min

class Day18(private val emitter: suspend (UI) -> Unit = {}) : Day {


    sealed class UI {
        data class Graph(val graph: Map<Cell, Map<Cell, Int>>) : UI()
    }

    override suspend fun execute1() {
        val map = buildMap()
        val current = map.entries.single { it.value is Cell.Character }
        val scannedMap = scanMap(map, current.value, mapOf(current.value to emptyMap()), listOf(current.key))
        emitter(UI.Graph(scannedMap))
        val result = bruteForceScannedMap(scannedMap, Cell.Character, 0, Int.MAX_VALUE)
        println(result)
    }

    private suspend fun bruteForceScannedMap(scannedMap: Map<Cell, Map<Cell, Int>>, current: Cell, count: Int, best: Int): Int {
        val reachableNodes = scannedMap[current]!!
        val entries = reachableNodes.entries.filter { it.key is Cell.Key }
        // Prioritize leafs
        val smartEntries = entries.filter { scannedMap[it.key]?.size == 1 }
        val e = when {
            smartEntries.isNotEmpty() -> smartEntries
            else -> entries
        }
        return e.fold(best) { acc, (key, distance) ->
            when (key) {
                is Cell.Key -> {
                    val newCount = count + distance
                    if (newCount >= acc) return@fold acc
                    val mapWithoutDoor = popNodeAndRelink(scannedMap, Cell.Door(key.value.toUpperCase()))
                    val mapWithoutCurrent = popNodeAndRelink(mapWithoutDoor, current).filterValues { it.values.isNotEmpty() }
                    if (mapWithoutCurrent.isEmpty()) return@fold newCount.also { println(it) }
                    emitter(UI.Graph(mapWithoutCurrent))
                    bruteForceScannedMap(mapWithoutCurrent, key, newCount, acc)
                }
                is Cell.Door -> acc
                Cell.Character,
                Cell.Path -> throw IllegalArgumentException("No path in nodes")
            }
        }
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


    // Brute Force : do not scale ;)
    private fun bruteForce(map: Map<Pair<Int, Int>, Cell>,
                           current: Pair<List<String>, Int>,
                           best: Pair<List<String>, Int>,
                           currentPosition: Pair<Int, Int>,
                           keys: Set<Cell.Key>): Pair<List<String>, Int> {
        val accessibleKeys = nextKeys(currentPosition, listOf(currentPosition), map, emptyMap())
        return accessibleKeys.entries.fold(best) { acc, (keyCoord, count) ->
            val key = map[keyCoord] as Cell.Key
            val updatedKeys = keys.minus(key)
            val pair = current.first.plus(key.value) to current.second + count
            when {
                pair.second >= acc.second -> acc
                updatedKeys.isEmpty() -> pair.also { println(it) }
                else -> {
                    val updatedMap = updateMap(map, keyCoord, currentPosition, key.value)
                    bruteForce(updatedMap, pair, acc, keyCoord, updatedKeys)
                }
            }
        }
    }

    private fun updateMap(map: Map<Pair<Int, Int>, Cell>, keyCoord: Pair<Int, Int>, current: Pair<Int, Int>, element: String): Map<Pair<Int, Int>, Cell> {
        val doors = map.filter { (it.value as? Cell.Door)?.value?.toLowerCase() == element }.keys
        return map.minus(current).plus(current to Cell.Path) // Remove current character
                .minus(doors).plus(doors.associateWith { Cell.Path }) // Replace opened door with paths
                .minus(keyCoord).plus(keyCoord to Cell.Path) // Move character
    }

    private fun nextKeys(currentCell: Pair<Int, Int>, visitedCells: List<Pair<Int, Int>>,
                         map: Map<Pair<Int, Int>, Cell>, aggr: Map<Pair<Int, Int>, Int>): Map<Pair<Int, Int>, Int> {
        val closeCells = nextVisitableCells(currentCell, visitedCells)
        return closeCells.fold(aggr) { acc, it ->
            when (map[it]) {
                Cell.Character, is Cell.Door, null -> acc
                is Cell.Key -> {
                    // require(acc[it] == null)
                    // When multiple paths goes to the same key, use the shortest one
                    val length = visitedCells.count() + 1
                    acc.plus(it to (acc[it]?.let { min(it, length) } ?: length))
                }
                Cell.Path -> nextKeys(it, visitedCells.plus(it), map, acc)
            }
        }
    }

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