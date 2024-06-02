# Interview on Multithreading and Concurrency: Counter/Sharing Resource Problem
How can we resolve incrementing and decrementing a counter in different ways, including explanations of timings?

Have you ever seen this question in an interview?


``` kotlin
fun main() {
  var counter = 0

  val one = thread(true) {
    for (i in 0..1000) {
      ++counter
    }
  }

  val two = thread(true) {
    for (i in 0..1000) {
      --counter
    }
  }

  one.join()
  two.join()

  // What would be printed?
  println(counter)
  
}
```

Here is a simple example of two threads accessing a critical section with a shared resource: the counter.

And the answer is a range from [-1000 to 1000].

But how do we achieve 0?

# Ways to fix problem

## Synchronized - simple
You can introduce synchronization using a shared monitor, ensuring that operations by the two threads are completed in a synchronized block. This allows the threads to sync their caches with the heap at the start and the end of the synchronization block. Result: guaranteed accurate counter manipulation.

``` kotlin
fun main() {
  var counter = 0
  val a = Any()

  val one = thread(true) {
    for (i in 0..1000) {
      synchronized(a) {
        ++counter 
      }
    }
  }

  val two = thread(true) {
    for (i in 0..1000) {
      synchronized(a) {
        --counter 
      }
    }
  }

  one.join()
  two.join()

  // What would be printed?
  println(counter)
  
}
```


## Synchronized and volatile - unnecessary volatile

Some programmers with limited concurrency experience might add the volatile keyword, not knowing that threads will synchronize their cache at the start and the end of a synchronized block. Their solution might look something like this:

``` kotlin
fun main() {
  @Volatile
  var counter = 0
  val a = Any()

  val one = thread(true) {
    for (i in 0..1000) {
      synchronized(a) {
        ++counter 
      }
    }
  }

  val two = thread(true) {
    for (i in 0..1000) {
      synchronized(a) {
        --counter 
      }
    }
  }

  one.join()
  two.join()

  // What would be printed?
  println(counter)
  
}
```

## Atomic - best

This is the best approach in terms of speed (no need for explicit synchronization, only atomic operations).

Why does it work? Because atomic operations utilize CPU-level atomic instructions and are based on volatile variables.

Thus, the optimal solution would look something like this:


``` kotlin
fun main() {
  var counter = AtomicInteger(0)

  val one = thread(true) {
    for (i in 0..1000) {
      counter.getAndIncrement()
    }
  }

  val two = thread(true) {
    for (i in 0..1000) {
      counter.getAndDecrement()
    }
  }

  one.join()
  two.join()

  // What would be printed?
  println(counter.get())
  
}
```


## Other solutions

We could develop more complex solutions using CountDownLatch and other concurrency primitives, but the principles of KISS (Keep It Simple, Stupid) and YAGNI (You Aren't Gonna Need It) suggest otherwise.


