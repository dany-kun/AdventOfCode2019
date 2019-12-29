package advent

import react.FunctionalComponent
import react.child
import react.dom.render
import kotlin.browser.document
import kotlin.reflect.KProperty

fun main() {
    render(document.getElementById("content")) {
        child(Day17UI)
    }
}


// https://github.com/JetBrains/kotlin-wrappers/issues/125
operator fun <P> FunctionalComponent<P>.getValue(thisRef: Any?, property: KProperty<*>): FunctionalComponent<P> {
    this.asDynamic().displayName = property.name.capitalize()
    return this
}