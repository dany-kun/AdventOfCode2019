package advent.dataviz

import advent.Day13
import advent.Day13.Companion.toPixelValue
import io.data2viz.color.Colors
import io.data2viz.viz.GroupNode
import io.data2viz.viz.HasStroke
import io.data2viz.viz.RectNode
import io.data2viz.viz.bindRendererOn
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import kotlin.browser.document

private const val CELL_SIZE = 30.0

fun showDay13Viz(root: HTMLDivElement) {
    val canvas = document.createElement("canvas") as HTMLCanvasElement
    canvas.setAttribute("width", "500")
    canvas.setAttribute("height", "500")
    canvas.width = 500
    canvas.height = 500

    root.appendChild(canvas)

    val scope = MainScope()

    scope.launch {
        flow {
            Day13 { emit(it) }.execute2()
        }
                .take(300)
                .map {
                    delay(1)
                    it
                }
                .onEach { renderGame(it, canvas) }
                .collect()
    }
}


private fun renderGame(map: Map<Int, Map<Int, Int>>, canvas: HTMLCanvasElement) {
    io.data2viz.viz.viz {
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
    }.bindRendererOn(canvas)
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
