import java.util.*;

public class Main {
    public static void main(String[] args) {
        Random r = new Random(100);
        int bound = 50000;
        int[] tests = {2, 2 * 2, 2 * 2 * 2, 2 * 2 * 2 * 2, 2 * 2 * 2 * 2 * 2};

        for (int i = 1; i <= 5; i++) {
            System.out.println("###");
            HashSet<Integer> seenSeeds = new HashSet<>();
            for (int j = 0; j < tests[i - 1]; j++) {
                Integer nextSeed = r.nextInt(bound);
                while (seenSeeds.contains(nextSeed)) {
                    nextSeed = r.nextInt(bound);
                }
                seenSeeds.add(nextSeed);
                tree(i, nextSeed);
            }
        }
        System.out.println("###");
    }

    public static void tree(int size, int seed) {
        int n = 1; // Number of operations for each thread
        List<Character> threads = List.of('A', 'B');

        // Create and assign labels for the execution tree
        ExecutionTree e = new ExecutionTree(threads, n, seed);
        e.assignLabels(); // Call assignLabels on the ExecutionTree
        
        // Print the tree
        System.out.println("Tree with each thread making " + size + " moves and with seed " + seed + ":");
        e.root.printTree("", true);
    }
}
