package nachos.threads;

import nachos.machine.*;
public class Pair
{

   protected int friendID;
   protected Kthread tired;


    public  Pair(Kthread thread, int id) {
    
    friendID = id;
    tired = thread;
    
    }

}


//  Too tired to finish, btw you're gay (awe)