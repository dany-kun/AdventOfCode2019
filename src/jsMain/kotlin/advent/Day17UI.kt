package advent

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.css.*
import react.*
import styled.css
import styled.styledDiv

interface AppProps : RProps

val Day17UI by functionalComponent<AppProps> {
    val scope = MainScope()

    val pixels = useState(emptyList<List<Char>>())

    useEffectWithCleanup(emptyList()) {
        if (pixels.first.isEmpty()) {
            scope.launch {
                cameraFrames().collect {
                    pixels.second(it)
                }
            }
        }
        return@useEffectWithCleanup {
            console.log("stop")
            scope.cancel()
        }
    }
    styledDiv {
        css {
            display = Display.grid
        }
        pixels.first.withIndex().flatMap { (index, value) ->
            value.mapIndexed { col, v -> pixel(v.toString(), index to col) }
        }
    }
}

fun RBuilder.pixel(value: String, position: Pair<Int, Int>) {
    styledDiv {
        key = position.toString()
        css {
            gridColumn = GridColumn("${position.second + 1} / span 1")
            gridRow = GridRow("${position.first + 1} / span 1")
        }
        +value
    }
}

private suspend fun cameraFrames(): Flow<List<List<Char>>> {
    return flow {
        Day17 {
            emit(it)
        }.execute2()
    }
            .map {
                delay(1000)
                it
            }
}
