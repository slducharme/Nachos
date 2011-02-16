package nachos.threads;

import nachos.machine.*;

public class ReactWater{

	qQueue hydroWait = new qQueue();
	qQueue oxyWait = new qQueue();
	int hydroCount = 0;
	int oxyCount = 0;
	Lock modLock = new Lock();
	
    /** 
     *   Constructor of ReactWater
     **/
    public ReactWater() {

    } // end of ReactWater()

    /** 
     *   When H element comes, if there already exist another H element 
     *   and an O element, then call the method of Makewater(). Or let 
     *   H element wait in line. 
     **/ 
    public void hReady() {
		hydroWait.add(this);
		hydroCount++;
		if(oxyWait.isEmpty() || hydroCount < 2)
		{
			sleep();
		}	
		else
		{
			hydroWait.nextThread().wake();
			oxyWait.wake();
			makeWater();
		}
    } // end of hReady()
 
    /** 
     *   When O element comes, if there already exist another two H
     *   elements, then call the method of Makewater(). Or let O element
     *   wait in line. 
     **/ 
    public void oReady() {
		oxyWait.add(this);
		oxyCount++;
		if(hydroCount < 2)
		{
			sleep();
		}
		else
		{
			hydroWait.wake();
			hydroWait.nextThread().wake();
			makeWater();
		}
    } // end of oReady()
    
    /** 
     *   Print out the message of "water was made!".
     **/
    public void Makewater() 
	{
		Lib.assertTrue(hydroCount > 2);
		Lib.assertTrue(oxyCount > 1);
		
		modLock.acquire();
		
		while(hydroCount > 2 && oxyCount > 1)
		{
			System.out.println("Water has been made");
			hydroCount = hydroCount - 2;
			oxyCount--;
		}
		
		modLock.release();

    } // end of Makewater()

} // end of class ReactWater


