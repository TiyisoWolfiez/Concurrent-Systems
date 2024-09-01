import java.util.*;

class Node {
    Integer value;
    String path;
    List<Node> children;
    List<Label> labels;
    HashSet<Integer> childrenValues;

    public Node(Integer value, String path) {
        this.value = value;
        this.path = path;
        this.children = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.childrenValues = new HashSet<>();
    }

    public Node(String path) {
        this.value = null;
        this.path = path;
        this.children = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.childrenValues = new HashSet<>();
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public void addLabel(Label l) {
        this.labels.add(l);
    }

    // Maybe adding a method here would wake it easier ¯\_(ツ)_/¯
    // ------------------------------------------------ My Method------------------------------------
    

    // ----------------------------------------------------------------------------------------------

    public void printTree(String prefix, boolean isTail) {

        System.out.println(
                prefix + (isTail ? "└── " : "├── ") + path + " Labels:" + labels + " value: (" + value + ")");
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).printTree(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).printTree(prefix + (isTail ? "    " : "│   "), true);
        }
    }

}

public class ExecutionTree {
    public Random r;
    public Node root;

    public void assignLabels() {
        // Step 1: Label FINAL nodes
        labelFinalNodes(root);

        // Step 2: Determine UNIVALENT and BIVALENT nodes
        determineUnivalentBivalent(root);

        // Step 3: Determine CRITICAL nodes
        determineCriticalNodes(root);
    }

    // I wouldn't change this if I were you
    public ExecutionTree(List<Character> threads, int n, int seed) {
        this.r = new Random(seed);
        int[] counts = new int[threads.size()]; // Array to keep track of counts

        this.root = new Node("");

        // Start the generation with each character
        for (int i = 0; i < threads.size(); i++) {
            counts[i]++;
            root.addChild(generateTree(threads.get(i) + "", counts, n, threads));
            counts[i]--; // Backtrack
        }

        root.addLabel(Label.INITIAL);
    }

    public Node generateTree(String path, int[] counts, int n, List<Character> chars) {
        boolean isComplete = true;
        for (int count : counts) {
            if (count < n) {
                isComplete = false;
                break;
            }
        }
        if (isComplete) {
            return new Node(r.nextInt(counts.length), path);
        }

        Node node = new Node(path);

        for (int i = 0; i < chars.size(); i++) {
            if (counts[i] < n) {
                counts[i]++;
                node.addChild(generateTree(path + chars.get(i), counts, n, chars));
                counts[i]--; // Backtrack
            }
        }
        return node;
    }

    private void labelFinalNodes(Node node) {
        if (node.children.isEmpty()) {
            node.addLabel(Label.FINAL);
        } else {
            for (Node child : node.children) {
                labelFinalNodes(child);
            }
        }
    }

    private void determineUnivalentBivalent(Node node) {
        if (node.children.isEmpty()) {
            return;
        }

        Set<Integer> values = new HashSet<>();
        for (Node child : node.children) {
            determineUnivalentBivalent(child);
            values.add(child.value);
        }

        if (values.size() == 1) {
            node.addLabel(Label.UNIVALENT);
        } else {
            node.addLabel(Label.BIVALENT);
        }
    }

    private void determineCriticalNodes(Node node) {
        if (node.children.isEmpty()) {
            return;
        }

        boolean isCritical = true;
        for (Node child : node.children) {
            if (!child.labels.contains(Label.UNIVALENT)) {
                isCritical = false;
                break;
            }
        }

        if (isCritical) {
            node.addLabel(Label.CRITICAL);
        }

        for (Node child : node.children) {
            determineCriticalNodes(child);
        }
    }

}
