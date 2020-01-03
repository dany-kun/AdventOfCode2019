package advent.dataviz

import advent.Day18
import io.data2viz.viz.bindRendererOn
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import kotlin.dom.appendElement

fun renderDay18Viz(root: HTMLDivElement) {
    val scope = MainScope()

    val canvas = root.appendElement("canvas") {
        setAttribute("height", "900")
        setAttribute("width", "900")
    } as HTMLCanvasElement

    scope.launch {
        flow {
            Day18 { emit(it) }.execute1()
        }.map { DataVizDay18(it).viz }
                .onEach { delay(1000) }
                .take(1)
                .collect { it.bindRendererOn(canvas) }
    }
}