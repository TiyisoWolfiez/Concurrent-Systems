import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {
    private final BlockingQueue<Runnable> taskQueue;
    private final Thread[] workerThreads;

    // ---------- Optimistic synchronization
    private final AtomicInteger completedTasks; 

    private final ReentrantLock lock;

    // ---------- Course-grained synchronization
    private volatile boolean isShutdown;

    public ThreadPool(int numThreads) {
        this.taskQueue = new PriorityBlockingQueue<>(11, 
            (r1, r2) -> ((PrioritizedRunnable)r1).getPriority() - ((PrioritizedRunnable)r2).getPriority());
        this.workerThreads = new Thread[numThreads];

        // ----------- Optimistic synchronization
        this.completedTasks = new AtomicInteger(0); 
        this.lock = new ReentrantLock();
        this.isShutdown = false;

        for (int i = 0; i < numThreads; i++) {
            workerThreads[i] = new WorkerThread();
            workerThreads[i].start();
        }
    }

    public void submit(Runnable task, int priority) {
        if (!isShutdown) {
            taskQueue.offer(new PrioritizedRunnable(task, priority));
        }
    }

    public void shutdown() {
        // ------------ Course-grained synchronization
        isShutdown = true; 
        for (Thread thread : workerThreads) {
            thread.interrupt();
        }
    }

    public int getCompletedTaskCount() {
        return completedTasks.get();
    }

    private class WorkerThread extends Thread {
        public void run() {
            // ------------ Course-grained synchronization
            while (!Thread.currentThread().isInterrupted() && !isShutdown) {
                try {
                    Runnable task = taskQueue.take();
                    task.run();
                    completedTasks.incrementAndGet(); // ----------- Optimistic synchronization
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private static class PrioritizedRunnable implements Runnable {
        private final Runnable task;
        private final int priority;

        public PrioritizedRunnable(Runnable task, int priority) {
            this.task = task;
            this.priority = priority;
        }

        @Override
        public void run() {
            task.run();
        }

        public int getPriority() {
            return priority;
        }
    }
}
