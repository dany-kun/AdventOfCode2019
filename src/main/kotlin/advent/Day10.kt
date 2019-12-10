package advent

import kotlin.math.PI
import kotlin.math.atan2

class Day10 : Day {
    override fun execute1() {
        val asteroids = loadMap()
        val result = asteroids.maxBy { countVisibleAsteroid(it, asteroids) }!!
        println(result)
        // println(countVisibleAsteroid(5 to 8, asteroids))
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
        // val base = 23 to 29
        val base = 11 to 13
        val asteroids = loadMap().filter { it != base }
        val sorted = asteroids
                .groupBy { it.toPolar(base).angle % PI }
                .mapValues { it.value.sortedBy { it.toPolar(base).distance } }
                .entries
                .sortedBy { it.key % PI }
                .toMutableList()

        var remainingAsteroids = sorted
        val firstAsteroids = mutableListOf<Pair<Int, Int>>()
        while (firstAsteroids.size < 200) {
            firstAsteroids.addAll(remainingAsteroids.map { it.value[0] })
            println(firstAsteroids)
            remainingAsteroids = remainingAsteroids.map { it.key to it.value.drop(1) }.filter { it.second.isNotEmpty() }
                    .toMap()
                    .mapValues { it.value.sortedBy { it.toPolar(base).distance } }
                    .entries
                    .sortedBy { it.key % PI }
                    .toMutableList()
        }
        println(firstAsteroids[200])
    }

    private fun Pair<Int, Int>.toPolar(base: Pair<Int, Int>): Polar {
        val (x, y) = first - base.first to second - base.second
        val angle = atan2(y * 1.0, x * 1.0) + if (x < 0) -PI / 2 else 0.0
        val distance = x * x + y * y
        return Polar(angle, distance)
    }

    data class Polar(val angle: Double, val distance: Int)
}