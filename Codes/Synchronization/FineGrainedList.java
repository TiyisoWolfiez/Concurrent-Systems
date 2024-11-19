import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedList<T> {
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

    public FineGrainedList() {
        head = new Node(null); // Sentinel node
        head.next = new Node(null); // Tail node
        head.next.key = Integer.MAX_VALUE; // Ensure the tail node has the maximum key value
    }

    public boolean add(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    System.out.println("Add: Item " + item + " already exists.");
                    return false;
                }
                Node newNode = new Node(item);
                newNode.next = curr;
                pred.next = newNode;
                System.out.println("Add: Item " + item + " added.");
                return true;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    System.out.println("Remove: Item " + item + " removed.");
                    return true;
                }
                System.out.println("Remove: Item " + item + " not found.");
                return false;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        head.lock.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock.lock();
            try {
                while (curr.key < key) {
                    pred.lock.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock.lock();
                }
                boolean found = curr.key == key;
                System.out.println("Contains: Item " + item + (found ? " found." : " not found."));
                return found;
            } finally {
                curr.lock.unlock();
            }
        } finally {
            pred.lock.unlock();
        }
    }
}
