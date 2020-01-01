package advent

interface IntCodeInput {
    fun next(): Int

    companion object {
        operator fun invoke(): IntCodeInput {
            return object : IntCodeInput {
                override fun next(): Int {
                    throw IllegalStateException("Empty input queue")
                }

            }
        }
    }
}

class SingleInput(private val value: Int) : IntCodeInput {
    override fun next(): Int {
        return value
    }

}

class InputQueue(values: Iterable<Int>) : IntCodeInput {

    private var queue = values.toMutableList()

    override fun next(): Int {
        return queue.first().also { queue.removeAt(0) }
    }
}
