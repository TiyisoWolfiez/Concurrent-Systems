import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PerformanceTest {
    public static void main(String[] args) throws InterruptedException {
        int[] threadCounts = {10, 20, 30, 40, 50};
        int operationsPerThread = 1000;

        System.out.println("Testing Fine-Grained Synchronization:");
        for (int threadCount : threadCounts) {
            testPerformance(new FineGrainedList<>(), threadCount, operationsPerThread);
        }

        System.out.println("\nTesting Coarse-Grained Synchronization:");
        for (int threadCount : threadCounts) {
            testPerformance(new coarseGrainedList<>(), threadCount, operationsPerThread);
        }

        System.out.println("\nTesting Optimistic Synchronization:");
        for (int threadCount : threadCounts) {
            testPerformance(new OptimisticList<>(), threadCount, operationsPerThread);
        }
    }

    private static void testPerformance(Object list, int threadCount, int operationsPerThread) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        long startTime = System.nanoTime();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                for (int j = 0; j < operationsPerThread; j++) {
                    if (list instanceof FineGrainedList) {
                        FineGrainedList<Integer> fgList = (FineGrainedList<Integer>) list;
                        fgList.add(j);
                        fgList.contains(j);
                        fgList.remove(j);
                    } else if (list instanceof coarseGrainedList) {
                        coarseGrainedList<Integer> cgList = (coarseGrainedList<Integer>) list;
                        cgList.add(j);
                        cgList.contains(j);
                        cgList.remove(j);
                    } else if (list instanceof OptimisticList) {
                        OptimisticList<Integer> oList = (OptimisticList<Integer>) list;
                        oList.add(j);
                        oList.contains(j);
                        oList.remove(j);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

        System.out.println("Threads: " + threadCount + ", Execution time: " + duration + " ms");
    }
}
