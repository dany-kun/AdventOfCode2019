package advent

class Day6 : Day {

    override fun execute1() {
        buildGraph("COM")
    }

    private fun buildGraph(start: String) {
        val result = loadFile("day6.txt")
                .fold(mutableMapOf(start to 0) to mutableListOf<Pair<String, String>>()) { (cachedCount, unknownNodes), line ->
                    val (left, right) = line.split(")").run { first() to last() }
                    // Already traversed node
                    if (cachedCount[left] != null) {
                        val i = cachedCount[left]!! + 1
                        cachedCount[right] = i

                        val linkedNodes = linkUnknownNodeToGraph(right, i, unknownNodes)

                        cachedCount.putAll(linkedNodes)
                        // New node
                    } else {
                        unknownNodes.add(left to right)
                    }
                    cachedCount to unknownNodes
                }
        require(result.second.isEmpty())
        println(result.first.values.sum())
    }

    private fun linkUnknownNodeToGraph(newLinkedNode: String, i: Int, unknownNodes: MutableList<Pair<String, String>>): List<Pair<String, Int>> {
        val newLinks = unknownNodes.filter { it.first == newLinkedNode }.map { it.second to i + 1 }
        // Remove processed/re-attached node
        if (newLinks.isEmpty()) return newLinks
        unknownNodes.removeAll { it.first == newLinkedNode }
        val missing = newLinks.flatMap { linkUnknownNodeToGraph(it.first, i + 1, unknownNodes) }
        return newLinks + missing
    }

    override fun execute2() {

    }
}