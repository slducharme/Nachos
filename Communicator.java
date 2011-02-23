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
		if(!commLock.isHeldByCurrentThread()){ commLock.acquire(); }
		++speakerCount;
            while((listenerCount == 0) || inboxFull){

                condSpeak.sleep();

             }

		
		
		MESG = word;
                inboxFull = true;   // Going to be placing a Messgae
                condListen.wake();  //Wake any(the) sleeping listners
		--speakerCount;
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
		if(!commLock.isHeldByCurrentThread()){ commLock.acquire(); }
		condSpeak.wakeAll();
		++listenerCount;

		while(!inboxFull){ condListen.sleep(); }      // No message has been spoken, listners wait.
			

		int word = MESG;        //Recieve the message and update the inbox
		inboxFull = false;
		--listenerCount;

		condSpeak.wakeAll();    //Remind any other speakers to check if there are additional listeners
		commLock.release();
		return word;
	}

	public static void selfTest()
	{
            /* Speaker in first, standard transfer */
            System.out.println("------------Communicator SelfTest Output-----------");
            System.out.println("<---Test Speaker First #1--->");
            Communicator tester = new Communicator();
            KThread say = new KThread(new Talk(tester, 70)).setName("Said70");
            say.fork();
            System.out.println(" No output till here indicates test thread is waiting for listner");
            System.out.println(" Threads should have shared : 70 ");
            KThread list= new KThread(new Hear(tester)).setName("Listner1");
            list.fork();
            list.join();
            //List1 should now pick up say1, both should output the transferred message.
           
            
            /* Listner in first */
            System.out.println("<--Test Listner First #2--->");
            KThread listF = new KThread(new Hear(tester)).setName("Listner1"); // Listener comes in first
            listF.fork();
            System.out.println("Should be no new output since header, listner should be waiting");
            System.out.println("Threads should have shared : 75 ");
            KThread sayL = new KThread(new Talk(tester, 75)).setName("Said70");
            sayL.fork();
            sayL.join();
            
            /* Multi thread waiting */

            System.out.println("<---Test Many Speakers #3--->");
            Communicator multiTest = new Communicator();
            KThread say1 = new KThread(new Talk(multiTest, 10)).setName("Said10");
            KThread say2 = new KThread(new Talk(multiTest, 79)).setName("Said79");
            KThread say3 = new KThread(new Talk(multiTest, 30)).setName("Said30");

            say1.fork();
            say2.fork(); // Not joined as never expected to be heard. Alright if main nachos forgets these threads.
            say3.fork();
            System.out.println("Only listner should have shared 10");
            KThread list1 = new KThread(new Hear(multiTest)).setName("Listner1");
            list1.fork();
            list1.join();
            System.out.println("<---Test Many Speakers and Matching listeners #4--->");
            Communicator multiTest2 = new Communicator();   // New communicator used as old multiTest still has speak threads on it. Forever left waiting.
            KThread say4 = new KThread(new Talk(multiTest2, 11)).setName("Said10");
            KThread say5 = new KThread(new Talk(multiTest2, 69)).setName("Said79");
            KThread say6 = new KThread(new Talk(multiTest2, 45)).setName("Said30");

            say4.fork();
            say5.fork();
            say6.fork();
            System.out.println("Should have shared 11");
            KThread list4 = new KThread(new Hear(multiTest2)).setName("Listner1");
            list4.fork();
            list4.join();
            System.out.println("Should have shared 69");
            KThread list5 = new KThread(new Hear(multiTest2)).setName("Listner1");
            list5.fork();
            list5.join();
            System.out.println("Should have shared 45");
            KThread list6 = new KThread(new Hear(multiTest2)).setName("Listner1");
            list6.fork();
            list6.join();
            
           System.out.println("There should have been 3 Words Spoken and 3 Words Heard in test #4. End."); // Should be seen at the conclusion of all test threads.
            
        }

/*
 * Talk and Hear are two internal classes modeled after PingTest but designed to allow for threads to exist to share a(n) int. They implement runnable and call
 * and exist based on a Communicator object, which gives them access to their sole purpose: Speak() and Listen(); methods which handle the actual transfer of the word.
 */
	private static class Talk implements Runnable
	{
		int MESG;
		Communicator communicator;

		public Talk(Communicator comm, int word)
		{
			MESG = word;
			communicator = comm;
		}

		public void run()
		{
			
			communicator.speak(MESG);
                        System.out.println("Said: " + MESG);
		}
	}
/*
 * Talk and Hear are two internal classes modeled after PingTest but designed to allow for threads to exist to share a(n) int. They implement runnable and call
 * and exist based on a Communicator object, which gives them access to their sole purpose: Speak() and Listen(); methods which handle the actual transfer of the word.
 */
	private static class Hear implements Runnable
	{
		int message;
		Communicator communicator;

		public Hear(Communicator comm)
		{
			message = 0;
			communicator = comm;
		}

		public void run()
		{
                        message = communicator.listen();
			System.out.println("Heard: " + message);
		}
	}
}



