package advent

import java.lang.Math.pow
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.tan

class Day10 : Day {
    override fun execute1() {
        val asteroids = loadMap()
        val result = asteroids.maxBy { countVisibleAsteroid(it, asteroids) }!!
        println(result)
        println(countVisibleAsteroid(result, asteroids))
    }

    private fun loadMap(): List<Pair<Int, Int>> {
        val asteroids = loadFile("day10.txt").withIndex().flatMap { value ->
            value.value.mapIndexedNotNull { x, c ->
                if (c != '#')
                    null
                else
                    x to value.index
            }
        }
        return asteroids
    }

    private fun countVisibleAsteroid(base: Pair<Int, Int>, asteroids: List<Pair<Int, Int>>): Int {
        val a = asteroids
                .mapNotNull { if (it == base) null else (it.first - base.first) to (it.second - base.second) }
                .sortedBy { pow(it.first.toDouble(), 2.0) + pow(it.second.toDouble(), 2.0) }
        val distances = a.toMutableList()
        var index = 0
        while (index < distances.size) {
            val (x0, y0) = distances[index]
            val hiddenAsteroidIndices = (index + 1 until distances.size)
                    .filter {
                        val (x1, y1) = distances[it]
                        (if (x1 != 0 && x0 != 0 && y1 != 0 && y0 != 0) {
                            tan((y1 * 1.0 / x1)) == tan((y0 * 1.0 / x0)) && y1.sign == y0.sign && x1.sign == x0.sign
                            // x1 * 1.0F / x0 == y1 * 1.0F / y0
                        } else if (x1 == 0 && x0 == 0) {
                            y1.sign == y0.sign
                        } else y1 == 0 && y0 == 0 && x1.sign == x0.sign)
                                .also {
                                    if (it) {
                                        val point = x1 + base.first to y1 + base.second
                                        val overseen = x0 + base.first to y0 + base.second
                                        println("Removing $point because of $overseen")
                                    }
                                }
                    }
                    // Reverse to be able to remove from the last index
                    .reversed()
            for (k in hiddenAsteroidIndices) distances.removeAt(k)
            index += 1
        }
        println("$base -> $a -> $distances -> ${distances.count()}")
        return distances.count()
    }

    override fun execute2() {
        // val base = 23 to 29
        val base = 11 to 13
        val asteroids = loadMap().filter { it != base }
        val sorted = asteroids.sortedWith(Comparator { o1, o2 -> o1.toPolar(base).angle.compareTo(o2.toPolar(base).angle) })
        println(sorted)
        println(sorted.map { it.toPolar(base) })
    }

    private fun Pair<Int, Int>.toPolar(base: Pair<Int, Int>): Polar {
        val (x, y) = first - base.first to second - base.second
        val angle = atan2(y * 1.0, x * 1.0)// + if (x < 0) -PI else 0.0
        val distance = x * x + y * y
        return Polar(angle, distance)
    }

    data class Polar(val angle: Double, val distance: Int) : Comparable<Polar> {
        override fun compareTo(other: Polar): Int {
            return if (other.angle != angle) {
                angle.compareTo(other.angle)
            } else {
                distance.compareTo(other.distance)
            }
        }
    }

    private val Int.sign: Int
        get() = if (this > -0) 1 else -1
}