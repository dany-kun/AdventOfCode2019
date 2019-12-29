package advent

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class Day17(private val emitter: suspend (List<List<Char>>) -> Unit = { }) : Day {
    override suspend fun execute1() {
        val machine = IntCodeMachine()
        var input = Instruction.Output.Input(0, loadFile("day17.txt").first().split(","),
                IntCodeInput(), 0, emptyMap())
        val output = mutableListOf<Char>()
        loop@ while (true) {
            input = when (val out = machine.runInstructions(input)) {
                is IntCodeMachine.Result.Output -> {
                    output.add(out.value.toChar())
                    out.input
                }
                IntCodeMachine.Result.Terminal -> break@loop
            }
        }
        val printedMap = output.joinToString("")
        val table = printedMap.toCoordinates()
        val paths = table.filter { it.value == '#' }.keys
        val intersections = intersections(paths)
        println(intersections.sumBy { it.first * it.second })
    }

    private fun String.toCoordinates() =
            lines().mapIndexed { row, s -> s.mapIndexed { col, c -> (row to col) to c } }.flatten().toMap()

    override suspend fun execute2() {
        createMap(loadFile("day17.txt").first().split(",").toMutableList().apply { set(0, "2") })
                .collect { emitter(it) }
    }

    private fun createMap(intCode: List<String>): Flow<List<List<Char>>> {
        val machine = IntCodeMachine()
        var output = listOf(listOf<Char>())
        val inputValues = ScaffoldInput { output }
        var input = Instruction.Output.Input(0,
                intCode, inputValues, 0, emptyMap())

        return flow {
            loop@ while (true) {
                input = when (val out = machine.runInstructions(input)) {
                    is IntCodeMachine.Result.Output -> {
                        if (inputValues.instructionsOn) {
                            println(out.value)
                            // emit(output)
                        }
                        output = if (out.value == 10.0) {
                            output + listOf(emptyList())
                        } else {
                            val updatedRow: List<Char> = output.last().plus(out.value.toChar())
                            output.dropLast(1) + listOf(updatedRow)
                        }
                        out.input
                    }
                    IntCodeMachine.Result.Terminal -> break@loop
                }
            }
        }
    }

}

private fun intersections(paths: Set<Pair<Int, Int>>): List<Pair<Int, Int>> {
    return paths.filter { (x, y) ->
        val adjacent = setOf(x to y + 1, x to y - 1, x - 1 to y, x + 1 to y)
        paths.containsAll(adjacent)
    }
}

class ScaffoldInput(private val scaffold: () -> List<List<Char>>) : IntCodeInput {

    private val queue: IntCodeInput by lazy { InputQueue(computeInputs(scaffold())) }

    var instructionsOn = false

    private fun computeInputs(scaffold: List<List<Char>>): Iterable<Int> {
        val instructions = findPath(scaffold)
        val abc = split(instructions, instructions, emptyList())
        val a = abc[0]
        val b = abc[1]
        val c = abc[2]
        val cmds = findCmdSequence(instructions, a, b, c)
        return listOf(cmds.joinToString(",").map { it.toInt() },
                toIntCmd(a),
                toIntCmd(b),
                toIntCmd(c))
                .flatMap { it.plus('\n'.toInt()) }
                .plus('n'.toInt())
                .plus('\n'.toInt())
    }

    private fun toIntCmd(a: List<Instruction>): List<Int> {
        return a.flatMap {
            when (it) {
                Instruction.Turn.Left -> "L,"
                Instruction.Turn.Right -> "R,"
                is Instruction.Move -> "${it.count},"
            }.map { it.toInt() }
        }.dropLast(1)
    }

    private fun findPath(scaffold: List<List<Char>>): MutableList<Instruction> {
        val scaffoldPoints = scaffold.withIndex().flatMap { (index, value) ->
            value.mapIndexedNotNull { col, c ->
                if (c != '#') return@mapIndexedNotNull null
                col to index
            }
        }.toSet()
        val (robotX, robotY) = scaffold.withIndex().flatMap { (index, value) ->
            value.mapIndexedNotNull { col, it ->
                if (it == '^' || it == '>' || it == '<' || it == 'v') return@mapIndexedNotNull col to index
                return@mapIndexedNotNull null
            }
        }.single()
        val direction = when (val r = scaffold[robotY][robotX]) {
            '^' -> Direction.TOP
            '>' -> Direction.RIGHT
            '<' -> Direction.LEFT
            'v' -> Direction.DOWN
            else -> throw IllegalArgumentException("Unknown robot direction: $r")
        }
        var robot = Robot(direction, robotX, robotY)
        val visitedPoints = mutableSetOf<Pair<Int, Int>>()
        val instructions = mutableListOf<Instruction>()

        while (true) {
            val (movedRobot, moved) = moveInstruction(scaffoldPoints, robot, emptyList())
            if (moved.isEmpty()) {
                val turn = turnInstruction(scaffoldPoints, robot) ?: break
                robot = Robot(turn.first, robot.x, robot.y)
                instructions.add(turn.second)
            } else {
                visitedPoints.addAll(moved.toSet())
                instructions.add(Instruction.Move(moved.size))
                robot = movedRobot
            }
        }
        require(visitedPoints.containsAll(scaffoldPoints))
        return instructions
    }

    private fun findCmdSequence(instructions: List<Instruction>, a: List<Instruction>, b: List<Instruction>, c: List<Instruction>): List<Char> {
        val cmds = mutableListOf<Char>()
        var remaining = instructions
        while (remaining.isNotEmpty()) {
            remaining = when {
                remaining.take(a.size) == a -> {
                    cmds.add('A')
                    remaining.drop(a.size)
                }
                remaining.take(b.size) == b -> {
                    cmds.add('B')
                    remaining.drop(b.size)
                }
                remaining.take(c.size) == c -> {
                    cmds.add('C')
                    remaining.drop(c.size)
                }
                else -> TODO()
            }
        }
        return cmds
    }

    private fun split(original: List<Instruction>, instructions: List<Instruction>, sets: List<List<Instruction>>): List<List<Instruction>> {
        if (instructions.isEmpty()) return sets
        return when {
            sets.isEmpty() -> {
                val cmd = instructions.take(10)
                split(original, instructions, sets.plusElement(cmd))
            }
            instructions.take(sets.first().size) == sets.first() -> split(original, instructions.drop(sets.first().size), sets)
            sets.getOrNull(1) == null -> {
                val cmd = instructions.take(10)
                split(original, instructions, sets.plusElement(cmd))
            }
            instructions.take(sets[1].size) == sets[1] -> split(original, instructions.drop(sets[1].size), sets)
            sets.getOrNull(2) == null -> {
                val cmd = instructions.take(10)
                split(original, instructions, sets.plusElement(cmd))
            }
            instructions.take(sets[2].size) == sets[2] -> split(original, instructions.drop(sets[2].size), sets)
            sets[2].size == 1 -> {
                if (sets[1].size == 1) {
                    if (sets[0].size == 1) throw IllegalStateException("Could not find a correct split")
                    else split(original, original, listOf(sets[0].dropLast(1)))
                } else {
                    split(original, original, listOf(sets[0], sets[1].dropLast(1)))
                }
            }
            else -> split(original, original, listOf(sets[0], sets[1], sets[2].dropLast(1)))
        }
    }


    private fun turnInstruction(scaffold: Set<Pair<Int, Int>>, robot: Robot): Pair<Direction, Instruction.Turn>? {
        val x = robot.x
        val y = robot.y
        when (robot.direction) {
            Direction.LEFT -> if (scaffold.contains(x to y + 1)) {
                return Direction.DOWN to Instruction.Turn.Left
            } else if (scaffold.contains(x to y - 1)) {
                return Direction.TOP to Instruction.Turn.Right
            }
            Direction.TOP -> if (scaffold.contains(x - 1 to y)) {
                return Direction.LEFT to Instruction.Turn.Left
            } else if (scaffold.contains(x + 1 to y)) {
                return Direction.RIGHT to Instruction.Turn.Right
            }
            Direction.RIGHT -> if (scaffold.contains(x to y - 1)) {
                return Direction.TOP to Instruction.Turn.Left
            } else if (scaffold.contains(x to y + 1)) {
                return Direction.DOWN to Instruction.Turn.Right
            }
            Direction.DOWN -> if (scaffold.contains(x + 1 to y)) {
                return Direction.RIGHT to Instruction.Turn.Left
            } else if (scaffold.contains(x - 1 to y)) {
                return Direction.LEFT to Instruction.Turn.Right
            }
        }
        return null
    }

    private fun moveInstruction(scaffoldPoints: Set<Pair<Int, Int>>, robot: Robot, visited: List<Pair<Int, Int>>): Pair<Robot, List<Pair<Int, Int>>> {
        val x = robot.x
        val y = robot.y
        val step = when (robot.direction) {
            Direction.LEFT -> x - 1 to y
            Direction.TOP -> x to y - 1
            Direction.RIGHT -> x + 1 to y
            Direction.DOWN -> x to y + 1
        }
        val nextPosition = scaffoldPoints.find { it == step }
        return if (nextPosition != null) {
            moveInstruction(scaffoldPoints, Robot(robot.direction, step.first, step.second), visited.plusElement(step))
        } else {
            robot to visited
        }

    }

    sealed class Instruction {
        sealed class Turn : Instruction() {
            object Left : Turn() {
                override fun toString(): String {
                    return "L"
                }
            }

            object Right : Turn() {
                override fun toString(): String {
                    return "R"
                }
            }
        }

        data class Move(val count: Int) : Instruction() {
            override fun toString(): String {
                return "$count"
            }
        }
    }

    data class Robot(val direction: Direction, val x: Int, val y: Int)

    enum class Direction {
        LEFT, TOP, RIGHT, DOWN
    }

    override fun next(): Int {
        instructionsOn = true
        return queue.next()
    }

}
