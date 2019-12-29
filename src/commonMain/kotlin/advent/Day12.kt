package advent

import kotlin.math.abs

class Day12 : Day {


    data class Coordinates(val x: Int, val y: Int, val z: Int)

    data class Moon(val position: Coordinates, val velocity: Coordinates)

    private fun move(position: Coordinates, velocity: Coordinates): Coordinates {
        return Coordinates(position.x + velocity.x,
                position.y + velocity.y,
                position.z + velocity.z)
    }

    override suspend fun execute1() {
        val result = (0 until 1000).fold(loadMoons()) { moons, _ ->
            moons.map { moon ->
                val gravity = Coordinates(
                        moons.sumBy { (it.position.x - moon.position.x).sign },
                        moons.sumBy { (it.position.y - moon.position.y).sign },
                        moons.sumBy { (it.position.z - moon.position.z).sign })
                val velocity = Coordinates(moon.velocity.x + gravity.x, moon.velocity.y + gravity.y, moon.velocity.z + gravity.z)
                val position = move(moon.position, velocity)
                Moon(position, velocity)
            }
        }
        println(result.energy)

    }

    override suspend fun execute2() {
        val moons = loadMoons()
        val x = extractCountDimension(moons) { it.x }.toDouble()
        val y = extractCountDimension(moons) { it.y }.toDouble()
        val z = extractCountDimension(moons) { it.z }.toDouble()
        println(lcm(x, lcm(y, z)))
    }

    private fun lcm(a: Double, b: Double) = a * b * 1.0 / gcd(a, b)
    private fun gcd(a: Double, b: Double): Double = if (b == 0.0) a else gcd(b, a.rem(b))


    private fun extractCountDimension(moons: List<Moon>, axis: (Coordinates) -> Int): Int {
        val xs = moons.map { axis(it.position) to axis(it.velocity) }

        var stepCount = 0
        var state = xs

        while (true) {
            val gravities = (xs.indices).map {
                val x = state[it].first
                state.sumBy { (it.first - x).sign }
            }
            val velocities = state.mapIndexed { index, (_, v) -> v + gravities[index] }

            state = velocities.mapIndexed { index, v -> state[index].first + v to v }
            stepCount += 1
            if (state == xs) break
        }
        println(state)
        return stepCount
    }

    private suspend fun loadMoons(): List<Moon> {
        val positions = loadFile("day12.txt").map {
            val values = it.dropLast(1).drop(1).split(",").map { it.split("=")[1] }
            Coordinates(values[0].toInt(), values[1].toInt(), values[2].toInt())
        }
        return positions.map { Moon(it, Coordinates(0, 0, 0)) }
    }

    private val List<Moon>.energy: Int
        get() = sumBy { energy(it.position) * energy(it.velocity) }

    private fun energy(coordinates: Coordinates) = abs(coordinates.x) + abs(coordinates.y) + abs(coordinates.z)

    private val Int.sign: Int
        get() = if (this > 0) 1 else if (this < 0) -1 else 0

}