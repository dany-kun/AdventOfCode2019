package advent.dataviz

import advent.Day13
import io.data2viz.viz.JFxVizRenderer
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.VBox
import javafx.stage.Stage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


internal class Day13DataVizJavaFx : Application() {

    private val mainScope = MainScope()

    private val canvas = Canvas(1500.0, 700.0)
    private val score = Canvas(1500.0, 100.0)

    override fun start(p0: Stage?) {
        val group = VBox().apply {
            alignment = Pos.CENTER
        }
        group.children.add(canvas)
        group.children.add(score)
        mainScope.launch {
            renderDay13()
                    .onEach { delay(1) }
                    .collect { it.render() }
        }
        p0?.scene = Scene(group, 1500.0, 1000.0)
        p0?.show()
    }

    override fun stop() {
        super.stop()
        mainScope.cancel()
    }

    private fun renderDay13(): Flow<JFxVizRenderer> {
        return flow {
            Day13 {
                emit(it)
            }.execute2()
        }
                .transform {
                    when (it) {
                        is Day13.UI.Board -> {
                            if (Day13.hasBallAndPaddle(it.map)) {
                                val viz = day13BoardViz(it.map)
                                emit(JFxVizRenderer(canvas, viz))
                            }
                        }
                        is Day13.UI.Score -> {
                            val viz = day13ScoreViz(it.value)
                            emit(JFxVizRenderer(score, viz))
                        }
                    }

                }
    }

    companion object {
        fun launch() {
            launch(Day13DataVizJavaFx::class.java)
        }
    }

}




