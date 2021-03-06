package advent

import advent.dataviz.renderDay18Viz
import org.w3c.dom.HTMLDivElement
import react.FunctionalComponent
import kotlin.browser.document

import kotlin.reflect.KProperty

fun main() {
    val root = document.getElementById("content") as HTMLDivElement
    renderDay18Viz(root)
}


// https://github.com/JetBrains/kotlin-wrappers/issues/125
operator fun <P> FunctionalComponent<P>.getValue(thisRef: Any?, property: KProperty<*>): FunctionalComponent<P> {
    this.asDynamic().displayName = property.name.capitalize()
    return this
}