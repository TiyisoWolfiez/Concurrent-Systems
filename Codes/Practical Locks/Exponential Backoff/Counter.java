
class Counter implements Runnable 
{
	private int value;
	private BackoffLock lock ;
	int num_iterations;
	
	public Counter(int c,int n,int min,int max) 
	{ 
		value = c;
		num_iterations = n;
		lock =  new BackoffLock(min,max);
		
	}

	public int getAndIncrement() 
	{
		int temp=0;
		System.out.println("Thread " + Thread.currentThread().getId() + " is trying to enter CS");
		lock.lock();
		System.out.println("Thread " + Thread.currentThread().getId() + " entered CS");
		try
		{
			temp = value;
			value = temp+1;			
			System.out.println("Thread " + Thread.currentThread().getId()  + " changed the value from " + temp + " to " + value);
		}
		finally
		{
			System.out.println("Thread " + Thread.currentThread().getId() + " is leaving CS");
			lock.unlock();
			System.out.println("Thread " + Thread.currentThread().getId() + " left CS");
		}
		return temp;
	}
 
	public void run()
	{
		for(int i=0;i<num_iterations;i++)
		{
			getAndIncrement();
		}
	}
}
