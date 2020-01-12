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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        }

        data class Cut(val value: Int) : Technique() {

            override fun deal(stack: List<Int>): List<Int> {
                val cutValue = if (value < 0) stack.size + value else value
                return stack.drop(cutValue).plus(stack.take(cutValue))
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
        }

        abstract fun deal(stack: List<Int>): List<Int>

    }
}