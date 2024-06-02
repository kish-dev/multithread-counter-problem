package multithread

import java.util.logging.Logger
import org.junit.Test

class TestMultiThread {

    companion object {
        const val ZERO = 0
        const val REPEAT_COUNT = 1000
    }

    private val logger = Logger.getLogger("TestLogger")

    private fun medium(list: List<Long>): Long {
        var sum = 0L
        list.forEach { value ->
            sum += value
        }
        return sum / list.size
    }

    private fun start(
        methodName: String,
        lambda: () -> Int
    ) {
        val list = mutableListOf<Long>()
        val counterList = mutableListOf<Int>()
        for(i in 0 until REPEAT_COUNT) {
            val startTime = System.nanoTime()

            val counter = lambda.invoke()
            counterList.add(counter)

            val endTime = System.nanoTime()
            val diff = endTime - startTime
            list.add(diff)
        }

        logger.info("$methodName == ${medium(list)}")

        assert(counterList.filter { it == ZERO }.size == REPEAT_COUNT)
    }

    @Test
    fun notSync() {
        start(
            methodName = "notSync",
            lambda = { MultiThread.notSync() }
        )
    }

    @Test
    fun sync() {
        start(
            methodName = "sync",
            lambda = { MultiThread.sync() }
        )
    }

    @Test
    fun syncAndVolatile() {
        start(
            methodName = "syncAndVolatile",
            lambda = { MultiThread.syncAndVolatile() }
        )
    }

    @Test
    fun atomic() {
        start(
            methodName = "atomic",
            lambda = { MultiThread.atomic() }
        )
    }
}
