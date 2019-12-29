package advent

class Day8 : Day {
    override suspend fun execute1() {
        val input = loadFile("day8.txt").first()
        val layerSize = 25 * 6
        val layers = input.chunked(layerSize)
        val layer = layers.minBy { it.count { it == '0' } }!!
        val result = layer.count { it == '1' } * layer.count { it == '2' }
        println(result)
    }

    override suspend fun execute2() {
        val input = loadFile("day8.txt").first()
        val layerSize = 25 * 6
        val layersCount = input.count() / layerSize
        val finalImage = (0 until layerSize).map { position ->
            val layer = (0 until layersCount).firstOrNull { layer -> input[position + layer * layerSize] != '2' } ?: 0
            input[position + layer * layerSize]
        }
        finalImage.chunked(25).forEach {
            println(it.map { when (it) {
                '1' -> 'X'
                '0' -> ' '
                else -> TODO()
            } })
        }
    }
}