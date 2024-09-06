package multithread;

import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private int count;

    public Counter(int count) {
        this.count = count;
    }

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public int getCount() {
        return count;
    }
}

class CounterVolatile {
    private volatile int count;

    public CounterVolatile(int count) {
        this.count = count;
    }

    public void increment() {
        count++;
    }

    public void decrement() {
        count--;
    }

    public int getCount() {
        return count;
    }
}

class AtomicCounter {
    private AtomicInteger count;

    public AtomicCounter(AtomicInteger count) {
        this.count = count;
    }

    public void increment() {
        count.getAndIncrement();
    }

    public void decrement() {
        count.getAndDecrement();
    }

    public int getCount() {
        return count.get();
    }
}

public class MultiThread {

    public static int notSync() {
        try {

            Counter counter = new Counter(0);

            Thread one = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    counter.increment();
                }
            });

            Thread two = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    counter.decrement();
                }
            });

            one.start();
            two.start();

            one.join();
            two.join();

            return counter.getCount();
        } catch (InterruptedException e) {
            System.out.printf(e.getMessage());
        }
        return -1;
    }

    public static int sync() {
        try {

            Counter counter = new Counter(0);
            Object lock = new Object();

            Thread one = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    synchronized (lock) {
                        counter.increment();
                    }
                }
            });

            Thread two = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    synchronized (lock) {
                        counter.decrement();
                    }
                }
            });

            one.start();
            two.start();

            one.join();
            two.join();

            return counter.getCount();
        } catch (InterruptedException e) {
            System.out.printf(e.getMessage());
        }
        return -1;
    }

    public static int syncAndVolatile() {
        try {
            CounterVolatile counter = new CounterVolatile(0);
            Object lock = new Object();

            Thread one = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    synchronized (lock) {
                        counter.increment();
                    }
                }
            });

            Thread two = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    synchronized (lock) {
                        counter.decrement();
                    }
                }
            });

            one.start();
            two.start();

            one.join();
            two.join();

            return counter.getCount();
        } catch (InterruptedException e) {
            System.out.printf(e.getMessage());
        }
        return -1;
    }

    public static int atomic() {
        try {
            AtomicCounter counter = new AtomicCounter(new AtomicInteger(0));

            Thread one = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    counter.increment();
                }
            });

            Thread two = new Thread(() -> {
                for (int i = 0; i <= 100_000; i++) {
                    counter.decrement();
                }
            });

            one.start();
            two.start();

            one.join();
            two.join();

            return counter.getCount();
        } catch (InterruptedException e) {
            System.out.printf(e.getMessage());
        }
        return -1;
    }
}

