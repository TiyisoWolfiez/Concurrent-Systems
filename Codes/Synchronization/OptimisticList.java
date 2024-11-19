import java.util.concurrent.locks.ReentrantLock;

public class OptimisticList<T> {
    private class Node {
        T item;
        int key;
        Node next;
        ReentrantLock lock = new ReentrantLock();

        Node(T item) {
            this.item = item;
            this.key = (item == null) ? Integer.MIN_VALUE : item.hashCode();
        }
    }

    private final Node head;

    public OptimisticList() {
        head = new Node(null); // Sentinel node
        head.next = new Node(null); // Tail node
        head.next.key = Integer.MAX_VALUE; // Ensure the tail node has the maximum key value
    }

    private boolean validate(Node pred, Node curr) {
        Node node = head;
        while (node.key <= pred.key) {
            if (node == pred) {
                return pred.next == curr;
            }
            node = node.next;
        }
        return false;
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock.lock();
            try {
                curr.lock.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return false;
                        }
                        Node newNode = new Node(item);
                        newNode.next = curr;
                        pred.next = newNode;
                        return true;
                    }
                } finally {
                    curr.lock.unlock();
                }
            } finally {
                pred.lock.unlock();
            }
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock.lock();
            try {
                curr.lock.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            pred.next = curr.next;
                            return true;
                        }
                        return false;
                    }
                } finally {
                    curr.lock.unlock();
                }
            } finally {
                pred.lock.unlock();
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            curr.lock.lock();
            try {
                if (validate(pred, curr)) {
                    return curr.key == key;
                }
            } finally {
                curr.lock.unlock();
            }
        }
    }
}
