package advent.dataviz

import advent.Day18
import io.data2viz.color.Colors
import io.data2viz.force.ForceLink
import io.data2viz.force.ForceNode
import io.data2viz.force.Link
import io.data2viz.force.forceSimulation
import io.data2viz.geom.Point
import io.data2viz.geom.point
import io.data2viz.geom.size
import io.data2viz.math.pct
import io.data2viz.viz.*
import kotlin.math.sqrt

class DataVizDay18(private val ui: Day18.UI) : DataViz {
    override val viz: Viz
        get() = when (ui) {
            is Day18.UI.Graph -> createViz(removeRedundantLinks(ui.graph))
        }

    private fun removeRedundantLinks(graph: Map<Day18.Cell, Map<Day18.Cell, Int>>): Map<Day18.Cell, Map<Day18.Cell, Int>> {
        return graph.entries.fold(emptyMap()) { acc, el ->
            val newValue = el.value.filterKeys { !acc.contains(it) }
            acc.plus(el.key to newValue)
        }
    }

    data class Item(val cell: Day18.Cell, val parents: List<Pair<Day18.Cell, Int>> = listOf())


    private fun createViz(graph: Map<Day18.Cell, Map<Day18.Cell, Int>>): Viz {
        val graphSide = 900
        val items = graph.entries.map { Item(it.key, it.value.map { it.key to it.value }) }
        val particles = mutableListOf<TextNode>()
        val particleNodes = mutableListOf<CircleNode>()
        val particleLinks = mutableListOf<LineNode>()

        val maxDistance = graph.flatMap { it.value.values }.max()!!

        var drag = false
        var clickedNode: ForceNode<Item>? = null
        var clickedNodePos: Point

        lateinit var forceLink: ForceLink<Item>
        val simulation = forceSimulation<Item> {
            forceLink = forceLink {
                linkGet = {
                    domain.parents.map {
                        Link(this,
                                nodes.find { n -> n.domain.cell == it.first }!!,
                                it.second.toDouble() * graphSide / (maxDistance * 2.0))
                    }
                }
            }
            forceCenter {
                center = point(graphSide / 2, graphSide / 2)
            }
            domainObjects = items
        }
        return viz {
            size = size(graphSide, graphSide)
            forceLink.links.forEach {
                particleLinks += line {
                    x1 = it.source.x
                    y1 = it.source.y
                    x2 = it.target.x
                    x2 = it.target.y
                    stroke = Colors.Web.black.withAlpha(20.pct)
                }
            }
            simulation.nodes.forEach {
                particleNodes += circle {
                    fill = if (it.domain.cell is Day18.Cell.Door) Colors.Web.red else Colors.Web.black
                    radius = 2.0 + (it.domain.parents.size / 2.0)
                }
                particles += text {
                    fill = Colors.Web.black
                    textContent = it.domain.cell.label
                    textAlign = textAlign(TextHAlign.MIDDLE, TextVAlign.MIDDLE)
                    fontWeight = FontWeight.BOLD
                }
            }
            animation {
                simulation.nodes.forEach { node ->
                    particles[node.index].x = node.x
                    particles[node.index].y = node.y - 15.0
                    particleNodes[node.index].x = node.x
                    particleNodes[node.index].y = node.y
                }
                forceLink.links.forEachIndexed { index, link ->
                    particleLinks[index].x1 = link.source.x
                    particleLinks[index].x2 = link.target.x
                    particleLinks[index].y1 = link.source.y
                    particleLinks[index].y2 = link.target.y
                }
            }
            on(KPointerDown) {
                if (!drag) {
                    clickedNode = simulation.nodes.firstOrNull { node ->
                        val diffX = it.pos.x - node.x
                        val diffY = it.pos.y - node.y
                        sqrt((diffX * diffX) + (diffY * diffY)) < 4.0
                    }
                    clickedNodePos = it.pos
                    drag = clickedNode != null
                }
            }
            on(KPointerUp) {
                drag = false
            }
            on(KPointerMove) {
                if (drag) {
                    clickedNodePos = it.pos
                    clickedNode!!.position = clickedNodePos
                }
            }
        }
    }

    private val Day18.Cell.label: String
        get() = when (this) {
            Day18.Cell.Character -> "@"
            is Day18.Cell.Key -> value
            is Day18.Cell.Door -> value
            Day18.Cell.Path -> ""
        }
}