package nachos.threads;

import nachos.machine.*;

/**
 * An implementation of condition variables that disables interrupt()s for
 * synchronization.
 *
 * <p>
 * You must implement this.
 *
 * @see	nachos.threads.Condition
 */
public class Condition2 {
    /**
     * Allocate a new condition variable.
     *
     * @param	conditionLock	the lock associated with this condition
     *				variable. The current thread must hold this
     *				lock whenever it uses <tt>sleep()</tt>,
     *				<tt>wake()</tt>, or <tt>wakeAll()</tt>.
     */
    public Condition2(Lock conditionLock) 
	{
		this.conditionLock = conditionLock;
		sleepingQueue = new qQueue();
    }

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() 
	{
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		Lib.assertTrue(KThread.currentThread().status != 4);	// Assure that thread status is not finished
		boolean intStatus = Machine.interrupt().disable();
		
		sleepingQueue.add(KThread.currentThread());
		conditionLock.release();
		KThread.currentThread().sleep();
		Machine.interrupt().restore(intStatus);
		conditionLock.acquire();
		
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() 
	{
		
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		if(!sleepingQueue.isEmpty())
		{
			boolean intStatus = Machine.interrupt().disable();
			KThread workdamnyou = new KThread();
			workdamnyou = sleepingQueue.removeFirst();
			workdamnyou.ready();
			sleepingQueue.removeFirst().ready();
			thread.ready();
			Machine.interrupt().restore(intStatus);
		}
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		while(!sleepingQueue.isEmpty())
		{
			wake();
		}
    }
	
	public static void selfTest()
	{
		
		KThread sleep = new KThread(new Cond2Tester(0, "Task 1"));
		sleep.fork();
		
		KThread wake = new KThread(new Cond2Tester(1, "Task 2"));
		wake.fork();
		sleep.join();
		
		
		

		
	
	}
	
	private static class Cond2Tester implements Runnable 
	{
		final Lock testLock = new Lock();
		final Condition2 testCond = new Condition2(testLock);
		int testCase;
		String name;
		
		Cond2Tester(int testCase, String name)
		{
			this.testCase = testCase;
			this.name = name;
		}
		public void run()
		{
			testLock.acquire();
			switch(testCase)
			{
				case(0):
					System.out.println("Taking a nap...");
					testCond.sleep();
					System.out.println("Woke up!");
					break;
				case(1):
					System.out.println("Waking up...");
					testCond.wake();
					break;
				case(2):
					System.out.println("Waking everyone...");
					testCond.wakeAll();
					break;
			}
			System.out.println(name + "Task finished");
			testLock.release();
		}
	
	}

    private Lock conditionLock;
	private static qQueue sleepingQueue;
}
