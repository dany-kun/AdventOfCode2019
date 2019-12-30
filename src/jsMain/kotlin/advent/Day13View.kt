package advent

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.css.Display
import kotlinx.css.display
import org.w3c.dom.HTMLDivElement
import react.*
import react.dom.render
import styled.css
import styled.styledDiv

fun renderDay13(root: HTMLDivElement?) {
    render(root) {
        child(Day13UI)
    }
}

private interface Day13Props : RProps

private val Day13UI by functionalComponent<Day13Props> {
    val scope = MainScope()

    val pixels = useState(emptyMap<Int, Map<Int, Int>>())

    useEffectWithCleanup(emptyList()) {
        if (pixels.first.isEmpty()) {
            scope.launch {
                Day13 {
                     delay(1)
                     pixels.second(it)
                }.execute2()
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
        pixels.first.entries.flatMap { (y, v) ->
            v.entries.map { (x, v) ->
                val value = with(Day13) { v.toPixelValue() }
                pixel(value, y to x)
            }
        }
    }
}
