package advent

import kotlinx.coroutines.flow.*

class Day13(private val emitItem: suspend (UI) -> Unit = {}) : Day {

    sealed class UI {
        data class Board(val map: Map<Int, Map<Int, Int>>) : UI()
        data class Score(val value: String, val map: Map<Int, Map<Int, Int>>) : UI()
    }

    override suspend fun execute1() {
        val input = loadFile("day13.txt").first().split(",")

        var latest: Map<Int, Map<Int, Int>>? = null

        loadMap(input)
                .filter { it is UI.Board }
                .onEach { latest = (it as UI.Board).map }
                .collect()

        println(latest!!.entries.sumBy { it.value.count { it.value == 2 } })
    }

    override suspend fun execute2() {
        val input = loadFile("day13.txt").first().split(",").toMutableList().apply { set(0, "2") }.toList()
        loadMap(input, true)
                .onEach { emitItem(it) }
                .collect()
    }

    private fun loadMap(input: List<String>,
                        showScore: Boolean = false): Flow<UI> {
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
                            emit(UI.Board(board))
                        }
                        outputs.add(out.value.toInt())
                        if (outputs.size == 3) {
                            val x = outputs.first()
                            val y = outputs[1]
                            val value = outputs.last()
                            outputs.clear()
                            if (showScore && x == -1 && y == 0) {
                                println(value)
                                emit(UI.Score(value.toString(), board))
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

        const val WALL_VALUE = 1
        const val BRICK_VALUE = 2
        const val PADDLE_VALUE = 3
        const val BALL_VALUE = 4

        fun hasBallAndPaddle(map: Map<Int, Map<Int, Int>>) =
                map.any { it.value.any { it.value == Day13.BALL_VALUE } } &&
                        map.any { it.value.any { it.value == Day13.PADDLE_VALUE } }

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


}