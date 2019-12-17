package advent

typealias WeightedElement = Pair<Int, String>

class Day14 : Day {

    override fun execute1() {
        val result = findOreCount(1)
        println(result)
    }

    private fun findOreCount(fuelCount: Long): Long {
        val reactions: Map<WeightedElement, List<WeightedElement>> = loadFile("day14.txt").map {
            it.split(" =>").let { it.last().trim().asWeighted to it.first().split(", ").map { it.asWeighted } }
        }.toMap()
        val ordersOfOperations = findOrdersOfOperations(reactions, listOf(setOf("ORE")))
        return computeAmount(reactions, ordersOfOperations, mapOf("FUEL" to fuelCount))
    }

    private fun computeAmount(reactions: Map<Pair<Int, String>, List<Pair<Int, String>>>,
                              order: List<Set<String>>,
                              current: Map<String, Long>): Long {
        if (current.keys.singleOrNull() == "ORE") return current.values.first()
        val mostComplex = current.entries.maxBy { (k, _) -> order.indexOfFirst { it.contains(k) } }!!
        val reaction = reactions.entries.first { it.key.second == mostComplex.key }
        val factor = ((mostComplex.value - 1) / reaction.key.first + 1)

        val remainingElements = current.minus(mostComplex.key)
        val newElements = reaction.value.map { it.first * factor to it.second }.associate { it.second to it.first }

        // val operation = (mostComplex.value * factor to mostComplex.key) to newElements

        return computeAmount(reactions, order, remainingElements.mergeReduce(newElements) { a, b -> a + b })
    }

    private fun <K, V> Map<K, V>.mergeReduce(other: Map<K, V>, reduce: (V, V) -> V = { a, b -> b }): Map<K, V> {
        val result = LinkedHashMap<K, V>(this.size + other.size)
        result.putAll(this)
        for ((key, value) in other) {
            result[key] = result[key]?.let { reduce(value, it) } ?: value
        }
        return result
    }

    private fun findOrdersOfOperations(reactions: Map<Pair<Int, String>, List<Pair<Int, String>>>, result: List<Set<String>>): List<Set<String>> {
        val knownElements = result.flatten().toSet()
        if (knownElements.contains("FUEL")) return result
        val newReactions = reactions.entries.filter { knownElements.containsAll(it.value.map { it.second }) }
        val newElements = newReactions.map { it.key.second }.toSet()
        return findOrdersOfOperations(reactions.minus(newReactions.map { it.key }), result.plus(listOf(newElements)))
    }


    private val String.asWeighted: WeightedElement
        get() = trim().split(" ").let { it.first().toInt() to it.last().trim() }

    override fun execute2() {
        val minimumFuel = 1000000000000 / findOreCount(1).also { println(it) }
        var fuelCount = minimumFuel * 2
        // Use step to make computations faster
        var step = 1000
        while (true) {
            val result = findOreCount(fuelCount)
            if (result < 1000000000000) {
                if (step == 1) {
                    println(fuelCount)
                    break
                } else {
                    fuelCount += step
                    step /= 10
                }
            }
            fuelCount += -step
        }
    }
}