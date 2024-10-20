// PerformanceComparison.java
import java.lang.reflect.Method;

public class PerformanceComparison {
    public static void main(String[] args) {
        try {
            Class<?> testClass = SampleTests.class;

            // Sequential execution
            long sequentialStart = System.nanoTime();
            runSequential(testClass);
            long sequentialEnd = System.nanoTime();
            long sequentialTime = sequentialEnd - sequentialStart;

            // Concurrent execution
            long concurrentStart = System.nanoTime();
            TestRunner runner = new TestRunner(4);
            runner.runTests(testClass);
            long concurrentEnd = System.nanoTime();
            long concurrentTime = concurrentEnd - concurrentStart;

            System.out.println("\nPerformance Comparison:");
            System.out.println("Sequential execution time: " + sequentialTime / 1_000_000.0 + " ms");
            System.out.println("Concurrent execution time: " + concurrentTime / 1_000_000.0 + " ms");
            System.out.println("Speedup: " + (double)sequentialTime / concurrentTime);
        } catch (Exception e) {
            System.err.println("An error occurred during test execution:");
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private static void runSequential(Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                try {
                    method.invoke(testClass.newInstance());
                    System.out.println(method.getName() + ": PASSED");
                } catch (Exception e) {
                    System.out.println(method.getName() + ": FAILED - " + e.getMessage());
                }
            }
        }
    }
}
