package advent

class Day4 : Day {
    override suspend fun execute1() {
        println(bruteForce(108457, 562041))
    }

    override suspend fun execute2() {
        val result = bruteForce(108457, 562041) {
            "$it".fold(mutableMapOf<Char, Int>()) { acc, el ->
                acc[el] = (acc[el] ?: 0) + 1
                acc
            }.values.any { it == 2 } // Has at least one duplicate which is a pair
        }
        println(result)
    }

    private fun bruteForce(min: Int, max: Int, additionalCondition: (Int) -> Boolean = { true }): Int {
        return (min..max).filter { it.isValid && additionalCondition(it) }.count()
    }

    private val Int.isValid: Boolean
        get() = "$this".toSet().size != 6 && // Have duplicate
                "$this".zipWithNext().fold(true) { acc, (a, b) -> acc && a.toInt() <= b.toInt() } // Is increasing number
}