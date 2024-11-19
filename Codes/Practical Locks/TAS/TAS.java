// IMPLEMENTATION OF LOCK USING TEST-AND-SET
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class TAS implements Lock {
	AtomicBoolean state = new AtomicBoolean(false);

	@Override
	public void lock() {
		while (state.getAndSet(true)) {}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
	}

	@Override
	public Condition newCondition() {
		return null;
	}

	@Override
	public boolean tryLock() {
		return false;
	}

	@Override
	public boolean tryLock(long arg0, TimeUnit arg1)
			throws InterruptedException {
		return false;
	}

	@Override
	public void unlock() {
		state.set(false);
	}
}
