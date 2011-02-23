package nachos.threads;

import nachos.machine.*;

public class ReactWater{

	private static qQueue hydroWait = new qQueue();
	private static qQueue oxyWait = new qQueue();
	private int hydroCount;
	private int oxyCount;
	private Lock modLock = new Lock();
	
    /** 
     *   Constructor of ReactWater
     **/
    public ReactWater(Lock modLock) 
	{
		this.modLock = modLock;
		hydroCount = 0;
		oxyCount = 0;
    } // end of ReactWater()

    /** 
     *  When H element comes in it is added to the hydro queue, 
	 *  hydro count is increased by 1 and makewater is called
	 */
    public void hReady() {
		hydroCount++;
		hydroWait.add(KThread.currentThread());
		Makewater();
    } // end of hReady()
 
    /** 
     *   When O element comes in it is added to the oxy queue,
	 *   oxy count is increased by 1 and makewater is called
     **/ 
    public void oReady() {
		oxyCount++;
		oxyWait.add(KThread.currentThread());
		Makewater();
    } // end of oReady()
    
    /** 
     *   Checks if the appropriate number of threads are available for making water and 
	 *   then prints out "Water has been made" and decrements the counters and removes the threads
	 *   from their queues
     **/
    public void Makewater() 
	{
		Lib.assertTrue(modLock.isHeldByCurrentThread());	
		while(hydroCount > 1 && oxyCount > 0)
		{
			System.out.println("Water has been made");
			hydroCount = hydroCount - 2;
			oxyCount--;
			oxyWait.removeFirst();
			hydroWait.removeFirst();
			hydroWait.removeFirst();
		}

    } // end of Makewater()
	/**
	 * Self tester and debugger for ReactWater.
	 * Some test cases from our design document have been excluded as they were unnecessary.
	 */
	public static void selfTest()
	{
		final Lock testLock = new Lock();
		final ReactWater react = new ReactWater(testLock);
		KThread water1 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
				System.out.println("-----------------------React Water Testing----------------------------");
                System.out.println("Test Case 1: 1 Hydrogen Atom");
                react.hReady();    
                System.out.println("Test Case 1: Water shouldn't have been made because we only have " + react.hydroCount + " hydrogen atoms and " + react.oxyCount + " oxygen atoms."); 
				System.out.println("Test Case 1: Complete");
				testLock.release();
				
        } } ).setName("Test 1");
		water1.fork();
		
		KThread water2 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 2: 2 Hydrogen Atoms");
                react.hReady();    
                System.out.println("Test Case 2: Water shouldn't have been made because we only have " + react.hydroCount + " hydrogen atoms and " + react.oxyCount + " oxygen atoms."); 
				System.out.println("Test Case 2: Complete");
				testLock.release();
				
        } } ).setName("Test 2");
		water2.fork();
		//water1.join();
		
		KThread water3 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 3: 3 Hydrogen Atoms");
                react.hReady();    
                System.out.println("Test Case 3: Water shouldn't have been made because we only have " + react.hydroCount + " hydrogen atoms and " + react.oxyCount + " oxygen atoms."); 
				System.out.println("Test Case 3: Complete");
				testLock.release();
				
        } } ).setName("Test 3");
		water3.fork();
		
		
		KThread water4 = new KThread(new Runnable(){

            public void run(){
                testLock.acquire();
                System.out.println("Test Case 4: 3 Hydrogen Atoms, 1 Oxygen Atom");
                react.oReady();    
                System.out.println("Test Case 4: Water should have been made with " + react.hydroCount + " hydrogen atoms and " + react.oxyCount + " oxygen atoms left over."); 
				System.out.println("Test Case 4: Complete");
				testLock.release();
				
        } } ).setName("Test 4");
		water4.fork();
		//water1.join();
		//water2.join();	
		
		
	}

} // end of class ReactWater


