package advent

class Day6 : Day {

    override fun execute1() {
        val (graph, unlinkNodes) = buildGraph("COM")
        require(unlinkNodes.isEmpty())
        println(countGraph(0, "COM", graph))
    }

    private fun countGraph(distance: Int, node: String, graph: MutableMap<String, MutableList<String>>): Int {
        val links = graph[node] ?: return distance + 1
        return distance + links.map { el -> countGraph(distance + 1, el, graph) }.sum()
    }

    private fun buildGraph(start: String): Pair<MutableMap<String, MutableList<String>>, MutableList<Pair<String, String>>> {
        return loadFile("day6.txt")
                .fold(mutableMapOf(start to mutableListOf<String>()) to mutableListOf()) { (cachedCount, unknownNodes), line ->
                    val (left, right) = line.split(")").run { first() to last() }
                    // Already traversed node
                    if (cachedCount[left] != null) {
                        cachedCount[left]!!.add(right)

                        val linkedNodes = linkUnknownNodeToGraph(right, unknownNodes)

                        cachedCount.putAll(linkedNodes)
                        // New node
                    } else {
                        unknownNodes.add(left to right)
                    }
                    cachedCount to unknownNodes
                }
    }

    private fun linkUnknownNodeToGraph(newLinkedNode: String, unknownNodes: MutableList<Pair<String, String>>): Map<String, MutableList<String>> {
        val newLinks = unknownNodes.filter { it.first == newLinkedNode }.map { it.second }.toMutableList()
        if (newLinks.isEmpty()) return mapOf(newLinkedNode to mutableListOf())
        // Remove processed/re-attached node
        unknownNodes.removeAll { it.first == newLinkedNode }
        val missing = newLinks.flatMap { linkUnknownNodeToGraph(it, unknownNodes).entries }.associate { it.toPair() }
        return mapOf(newLinkedNode to newLinks).plus(missing)
    }

    override fun execute2() {
        val (graph, unlinkNodes) = buildGraph("COM")
        require(unlinkNodes.isEmpty())
        findLinks(graph)
    }

    private fun findLinks(graph: Map<String, List<String>>) {
        var count = 0
        var leafs = listOf("YOU")
        while (true) {
            if (leafs.isEmpty()) break
            val linkedNodes = leafs
                    .flatMap { leaf ->
                        // Add all connections to a leaf
                        graph.entries.filter { it.value.contains(leaf) }.map { it.key }.plus(graph[leaf]!!)
                    }
                    .toSet()
            // If last connection was found
            if (graph.any { linkedNodes.contains(it.key) && it.value.contains("SAN") }) {
                break
            } else {
                count += 1
                leafs = linkedNodes.filter { graph[it]!!.isNotEmpty() }
            }
        }
        println(count)

    }
}