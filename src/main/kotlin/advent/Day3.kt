package advent

import java.lang.IllegalArgumentException
import kotlin.math.abs

class Day3 : Day {
    override fun execute1() {
        val input = loadFile("day3.txt")
        val line1 = loadPath(input[0])
        val line2 = loadPath(input[1])
        val intersections = line1.intersect(line2).filter { it.first != 0 || it.second != 0 }
        val result = intersections.minBy { abs(it.first) + abs(it.second) }!!
        println(result)
        println(abs(result.first) + abs(result.second))
    }

    override fun execute2() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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