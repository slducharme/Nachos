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
    public Condition2(Lock conditionLock) {
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
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		Lib.assertTrue(currentThread.status != 4);	// Assure that thread status is not finished
		Machine.interrupt().disable();
		sleepingQueue.add(currentThread);
		condition == false;
		conditionLock.release();
		while(condition == false)
		{
			currentThread.status == 3; // Status blocked
		}
		currentThread.status = 1;
		Machine.interrupt().enable();
		conditionLock.acquire();
		
    }

    /**
     * Wake up at most one thread sleeping on this condition variable. The
     * current thread must hold the associated lock.
     */
    public void wake() 
	{
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		if(sleepingQueue isEmpty() == false)
		{
			Machine.interrupt().disable();
			sleepingQueue.remove(this);
			condition == true;
			Machine.interrupt().enable();
		}
    }

    /**
     * Wake up all threads sleeping on this condition variable. The current
     * thread must hold the associated lock.
     */
    public void wakeAll() {
	
		Lib.assertTrue(conditionLock.isHeldByCurrentThread());
		while(sleepingQueue isEmpty() == false)
		{
			sleepingQueue.nextThread().wake();
		}
    }

    private Lock conditionLock;
	protected Boolean condition;
	public qQueue sleepingQueue = new qQueue();
}
