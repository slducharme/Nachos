package nachos.threads;

import nachos.machine.*;

public class alarmObjs implements Comparable {

    protected KThread sleepy;
    protected long timer;

    public alarmObjs( KThread thread, long waketime ) {
    
    
        sleepy = thread;
        timer = waketime;
        
        
    }

    public long getTimer(){ return timer;}
    public KThread getThread(){return sleepy;}

    
    
    public int compareTo(Object otherAlarm){
    
        if  (this.timer == ((alarmObjs)otherAlarm).timer) {
        
            return 0;   //Both threads need be woken at the same time
            
              
        }
        
        else if ( this.timer > ((alarmObjs)otherAlarm).timer ) {
        
            return 1;       // Timer is before the other object
        }
    
        else { return -1; }      // The other timer is larger than the current one
   
    }

    public static void selfTest(){

        alarmObjs test = new alarmObjs(KThread.currentThread(), 1231231213 );
        Lib.assertTrue(test.timer != 0);
        Lib.assertTrue(test.sleepy != null); // Objects contain both the timme and the thread to be awoken.



    }

    

}




