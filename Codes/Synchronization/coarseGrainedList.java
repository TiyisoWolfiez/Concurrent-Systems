import java.util.concurrent.locks.ReentrantLock;

public class coarseGrainedList<T> {
    private final ReentrantLock lock = new ReentrantLock();
    private Node head;

    private class Node {
        T item;
        Node next;

        Node(T item) {
            this.item = item;
        }
    }

    public coarseGrainedList() {
        head = new Node(null); // Sentinel node
    }

    public void add(T item) {
        lock.lock();
        try {
            Node newNode = new Node(item);
            newNode.next = head.next;
            head.next = newNode;
        } finally {
            lock.unlock();
        }
    }

    public void remove(T item) {
        lock.lock();
        try {
            Node pred = head;
            Node curr = head.next;
            while (curr != null && !curr.item.equals(item)) {
                pred = curr;
                curr = curr.next;
            }
            if (curr != null) {
                pred.next = curr.next;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean contains(T item) {
        lock.lock();
        try {
            Node curr = head.next;
            while (curr != null) {
                if (curr.item.equals(item)) {
                    return true;
                }
                curr = curr.next;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}
