package advent.dataviz

import io.data2viz.color.Colors
import io.data2viz.viz.*

private const val CELL_SIZE = 30.0

fun day13BoardViz(map: Map<Int, Map<Int, Int>>): Viz {
    return viz {
        group {
            width = ((map.flatMap { it.value.keys }.max() ?: 0) + 1) * CELL_SIZE
            height = ((map.keys.max() ?: 0) + 1) * CELL_SIZE

            map.entries.forEach { (y, row) ->
                row.forEach { (x, value) ->
                    val x = x * CELL_SIZE
                    val y = y * CELL_SIZE
                    when (value) {
                        0 -> rect(x, y)
                        1 -> rect(x, y) { fill = Colors.rgb(0x00FF00) }
                        2 -> rect(x, y) { fill = Colors.rgb(0x00FFFF) }
                        3 -> rect(x, y) { fill = Colors.rgb(0xFFFF00) }
                        4 -> circle {
                            this.x = x + CELL_SIZE / 2
                            this.y = y + CELL_SIZE / 2
                            this.radius = CELL_SIZE / 2
                            fill = Colors.rgb(0x22FF33)
                        }
                        else -> throw IllegalStateException("Unknown code $this")
                    }
                }
            }
        }
    }
}

fun day13ScoreViz(score: String): Viz {
    return viz {
        width = 300.0
        height = 50.0
        group {
            text {
                this.x = width / 2
                this.y = height / 2
                textAlign = textAlign(horizontal = TextHAlign.MIDDLE, vertical = TextVAlign.MIDDLE)
                textContent = "Score: $score"
                fontSize = 20.0
                textColor = Colors.Web.purple
            }

        }
    }
}

private fun GroupNode.rect(x: Double, y: Double, block: RectNode.() -> Unit = {}) {
    rect {
        this.x = x
        this.y = y
        width = CELL_SIZE
        height = CELL_SIZE
        block()
    }
}
