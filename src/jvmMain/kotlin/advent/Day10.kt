package advent

import kotlin.math.PI
import kotlin.math.atan2

class Day10 : Day {
    override fun execute1() {
        val asteroids = loadMap()
        val result = asteroids.maxBy { countVisibleAsteroid(it, asteroids) }!!
        println(result)
    }

    private fun loadMap(): List<Pair<Int, Int>> {
        return loadFile("day10.txt").withIndex().flatMap { value ->
            value.value.mapIndexedNotNull { x, c ->
                if (c != '#')
                    null
                else
                    x to value.index
            }
        }
    }

    private fun countVisibleAsteroid(base: Pair<Int, Int>, asteroids: List<Pair<Int, Int>>): Int {
        return asteroids
                .filter { it != base }
                .groupBy { it.toPolar(base).angle }
                .keys
                .size
    }

    override fun execute2() {
        val base = 23 to 29
        val asteroids = loadMap().filter { it != base }
        val sorted = asteroids
                .groupBy { it.toPolar(base).angle }
                .mapValues { it.value.sortedBy { it.toPolar(base).distance } }

        var remainingAsteroids = sorted
        var count = 0
        var batch: List<Pair<Int, Int>>
        while (true) {
            batch = remainingAsteroids.entries.map { it.value.first() }.toList()
            if (count + batch.size >= 200) {
                break
            }
            count += batch.size
            remainingAsteroids = remainingAsteroids.map { it.key to it.value.drop(1) }.filter { it.second.isNotEmpty() }
                    .toMap()

        }
        val sortedBy = batch.sortedBy { it.toPolar(base).angle }
        println(sortedBy[200 -count - 1])
    }

    private fun Pair<Int, Int>.toPolar(base: Pair<Int, Int>): Polar {
        val (x, y) = first - base.first to second - base.second
        // Need to convert it to degree due to some rounding issues with radians
        val angle = (atan2(y * 1.0, x * 1.0).let { if (it < 0) it + 2 * PI else it } + PI / 2) * 360 / (2 * PI) % 360
        val distance = x * x + y * y
        return Polar(angle, distance)
    }

    data class Polar(val angle: Double, val distance: Int)
}