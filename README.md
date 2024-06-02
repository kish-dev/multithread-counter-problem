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

## Time Complexity Evaluation of Multithreading Methods

In conducting performance analysis of different synchronization methods in multithreading, I executed each method multiple times to ensure accuracy, obtaining the average execution time measured in nanoseconds. Here below are the results from the experiment:

| Method            | Average Execution Time (nanoseconds) |
|-------------------|--------------------------------------|
| Atomic Operations | 4,333,015                            |
| Synchronized with Volatile | 7,618,192                  |
| Synchronized Method | 7,108,395                          |
| Non-synchronized  | 102,711                              |

### Observations:
- Atomic Operations: Moderate performance. Suitable for scenarios where the overhead of full synchronization is unnecessary.
- Synchronized with Volatile: This method shows the highest execution time, likely due to overhead from both synchronization and volatile access.
- Synchronized Method: Slightly faster than using both synchronized and volatile, indicating reduced overhead from dropping volatile.
- Non-synchronized: The fastest among the methods tested but not thread-safe, which can lead to inconsistent results in a concurrent environment.

### Conclusion:
Each method has its use cases depending on the requirement for data consistency versus execution speed. Synchronization introduces overhead, but ensures data consistency in multithreaded environments, whereas non-synchronized methods should only be used when there is no risk of concurrent modifications to the data.
