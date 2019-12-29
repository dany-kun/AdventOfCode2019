package advent

import kotlin.math.abs

class Day3 : Day {
    override suspend fun execute1() {
        val input = loadFile("day3.txt")
        val line1 = loadPath(input[0])
        val line2 = loadPath(input[1])
        val intersections = line1.intersect(line2).filter { it.first != 0 || it.second != 0 }
        val result = intersections.minBy { abs(it.first) + abs(it.second) }!!
        println(result)
        println(abs(result.first) + abs(result.second))
    }

    override suspend fun execute2() {
        val input = loadFile("day3.txt")
        val line1 = loadPath(input[0])
        val line2 = loadPath(input[1])
        val intersections = line1.intersect(line2).filter { it.first != 0 || it.second != 0 }.toSet()
        val line1Steps = intersections.map { line1.indexOf(it) }
        val line2Steps = intersections.map { line2.indexOf(it) }
        // +2 to offset the indices from 0
        val steps = line1Steps.zip(line2Steps) { a, b -> a + b + 2 }
        println(steps.min())
    }

    private fun loadPath(line: String): List<Pair<Int, Int>> {
        return line.split(",").fold(listOf()) { acc, el ->
            val (x, y) = acc.lastOrNull() ?: 0 to 0
            val (direction, count) = el.let { it.first() to it.drop(1).toInt() }
            val points = when (direction) {
                'L' -> (1..count).map { (x - it) to y }
                'R' -> (1..count).map { (x + it) to y }
                'D' -> (1..count).map { x to (y - it) }
                'U' -> (1..count).map { x to (y + it) }
                else -> throw IllegalArgumentException("Unknown direction $direction")
            }
            acc.plus(points)
        }
    }
}