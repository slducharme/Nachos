package nachos.threads;

import nachos.machine.*;

/**
 * A <i>communicator</i> allows threads to synchronously exchange 32-bit
 * messages. Multiple threads can be waiting to <i>speak</i>,
 * and multiple threads can be waiting to <i>listen</i>. But there should never
 * be a time when both a speaker and a listener are waiting, because the two
 * threads can be paired off at this point.
 */
public class Communicator {

	 private int MESG;
	 private int speakerCount;
	 private int listenerCount;	 
	 private Lock commLock;
	 private Condition condSpeak;
	 private Condition condListen;
	 private boolean inboxFull;
    /**
     * Allocate a new communicator.
     */
	
    public Communicator() 
	{
		inboxFull = false;
		speakerCount = 0;
		listenerCount = 0;
		commLock = new Lock();
		condSpeak = new Condition(commLock);
		condListen = new Condition(commLock);
    }

    /**
     * Wait for a thread to listen through this communicator, and then transfer
     * <i>word</i> to the listener.
     *
     * <p>
     * Does not return until this thread is paired up with a listening thread.
     * Exactly one listener should receive <i>word</i>.
     *
     * @param	word	the integer to transfer.
     */
    public void speak(int word) 
	{
		speakerCount++;
		commLock.acquire();
		while(listenerCount <= 0 || inboxFull == true)
		{
			condSpeak.sleep();
		}
			
		MESG = word;
		inboxFull = true;
		condListen.wake();
		speakerCount--;	
		commLock.release();
    }

    /**
     * Wait for a thread to speak through this communicator, and then return
     * the <i>word</i> that thread passed to <tt>speak()</tt>.
     *
     * @return	the integer transferred.
     */    
    public int listen() 
	{
		int temp;
		listenerCount++;
		commLock.acquire();
		while(speakerCount <= 0 || inboxFull == false)
		{
			condListen.sleep();
		}
		if(inboxFull == false)
		{
			condSpeak.wake();
			condListen.sleep();
		}
		else
		{
			temp = MESG;
			inboxFull == false;
			listenerCount--;
			commLock.release();
			return temp;
		}
		return 0;
    }
	
	public static void selfTest()
	{
		
	}
}
