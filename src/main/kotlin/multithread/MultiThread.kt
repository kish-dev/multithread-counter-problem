package multithread

import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

class Counter(var count: Int) {

    fun increment() {
        ++count
    }

    fun decrement() {
        --count
    }
}

class CounterVolatile(@Volatile var count: Int) {

    fun increment() {
        ++count
    }

    fun decrement() {
        --count
    }
}

class AtomicCounter(var count: AtomicInteger) {
    fun increment() {
        count.getAndIncrement()
    }

    fun decrement() {
        count.getAndDecrement()
    }
}


object MultiThread {
    fun notSync(): Int {
        val counter = Counter(0)

        val one = thread(true) {
            for (i in 0..100_000) {
                counter.increment()
            }
        }

        val two = thread(true) {
            for (i in 0..100_000) {
                counter.decrement()
            }
        }

        one.join()
        two.join()

        return counter.count
    }

    fun sync(): Int {
        val counter = Counter(0)
        val a = Any()

        val one = thread(true) {
            for (i in 0..100_000) {
                synchronized(a) {
                    counter.increment()
                }
            }
        }

        val two = thread(true) {
            for (i in 0..100_000) {
                synchronized(a) {
                    counter.decrement()
                }
            }
        }

        one.join()
        two.join()

        return counter.count
    }

    fun syncAndVolatile(): Int {
        val counter = CounterVolatile(0)
        val a = Any()

        val one = thread(true) {
            for (i in 0..100_000) {
                synchronized(a) {
                    counter.increment()
                }
            }
        }

        val two = thread(true) {
            for (i in 0..100_000) {
                synchronized(a) {
                    counter.decrement()
                }
            }
        }

        one.join()
        two.join()

        return counter.count
    }

    fun atomic(): Int {
        val counter = AtomicCounter(AtomicInteger(0))
        val a = Any()

        val one = thread(true) {
            for (i in 0..100_000) {
                counter.increment()
            }
        }

        val two = thread(true) {
            for (i in 0..100_000) {
                counter.decrement()
            }
        }

        one.join()
        two.join()

        return counter.count.get()
    }
}