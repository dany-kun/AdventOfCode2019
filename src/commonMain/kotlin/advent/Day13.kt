package advent

import kotlinx.coroutines.flow.*

class Day13(private val emitItem: suspend (Map<Int, Map<Int, Int>>) -> Unit = {}) : Day {
    override suspend fun execute1() {
        val input = loadFile("day13.txt").first().split(",")

        var latest: Map<Int, Map<Int, Int>>? = null

        loadMap(input).onEach { latest = it }
                .collect()

        println(latest!!.entries.sumBy { it.value.count { it.value == 2 } })
    }

    override suspend fun execute2() {
        val input = loadFile("day13.txt").first().split(",").toMutableList().apply { set(0, "2") }.toList()
        loadMap(input, true)
                // .map { drawnMap(it, findBounds(it)) }
                .onEach { emitItem(it) }
                .collect()
    }

    private fun findBounds(result: Map<Int, Map<Int, Int>>): Square {
        val (minY, maxY) = result.keys.min()!! to result.keys.max()!!
        val (minX, maxX) = result.entries.flatMap { it.value.keys }.let { it.min()!! to it.max()!! }
        return Square(minX, minY, maxX, maxY)
    }

    data class Square(val left: Int, val top: Int, val right: Int, val bottom: Int)

    private fun loadMap(input: List<String>,
                        showScore: Boolean = false): Flow<Map<Int, Map<Int, Int>>> {
        val machine = IntCodeMachine()
        var board = mapOf<Int, Map<Int, Int>>()
        val inputValues = GameInput { board }
        var state = Instruction.Output.Input(0, input, inputValues, 0, emptyMap())
        val outputs = mutableListOf<Int>()
        return flow {
            loop@ while (true) {
                state = when (val out = machine.runInstructions(state)) {
                    is IntCodeMachine.Result.Output -> {
                        if (inputValues.gameStarted) {
                            emit(board)
                        }
                        outputs.add(out.value.toInt())
                        if (outputs.size == 3) {
                            val x = outputs.first()
                            val y = outputs[1]
                            val value = outputs.last()
                            outputs.clear()
                            if (showScore && x == -1 && y == 0) {
                                println(value)
                                out.input
                            } else {
                                val newXs = board[y]?.minus(x)?.plus(x to value) ?: mapOf(x to value)
                                board = board.minus(y).plus(y to newXs)
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

    class GameInput(private val game: () -> Map<Int, Map<Int, Int>>) : IntCodeInput {

        var gameStarted = false

        override fun next(): Int {
            gameStarted = true
            val board = game()
            val boardCoordinates = board.entries.flatMap { (y, v) ->
                 v.entries.map { (x, c) -> (x to y) to c }.associate { it }.entries
            }.associate { it.key to it.value }
            val ballX = boardCoordinates.entries.find { it.value == 4 }!!.key.first
            val paddleX = boardCoordinates.entries.find { it.value == 3 }!!.key.first
            return when {
                ballX > paddleX -> 1
                ballX < paddleX -> -1
                else -> 0
            }
        }

    }

    companion object {
        fun Int.toPixelValue(): String {
            return when (this) {
                0 -> " "
                1 -> "X"
                2 -> "*"
                3 -> "="
                4 -> "O"
                else -> throw IllegalStateException("Unknown code $this")
            }
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

}