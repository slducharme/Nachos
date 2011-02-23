package nachos.threads;

import java.util.Calendar;
import nachos.machine.*;

/**
 * Uses the hardware timer to provide preemption, and to allow threads to sleep
 * until a certain time.
 */
public class Alarm {

    protected static qQueue alarmQue = new qQueue();
    /**
     * Allocate a new Alarm. Set the machine's timer interrupt handler to this
     * alarm's callback.
     *
     * <p><b>Note</b>: Nachos will not function correctly with more than one
     * alarm.
     */
    public Alarm() {
	Machine.timer().setInterruptHandler(new Runnable() {
		public void run() { timerInterrupt(); }
	    });
    }

    /**
     * The timer interrupt handler. This is called by the machine's timer
     * periodically (approximately every 500 clock ticks). Causes the current
     * thread to yield, forcing a context switch if there is another thread
     * that should be run.
     */
    public void timerInterrupt() {

      boolean intStatus = Machine.interrupt().disable();
      long currentTime = Machine.timer().getTime();
      if(!alarmQue.isEmpty()){
          
      while(alarmQue.peeking() != -49 && alarmQue.peeking() < currentTime){
       
     

           alarmObjs wakeCall =(alarmObjs) alarmQue.removeFirst();
           wakeCall.sleepy.ready();
        }
 
        Machine.interrupt().restore(intStatus);
        }
       
    }

    /**
     * Put the current thread to sleep for at least <i>x</i> ticks,
     * waking it up in the timer interrupt handler. The thread must be
     * woken up (placed in the scheduler ready set) during the first timer
     * interrupt where
     *
     * <p><blockquote>
     * (current time) >= (WaitUntil called time)+(x)
     * </blockquote>
     *
     * @param	x	the minimum number of clock ticks to wait.
     *
     * @see	nachos.machine.Timer#getTime()
     */
    public void waitUntil(long x) {


        long wakeTime = Machine.timer().getTime() + x;
        KThread current = KThread.currentThread();

        
        alarmObjs snooze = new alarmObjs(current, wakeTime);

        alarmQue.add(snooze);
        Machine.interrupt().disable();
        KThread.sleep();

    }

    public static void selfTest() {

    new KThread(new Runnable(){
   
    public void run(){
    System.out.println("Alarm Test Cases");
    KThread test1 = new KThread(new TestNaps(10)).setName("DebugAlarm1"); // Standard wait until, should wake on first check a.k.a 500 ticks.
    test1.fork();
    
    KThread test2 = new KThread(new TestNaps(700)).setName("DebugAlarm2"); // Standard wait until, longer, should be woken at 3 check, and back on the readyQueue to finish.
    test2.fork();
    
    KThread normal = new KThread(new KThread.TestThread(1)).setName("normal"); // Thread should run and finish before the sleeping, alarmed threads wake.
    normal.fork();
    KThread test3 = new KThread(new TestNaps(600)).setName("DebugAlarm3");
    test3.fork();
    
    KThread test4 = new KThread(new TestNaps(1500)).setName("DebugAlarm4");
    test4.fork(); // Testing to ensure threads are being prioritized upon waking, threads 2 3 should wake at same time, 4 should wake later.
    
    
    }
      }).setName("Alarm Test Cases").fork();
}
/*
 * Test classed built to allow for KThreads to call waitUntil based on a (long) time given. Based on PingTest. 
 */
    static class TestNaps implements Runnable {

        private long napLength;
        public long timeIwoke;
        public TestNaps(long timeTowait){

            napLength = timeTowait;

        }
       public void run(){

            System.out.println("I" + KThread.currentThread().getName() + " going to sleep at " + Machine.timer().getTime() );
            ThreadedKernel.alarm.waitUntil(napLength);
            timeIwoke = Machine.timer().getTime();
            System.out.println("I" + KThread.currentThread().getName() + " woke up at " + timeIwoke );
        }
   }

}

