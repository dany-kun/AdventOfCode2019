package advent

class Day6 : Day {

    override fun execute1() {
        val (graph, unlinkNodes) = buildGraph("COM")
        require(unlinkNodes.isEmpty())
        println(countGraph(graph["COM"], graph))
    }

    private fun countGraph(node: MutableList<String>?, graph: MutableMap<String, MutableList<String>>): Int {
        if (node == null) return 0
        return node.size + node.fold(0) { acc, el ->
            acc + countGraph(graph[el], graph)
        }
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
        println(unlinkNodes)
        println(graph)
    }
}