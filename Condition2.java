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
	}

    /**
     * Atomically release the associated lock and go to sleep on this condition
     * variable until another thread wakes it using <tt>wake()</tt>. The
     * current thread must hold the associated lock. The thread will
     * automatically reacquire the lock before <tt>sleep()</tt> returns.
     */
    public void sleep() 
	{
		// It became apparent soon after coding that our sleep and wake methods detailed in our design document would result in an infinite loop therefor some
		// slight changes have been made
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());	// Assure that the thread holds the lock
		Lib.assertTrue(KThread.currentThread().status != 4);	// Assure that thread status is not finished
		boolean intStatus = Machine.interrupt().disable();		// Disable interrupts
		
		sleepingQueue.add(KThread.currentThread());		// Add the current thread to the sleeping queue
		conditionLock.release();						// Release the lock
		KThread.currentThread().sleep();				// Put the currentThread to sleep using KThread's built in sleep method
		Machine.interrupt().restore(intStatus);			// Restore previous interrupts
		conditionLock.acquire();						// Reacquire the lock
		
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() 
	{
		// Some changes have been made in wake to account for the changes made in sleep
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());	// Assure that the thread holds the lock
		if(!sleepingQueue.isEmpty())							// Assure that there is at least one sleeping thread
		{
				boolean intStatus = Machine.interrupt().disable();	// Disable interrupts
				KThread workdamnyou = new KThread();				// Create a new KThread to be used for placing the waking thread on the readyQueue
				workdamnyou = (KThread) sleepingQueue.removeFirst();// Set new thread equal to the first one on the sleeping queue while removing it from the queue
				workdamnyou.ready();								// Place that queue on the readyQueue by calling KThreads ready() method
				Machine.interrupt().restore(intStatus);				// Restore interrupts
		}
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());	// Assure that the thread holds the lock
		while(!sleepingQueue.isEmpty())							// Call wake() while there are still threads on the sleeping queue
		{
			wake();
		}
    }
	/**
	 *	Method for testing and debugging Condition2
	 *  Note: Many test cases from the design document were removed as there would be no reason to test most of them as they could not 
	 *  possibly occur.
	 */
	public static void selfTest()
	{
		// Test Case 1: Put a running thread to sleep 
		KThread sleep0 = new KThread(new Cond2Tester(0, "Test 1.0"));
		sleep0.fork();
		
		KThread sleep1 = new KThread(new Cond2Tester(0, "Test 1.1"));
		sleep1.fork();
		
		KThread sleep2 = new KThread(new Cond2Tester(0, "Test 1.2"));
		sleep2.fork();
		
		// Test Case 2: Wake a sleeping thread
		KThread wake1 = new KThread(new Cond2Tester(1, "Test 2"));
		wake1.fork();
		sleep0.join();	// Task 1 wakes the sleeping task 2
		
		// Test Case 3: Waking all threads on the sleepingQueue
		KThread wakeAll = new KThread(new Cond2Tester(2, "Test 3"));
		wakeAll.fork();
		sleep1.join();
		sleep2.join();
		
		// Test Case 4: Wake a thread when sleepingQueue is empty
		KThread wake2 = new KThread(new Cond2Tester(1, "Test 4"));
		wake2.fork();
		
		// Test Case 5: Wake all when sleepingQueue is empty
		KThread wakeAll2 = new KThread(new Cond2Tester(2, "Test 5"));
		wakeAll2.fork();
		
	
	}
	/**
	 * A new runnable class that allows for a cleaner and more comprehensible organization to the
	 * self tester.
	 * 0: sleep, 1: wake, 2: wakeAll
	 */
	private static class Cond2Tester implements Runnable 
	{
		final Lock testLock = new Lock();
		final Condition2 testCond = new Condition2(testLock);
		int testCase;
		String testName;
		
		// Constructor requires the type of test case 0,1 or 2 for sleep(), wake() or wakeAll() and the name of the Test Case
		Cond2Tester(int testCase, String testName)
		{
			this.testCase = testCase;
			this.testName = testName;
		}
		public void run()
		{
			testLock.acquire();
			switch(testCase)	// Switch for test cases
			{
				case(0):
					System.out.println(testName + " used sleep...It was very effective!");
					testCond.sleep();
					System.out.println("A wild "  + testName + " appeared (woke up)!");
					break;
				case(1):
					System.out.println(testName + " is waking someone up...");
					testCond.wake();
					break;
				case(2):
					System.out.println(testName + " is being loud and waking everyone up...");
					testCond.wakeAll();
					break;
			}
			System.out.println(testName + " complete");
			testLock.release();
		}
	
	}

    private Lock conditionLock;
	private static qQueue sleepingQueue = new qQueue();
}
