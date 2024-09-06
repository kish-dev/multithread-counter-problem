package org.example;

import multithread.MultiThread;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public class TestMultiThread {

    private static final int ZERO = 0;
    private static final int REPEAT_COUNT = 1000;

    private final Logger logger = Logger.getLogger("TestLogger");

    private long medium(List<Long> list) {
        long sum = 0L;
        for (Long value : list) {
            sum += value;
        }
        return sum / list.size();
    }

    private void start(String methodName, java.util.function.IntSupplier lambda) {
        List<Long> timeList = new ArrayList<>();
        List<Integer> counterList = new ArrayList<>();

        for (int i = 0; i < REPEAT_COUNT; i++) {
            long startTime = System.nanoTime();

            try {
                int counter = lambda.getAsInt();
                counterList.add(counter);
            } catch (Exception e) {
                logger.severe("Exception occurred in method " + methodName + ": " + e.getMessage());
                // Обработка исключения (переход к следующей итерации, если это приемлемо)
                counterList.add(ZERO);
            }

            long endTime = System.nanoTime();
            long diff = endTime - startTime;
            timeList.add(diff);
        }

        logger.info(methodName + " == " + medium(timeList));

        // Assert that all counters are ZERO
        long zeroCount = counterList.stream().filter(count -> count == ZERO).count();
        assertEquals(REPEAT_COUNT, zeroCount);
    }

    @Test
    public void notSync() {
        start("notSync", MultiThread::notSync);
    }

    @Test
    public void sync() {
        start("sync", MultiThread::sync);
    }

    @Test
    public void syncAndVolatile() {
        start("syncAndVolatile", MultiThread::syncAndVolatile);
    }

    @Test
    public void atomic() {
        start("atomic", MultiThread::atomic);
    }
}
