package nachos.threads;

import nachos.machine.*;
public class Pair
{

   protected int friendID;
   protected KThread tired;


    public  Pair(KThread thread, int id) {
    
    friendID = id;
    tired = thread;
    
    }

}
