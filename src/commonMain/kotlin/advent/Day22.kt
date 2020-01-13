package advent

class Day22 : Day {
    override suspend fun execute1() {
        val instructions = loadFile("day22.txt")
                .map { lineParser(it) }
        val deck = List(10007) { it }
        val result = instructions.fold(deck) { acc, el -> el.deal(acc) }
        println(result.indexOf(2019))
    }

    override suspend fun execute2() {
        val instructions = loadFile("day22.txt")
                .map { lineParser(it) }
        val deck = List(10007) { it }
        val result = instructions.reversed().fold(6526) { acc, el -> el.previousPosition(acc, deck.size) }
        println(result)
    }


    private fun lineParser(line: String): Technique {
        return when {
            line.startsWith("cut") -> Technique.Cut(line.drop(4).toInt())
            line.contains("stack") -> Technique.NewStack
            else -> Technique.Increment(line.replace("deal with increment ", "").toInt())
        }
    }

    sealed class Technique {
        object NewStack : Technique() {
            override fun deal(stack: List<Int>): List<Int> {
                return stack.reversed()
            }

            override fun previousPosition(position: Int, stackSize: Int): Int {
                return stackSize - position
            }

        }

        data class Cut(val value: Int) : Technique() {

            override fun deal(stack: List<Int>): List<Int> {
                val cutValue = if (value < 0) stack.size + value else value
                return stack.drop(cutValue).plus(stack.take(cutValue))
            }

            override fun previousPosition(position: Int, stackSize: Int): Int {
                val cutValue = if (value < 0) stackSize + value else value
                return if (position > stackSize - cutValue) {
                    position - stackSize - cutValue
                } else {
                    cutValue + position
                }
            }
        }

        data class Increment(val value: Int) : Technique() {
            override fun deal(stack: List<Int>): List<Int> {
                val temp = MutableList(stack.size) { 0 }
                for (i in stack.indices) {
                    temp[(i * value) % stack.size] = stack[i]
                }
                return temp
            }

            override fun previousPosition(position: Int, stackSize: Int): Int {
                if (position == 0) return 0
                return stackSize * ((position - 1) / stackSize + 1) - value * position
            }
        }

        abstract fun deal(stack: List<Int>): List<Int>

        abstract fun previousPosition(position: Int, stackSize: Int): Int

    }
}