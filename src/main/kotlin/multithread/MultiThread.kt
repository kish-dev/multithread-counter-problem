package multithread

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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

class CounterMutex(var count: Int) {
    private val mutex = Mutex()

    suspend fun increment() {
        mutex.withLock {
            ++count
        }
    }

    suspend fun decrement() {
        mutex.withLock {
            --count
        }
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

object MultiCoroutine {
    suspend fun notSync(): Int {
        val scope = CoroutineScope(Dispatchers.Default)

        val counter = Counter(0)

        val one = scope.launch {
            for (i in 0..100_000) {
                counter.increment()
            }
        }

        val two = scope.launch {
            for (i in 0..100_000) {
                counter.decrement()
            }
        }

        one.join()
        two.join()

        return counter.count
    }

    suspend fun sync(): Int {
        val scope = CoroutineScope(Dispatchers.Default)

        val counter = CounterMutex(0)

        val one = scope.launch {
            for (i in 0..100_000) {
                counter.increment()
            }
        }

        val two = scope.launch {
            for (i in 0..100_000) {
                counter.decrement()
            }
        }

        one.join()
        two.join()

        return counter.count
    }
}
