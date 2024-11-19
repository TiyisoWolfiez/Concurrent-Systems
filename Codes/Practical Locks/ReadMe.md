## Practical Locks

In concurrent programming, locks such as `Test-And-Set (TAS)`, `Test-And-Test-And-Set (TTAS)`, and
`exponential backoff` are essential for managing access to shared resources among multiple threads.

The `Test-And-Set` lock is straightforward: *a thread repeatedly tries to set a shared lock variable until
it acquires the lock. This simplicity makes `TAS` easy to implement, but it can result in high cache
coherence traffic since each thread continually modifies the shared variable.*


The `Test-And-Test-And-Set (TTAS)` lock aims to alleviate this issue by *first checking if the lock is free before attempting
to set it. This extra check reduces unnecessary updates, potentially improving performance in some
scenarios. However, both `TAS` and `TTAS` can still struggle when multiple threads contend for the
lock, leading to excessive spinning and increased overhead.*


`Exponential backoff` introduces a different approach by *incorporating a waiting period before retrying
to acquire a lock. When a thread fails to obtain the lock, it waits for a random interval and doubles
this waiting time on subsequent failures. This method helps reduce contention by spacing out lock attempts, allowing threads to back off and prevent a collapse in performance during high contention. While exponential backoff can adaptively reduce the burden on shared resources, it also comes with its own trade-offs. The effectiveness of these locks depends on factors such as the number of threads, contention level, and specific workload patterns. Each method has its strengths and weaknesses, which become apparent under different conditions in real-world applications.*

**Random Test-And-Set (TAS) Snippet**
```java
public void lock() {
		while (state.getAndSet(true)) {}
	}
```

**Random Test-And-Test-And-Set (TTAS) Snippet**
```java
public void lock() {
		while (true) {
			while (state.get()) {};
			if (!state.getAndSet(true))
			return;
		}

	}
```

**Random Exponential backoff Snippet**
```java
public void lock() {
		Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
		while (true) 
		{
			while (state.get()) {};
			if (!state.getAndSet(true)) 
			{
				return;
			}
			else 
			{
				try 
				{
					backoff.backoff();
				} catch (InterruptedException e) 
				{
					
					e.printStackTrace();
				}
			}
		}
	}
```
