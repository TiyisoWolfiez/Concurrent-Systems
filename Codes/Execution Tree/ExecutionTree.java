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
    
    // ----------------------------My METHOODS-----------------------------------------------------

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isUnivalent() {
        if (isLeaf()) {
            return true;
        }
        HashSet<Integer> values = new HashSet<>();
        for (Node child : children) {
            if (child.value != null) {
                values.add(child.value);
            }
        }
        return values.size() <= 1;
    }

    public boolean isBivalent() {
        if (isLeaf()) {
            return false;
        }
        HashSet<Integer> values = new HashSet<>();
        for (Node child : children) {
            if (child.value != null) {
                values.add(child.value);
            }
        }
        return values.size() > 1;
    }

    public boolean isCritical() {
        if (isLeaf()) {
            return false;
        }
        for (Node child : children) {
            if (!child.isUnivalent()) {
                return false;
            }
        }
        return isBivalent();
    }

    //  -------------------------------- END ----------------------------------------------

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
        assignLabels(root);
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

    // --------------------------------My METHOD ------------------------------------

    private void assignLabels(Node node) {
        if (node.isLeaf()) {
            node.addLabel(Label.FINAL);
        } else if (node.isUnivalent()) {
            node.addLabel(Label.UNIVALENT);
        } else if (node.isBivalent()) {
            node.addLabel(Label.BIVALENT);
            if (node.isCritical()) {
                node.addLabel(Label.CRITICAL);
            }
        }
        for (Node child : node.children) {
            assignLabels(child);
        }
    }

}


// 28/62
