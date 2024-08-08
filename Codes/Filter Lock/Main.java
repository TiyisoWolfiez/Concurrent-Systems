import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) throws Exception {
        Output output = new Output();
        Queue<Paper> paperQueue = new LinkedList<>();

        // Add papers to the queue
        for (int i = 0; i < 10; i++) {
            paperQueue.add(new Paper());
        }

        Threeterson lock = new Threeterson(output);

        // Create and start marker threads
        Marker marker1 = new Marker(lock, output, paperQueue);
        marker1.setName("0");
        Marker marker2 = new Marker(lock, output, paperQueue);
        marker2.setName("1");
        Marker marker3 = new Marker(lock, output, paperQueue);
        marker3.setName("2");

        marker1.start();
        marker2.start();
        marker3.start();

        // Wait for markers to finish
        try {
            marker1.join();
            marker2.join();
            marker3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Print the output log
        System.out.println(output);

        // Validate the output
        OutputValidator validator = new OutputValidator(output);
        boolean isValid = validator.validate();
        System.out.println("Output is valid: " + isValid);
    }
}
