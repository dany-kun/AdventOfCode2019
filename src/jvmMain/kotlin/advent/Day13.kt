package advent

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

class Day13 : Day {
    override fun execute1() {
        val input = loadFile("day13.txt").first().split(",")

        var latest: Map<Int, Map<Int, Int>>? = null
        runBlocking {
            loadMap(input).onEach { latest = it }
                    .collect()
        }
        println(latest!!.entries.sumBy { it.value.count { it.value == 2 } })
    }

    override fun execute2() {
        val input = loadFile("day13.txt").first().split(",").toMutableList().apply { set(0, "2") }.toList()
        val result = loadMap(input, true)
                .map { drawnMap(it, findBounds(it)) }
                .filter { !it.contains("/") }
                .distinctUntilChanged()
                .drop(1)
                .onEach { println(it) }
        runBlocking { result.collect() }
    }

    private fun findBounds(result: Map<Int, Map<Int, Int>>): Square {
        val (minY, maxY) = result.keys.min()!! to result.keys.max()!!
        val (minX, maxX) = result.entries.flatMap { it.value.keys }.let { it.min()!! to it.max()!! }
        return Square(minX, minY, maxX, maxY)
    }

    data class Square(val left: Int, val top: Int, val right: Int, val bottom: Int)

    private fun loadMap(input: List<String>,
                        showScore: Boolean = false): Flow<Map<Int, MutableMap<Int, Int>>> {
        val machine = IntCodeMachine()

        var state = Instruction.Output.Input(0, input, emptySequence(), 0, emptyMap())
        val map = mutableMapOf<Int, MutableMap<Int, Int>>()
        val outputs = mutableListOf<Int>()
        var gameStarted = false
        return flow {
            loop@ while (true) {
                state = when (val out = machine.runInstructions(state)) {
                    is IntCodeMachine.Result.Output -> {
                        outputs.add(out.value.toInt())
                        if (outputs.size == 3) {
                            val x = outputs.first()
                            val y = outputs[1]
                            val value = outputs.last()
                            outputs.clear()
                            if (showScore && x == -1 && y == 0) {
                                println("Score is $value")
                                if (!gameStarted && value == 0) {
                                    gameStarted = true
                                    Instruction.Output.Input(out.input.pointerPosition,
                                            out.input.sequence,
                                            computeInputs(map),
                                            out.input.base,
                                            out.input.extraMemory
                                    )
                                } else {
                                    out.input
                                }
                            } else {
                                val xs = map.getOrPut(y, { mutableMapOf() })
                                xs[x] = value
                                emit(map)
                                out.input
                            }
                        } else {
                            out.input
                        }
                    }
                    IntCodeMachine.Result.Terminal -> break@loop
                }
            }
        }
    }

    private fun computeInputs(map: MutableMap<Int, MutableMap<Int, Int>>): Sequence<Int> {
        return sequenceOf(0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0)
    }

    private fun drawnMap(result: Map<Int, Map<Int, Int>>, bounds: Square,
                         destroyedBlocks: MutableSet<Pair<Int, Int>> = mutableSetOf()): String {
        val flattenedPositions = result.entries.flatMap { (y, e) -> e.entries.map { (x, v) -> (x to y) to v } }
        val paddle = flattenedPositions.firstOrNull { it.second == 3 }?.first
        val ball = flattenedPositions.firstOrNull { it.second == 4 }?.first
        if (paddle == null || ball == null) return "/"
        return (bounds.top..bounds.bottom).joinToString("\n") { y ->
            (bounds.left..bounds.right).joinToString("") { x ->
                if (destroyedBlocks.contains(x to y)) return@joinToString " "
                when (val tile = result[y]?.get(x)) {
                    0 -> " "
                    1 -> "X"
                    2 -> "*"
                    3 -> "="
                    4 -> "O"
                    null -> "/"
                    else -> throw java.lang.IllegalArgumentException("Unknown tile $tile")
                }
            }
        }
    }


    private fun resetDirection(newPosition: Pair<Int, Int>, limits: Square, direction: Direction): Direction {
        return if (newPosition.first == limits.left && newPosition.second == limits.top) {
            Direction.RIGHT_DOWN
        } else if (newPosition.first == limits.right && newPosition.second == limits.top) {
            Direction.LEFT_DOWN
        } else if (newPosition.first == limits.left && newPosition.second == limits.bottom) {
            Direction.RIGHT_UP
        } else if (newPosition.first == limits.right && newPosition.second == limits.bottom) {
            Direction.LEFT_UP
        } else if (newPosition.first == limits.left) {
            when (direction) {
                Direction.LEFT_DOWN -> Direction.RIGHT_DOWN
                Direction.LEFT_UP -> Direction.RIGHT_UP
                Direction.RIGHT_UP, Direction.RIGHT_DOWN -> TODO()
            }
        } else if (newPosition.first == limits.right) {
            when (direction) {
                Direction.LEFT_DOWN, Direction.LEFT_UP -> TODO()
                Direction.RIGHT_UP -> Direction.LEFT_UP
                Direction.RIGHT_DOWN -> Direction.LEFT_DOWN
            }
        } else if (newPosition.second == limits.top) {
            when (direction) {
                Direction.LEFT_UP -> Direction.LEFT_DOWN
                Direction.RIGHT_UP -> Direction.RIGHT_DOWN
                Direction.RIGHT_DOWN, Direction.LEFT_DOWN -> TODO()
            }
        } else if (newPosition.second == limits.bottom) {
            when (direction) {
                Direction.RIGHT_DOWN -> Direction.RIGHT_UP
                Direction.LEFT_DOWN -> Direction.LEFT_UP
                Direction.RIGHT_UP, Direction.LEFT_UP -> TODO()
            }
        } else {
            direction
        }
    }

    enum class Direction {
        LEFT_DOWN {
            override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
                return position.first - 1 to position.second + 1
            }
        },
        LEFT_UP {
            override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
                return position.first - 1 to position.second - 1
            }
        },
        RIGHT_UP {
            override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
                return position.first + 1 to position.second - 1
            }
        },
        RIGHT_DOWN {
            override fun move(position: Pair<Int, Int>): Pair<Int, Int> {
                return position.first + 1 to position.second + 1
            }
        };

        abstract fun move(position: Pair<Int, Int>): Pair<Int, Int>
    }
//
//    val flattenedPositions = result.entries.flatMap { (y, e) -> e.entries.map { (x, v) -> (x to y) to v } }
//    var (paddleX, paddleY) = flattenedPositions.first { it.second == 3 }.first
//    var ball = flattenedPositions.first { it.second == 4 }.first
//    val blocks = flattenedPositions.filter { it.second == 2 }.map { it.first }.toSet()
//    val paddleMoves = mutableListOf<Int>()
//
//    val destroyedBlocks = mutableSetOf<Pair<Int, Int>>()
//    val limits = Square(bounds.left + 1, bounds.top + 1, bounds.right - 1, paddleY)
//
//    var currentDirection = Direction.RIGHT_DOWN
//
//    var step = 0
//    while (step < 100) {
//        // drawMap(result, bounds, ball, destroyedBlocks, paddleX to paddleY)
//        ball = currentDirection.move(ball)
//        destroyedBlocks.add(ball)
//        if (destroyedBlocks.containsAll(blocks)) break
//        currentDirection = resetDirection(ball, limits, currentDirection)
//        if (ball.second == limits.bottom) {
//            paddleMoves.add(ball.first - paddleX)
//            paddleX = ball.first
//        }
//        step += 1
//    }
////        drawMap(result, bounds, ball, destroyedBlocks, paddleX to paddleY)
//
//    println(paddleMoves)
}