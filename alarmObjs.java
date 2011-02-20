package nachos.threads;

import nachos.machine.*;

public class alarmObjs implements Comparable {

    protected KThread sleepy;
    protected long timer;

    public alarmObjs( KThread thread, long waketime ) {
    
    
        sleepy = thread;
        timer = waketime;
        
        
    }
    
    
    
    public int compareTo(Object otherAlarm){
    
        if  (this.timer == ((alarmObjs)otherAlarm).timer) {
        
            return 0;   //Both threads need be woken at the same time
            
              
        }
        
        else if ( this.timer > ((alarmObjs)otherAlarm).timer ) {
        
            return 1;       // Timer is before the other object
        }
    
        else   
        
            return -1;      // The other timer is larger than the current one
    
    
    }

    

}




