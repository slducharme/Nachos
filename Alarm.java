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
       if (!alarmQue.isEmpty()){
       

       alarmQue.alarmTill(currentTime);
       }
 
        Machine.interrupt().restore(intStatus);

       
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

        Machine.interrupt().disable();
        alarmObjs snooze = new alarmObjs(current, wakeTime);

        alarmQue.add(snooze);

        KThread.currentThread().sleep();

    }

    public static void selfTest() {

    KThread test1 = new KThread(new TestNaps(10)).setName("DebugAlarm1");
    KThread test2 = new KThread(new TestNaps(20)).setName("DebugAlarm2");
    KThread test3 = new KThread(new TestNaps(234)).setName("DebugAlarm3");
    KThread test4 = new KThread(new TestNaps(1120)).setName("DebugAlarm4");
    test1.fork();
    test2.fork();
    test3.fork();
    test4.fork();
    System.out.println("What");

  }

    static class TestNaps implements Runnable {

        private long napLength;
        public long timeIwoke;
        public TestNaps(long timeTowait){

            napLength = timeTowait;

        }
       public void run(){

            
            ThreadedKernel.alarm.waitUntil(napLength);
            timeIwoke = Machine.timer().getTime();
            System.out.println("I" + KThread.currentThread().getName() + "woke up at" + timeIwoke );
        }
   }

}

