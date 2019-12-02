package advent

class Day1 : Day {
    override fun execute1() {
        println(compute(false))
    }

    private fun compute(b: Boolean): Int {
        val lines = loadFile("day1.txt")
        return lines.fold(0) { acc, s ->
            acc + toFuel(s.toInt(), b)
        }
    }

    private fun toFuel(mass: Int, addFuelMass: Boolean = false): Int {
        val requiredFuel = (mass / 3 - 2)
        if (!addFuelMass) return requiredFuel
        if (requiredFuel <= 0) return 0
        return requiredFuel + toFuel(requiredFuel, addFuelMass)
    }

    override fun execute2() {
        println(compute(true))
    }
}