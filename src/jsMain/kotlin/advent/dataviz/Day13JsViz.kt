package advent.dataviz

import advent.Day13
import io.data2viz.viz.bindRendererOn
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.html.InputType
import kotlinx.html.canvas
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.input
import kotlinx.html.js.div
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document

fun showDay13Viz(root: HTMLDivElement) {
    drawMainUI(root)
}

private fun drawMainUI(root: HTMLDivElement) {
    var showAllMoves = false
    root.append.div {
        attributes["style"] = "display: flex; flex-direction: column; justify-content: center; align-items: center;"
        input {
            id = "show-all"
            type = InputType.checkBox
            checked = showAllMoves
            +"Show all"
        }
        canvas { id = "board" }
        canvas { id = "score" }
    }
    val canvas = (document.getElementById("board") as HTMLCanvasElement)
    val scoreCanvas = (document.getElementById("score") as HTMLCanvasElement)
    val showAll = document.getElementById("show-all") as HTMLInputElement
    showAll.addEventListener("change", { showAllMoves = it.target?.asDynamic().checked as Boolean })

    root.appendChild(canvas)
    root.appendChild(scoreCanvas)

    val scope = MainScope()

    scope.launch {
        flow {
            Day13 { emit(it) }.execute2()
        }
                .transform {
                    when (it) {
                        is Day13.UI.Board -> if (Day13.hasBallAndPaddle(it.map) && showAllMoves) {
                            val game = day13BoardViz(it.map).apply { bindRendererOn(canvas) }
                            emit(game)
                        }
                        is Day13.UI.Score -> {
                            val day13ScoreViz = day13ScoreViz(it.value).apply { bindRendererOn(scoreCanvas) }
                            emit(day13ScoreViz)
                            if (!showAllMoves) {
                                val game = day13BoardViz(it.map).apply { bindRendererOn(canvas) }
                                emit(game)
                            }
                        }
                    }
                }
                .onEach { delay(1) }
                .collect { it.render() }
    }
}
