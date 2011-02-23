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
		conditionLock.acquire();						// Reacquire the lock
		Machine.interrupt().restore(intStatus);			// Restore previous interrupts
		
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
		final Lock testLock = new Lock();
		final Condition2 testCond = new Condition2(testLock);
		// Test Case 1: Put a running thread to sleep 
		KThread sleep1 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
				System.out.println("--------Starting Condition2 selft test -----------------");
                System.out.println("Test Case 1: Taking a nap");
                testCond.sleep();     
                System.out.println("Test Case 1: Thread woke up!"); 
				System.out.println("Test Case 1: Complete");
				testLock.release();
				
        } } ).setName("Test 1");
		sleep1.fork();
		//KThread sleep0 = new KThread(new Runnable);
		
		KThread sleep2 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 1.1: Taking a nap");
                testCond.sleep();     
                System.out.println("Test Case 1.1: Thread woke up!");
				System.out.println("Test Case 1.1: Complete");
				testLock.release();		
				
        } } ).setName("Test 1.1");
		
		sleep2.fork();
		
		KThread sleep3 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 1.2: Taking a nap");
                testCond.sleep();     
                System.out.println("Test Case 1.2: Thread woke up!"); 
				System.out.println("Test Case 1.2: Complete");
				testLock.release();
        } } ).setName("Test 1.2");
		sleep3.fork();
		
		// Test Case 2: Wake a sleeping thread
		KThread wake1 =	new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 2: Waking a thread...");
                testCond.wake();      
				System.out.println("Test Case 2: Complete");
				testLock.release();
        } } ).setName("Test 2");
		wake1.fork();
		sleep1.join();
		
		// Test Case 3: Waking all threads on the sleepingQueue
		KThread wakeAll1 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 3: Waking everyone up...");
                testCond.wakeAll();     
                System.out.println("Test Case 3: Everyone's awake now!!"); 
				System.out.println("Test Case 3: Complete");
				testLock.release();
        } } ).setName("Test 3");
		wakeAll1.fork();
		sleep2.join();
		sleep3.join();
		
		// Test Case 4: Wake a thread when sleepingQueue is empty
		KThread wake2 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 4: Waking someone who's not there...");
                testCond.wake();      
				System.out.println("Test Case 4: No one home to wake... ");
				System.out.println("Test Case 4: Complete");
				testLock.release();
        } } ).setName("Test 4");
		wake2.fork();
		
		// Test Case 5: Wake all when sleepingQueue is empty
		KThread wakeAll2 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 5: Waking an empty nest...");
                testCond.wakeAll();     
                System.out.println("Test Case 5: No one home to wake..."); 
				System.out.println("Test Case 5: Complete");
				testLock.release();
        } } ).setName("Test 5");
		wakeAll2.fork();
	
	}

    private Lock conditionLock;
	private static qQueue sleepingQueue = new qQueue();
}
