
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Threeterson implements Lock {
    private final ReentrantLock printLock;
    private final Output output;
    private final int[] level;
    private final int[] victim;

    public Threeterson(Output output) {
        this.printLock = new ReentrantLock();
        this.output = output;
        this.level = new int[3];
        this.victim = new int[3];
    }

    @Override
    public void lock() {
        int i;
        int me = (int) (Thread.currentThread().getId() % 3);
        for (i = 1; i < 3; i++) {
            printLock.lock();
            try {
                victim[i] = me;
                output.println(Thread.currentThread().getName() + " at L" + i);
                level[me] = i;
                output.println(Thread.currentThread().getName() + " is the victim of L" + i);
            } finally {
                printLock.unlock();
            }

            boolean wait = true;
            int k;
            while (wait) {
                wait = false;
                for (k = 0; k < 3; k++) {
                    if (k != me && level[k] >= i && victim[i] == me) {
                        wait = true;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void unlock() {
        int me = (int) Thread.currentThread().getId() % 3;
        level[me] = 0;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean tryLock() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
