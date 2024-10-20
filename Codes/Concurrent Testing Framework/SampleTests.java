// SampleTests.java
public class SampleTests {
    @Test
    public void test1() {
        System.out.println("Running test1");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DependsOn("test1")
    public void test2() {
        System.out.println("Running test2");
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void test3() {
        System.out.println("Running test3");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    @DependsOn({"test2", "test3"})
    public void test4() {
        System.out.println("Running test4");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Test
    public void test5() {
        System.out.println("Running test5");
        throw new RuntimeException("Simulated failure");
    }

    @Test
    @DependsOn("test5")
    public void test6() {
        System.out.println("Running test6");
        // This test should be skipped due to test5 failure
    }
}
