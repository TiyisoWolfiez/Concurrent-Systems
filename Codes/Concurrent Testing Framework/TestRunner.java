// TestRunner.java
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.*;

public class TestRunner {
    private final ThreadPool threadPool;
    private final Map<String, TestResult> results; // ---------- Fine-grained synchronization
    private final Map<String, Set<String>> dependencies;
    private final CountDownLatch completionLatch;

    public TestRunner(int numThreads) {
        this.threadPool = new ThreadPool(numThreads);
        this.results = new ConcurrentHashMap<>();
        this.dependencies = new HashMap<>();
        this.completionLatch = new CountDownLatch(1);
    }

    public void runTests(Class<?> testClass) throws Exception {
        Method[] methods = testClass.getDeclaredMethods();
        List<Method> testMethods = new ArrayList<>();

        for (Method method : methods) {
            if (method.isAnnotationPresent(Test.class)) {
                String methodName = method.getName();
                results.put(methodName, new TestResult(TestStatus.NOT_RUN));
                dependencies.put(methodName, new HashSet<>());
                
                DependsOn dependsOn = method.getAnnotation(DependsOn.class);
                if (dependsOn != null) {
                    dependencies.get(methodName).addAll(Arrays.asList(dependsOn.value()));
                }
                
                testMethods.add(method);
            }
        }

        for (Method method : testMethods) {
            scheduleTest(testClass, method.getName());
        }

        completionLatch.await();
        threadPool.shutdown();

        printResults();
    }

    private void scheduleTest(Class<?> testClass, String methodName) {
        Set<String> deps = dependencies.get(methodName);
        if (deps.isEmpty() || deps.stream().allMatch(dep -> results.get(dep).getStatus() == TestStatus.PASSED)) {
            threadPool.submit(() -> runTest(testClass, methodName), 1);
        } else if (deps.stream().anyMatch(dep -> results.get(dep).getStatus() == TestStatus.FAILED || results.get(dep).getStatus() == TestStatus.SKIPPED)) {
            results.put(methodName, new TestResult(TestStatus.SKIPPED));
            checkCompletion();
        } else {
            threadPool.submit(() -> {
                while (!Thread.currentThread().isInterrupted() && 
                    deps.stream().anyMatch(dep -> results.get(dep).getStatus() == TestStatus.NOT_RUN)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                scheduleTest(testClass, methodName);
            }, 2);
        }
    }

    private void runTest(Class<?> testClass, String methodName) {
        try {
            Method method = testClass.getMethod(methodName);
            method.invoke(testClass.newInstance());
            results.put(methodName, new TestResult(TestStatus.PASSED)); // ---------- Fine-grained synchronization
        } catch (Exception e) {
            results.put(methodName, new TestResult(TestStatus.FAILED, e.getMessage())); // ---------- Fine-grained synchronization
        }
        checkCompletion();
    }

    private void checkCompletion() {
        if (results.values().stream().allMatch(result -> result.getStatus() != TestStatus.NOT_RUN)) {
            completionLatch.countDown();
        }
    }

    private void printResults() {
        System.out.println("Test Results:");
        results.forEach((methodName, result) -> 
            System.out.println(methodName + ": " + result.getStatus() + 
                (result.getErrorMessage() != null ? " - " + result.getErrorMessage() : "")));
    }

    private enum TestStatus { NOT_RUN, PASSED, FAILED, SKIPPED }

    private static class TestResult {
        private final TestStatus status;
        private final String errorMessage;

        public TestResult(TestStatus status) {
            this(status, null);
        }

        public TestResult(TestStatus status, String errorMessage) {
            this.status = status;
            this.errorMessage = errorMessage;
        }

        public TestStatus getStatus() {
            return status;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
