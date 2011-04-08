package nachos.userprog;

import nachos.machine.*;
import nachos.threads.*;
import nachos.userprog.*;

/**
 * A kernel that can support multiple user processes.
 */
public class UserKernel extends ThreadedKernel {
    /**
     * Allocate a new user kernel.
     */
    public UserKernel() {
	super();
    }

    /**
     * Initialize this kernel. Creates a synchronized console and sets the
     * processor's exception handler.
     */
    public void initialize(String[] args) {
	super.initialize(args);

	console = new SynchConsole(Machine.console());
	
	Machine.processor().setExceptionHandler(new Runnable() {
		public void run() { exceptionHandler(); }
	    });
    }

	/**
	 * Initializes input position to -1 so that if the number of frames available is less than numPages, -1 indicates that there is not a contiguous block available and that a non contiguous approach is now necessary
	 * A lock is used to ensure synchronization when accessing the table
	 * When a contiguous block is found, the start position is stored in input pos to be returned later
	 * That section is removed from the free list and the number of free frames available is decremented
	 * The new amount of frames left is then calculated and re-added to the free list
	 */
	private int ContiguousAllocate(int numPages)
	{
		int inputPos = -1;
		FTLock.acquire();
		ListIterator walk = freeList.listIterator();
		while(walk.hasNext())
		{
			FreeFrames frames =  walk.next();
			if(frames.getFrames() >= numPages)
			{
				inputPos = frames.getStart();
				freeList.remove(frames);
				freeFrames = freeFrames - numPages;
				if(numPages < frames.getFrames())
				{
					int newStart = frames.getStart();						
					int newFrames = frames.getFrames() - numPages;
					FreeFrames newFrame = new FreeFrames(newStart,freeFrames);			
					freeList.add(newFrame);
				}
			}
			
		}

		FTLock.release();
		return inputPos;	
	}
	
	/** 
	 * Method checks if the pages can be allocated contiguously
	 * If not then, searches through the free list looking for smaller chunks and adds them to a linked list
	 * When enough frames have been collected, the linked list is returned
	 * A lock is used to ensure synchronization
	 */
	
	private LinkedList<FreeFrames> Allocate(int numPages)
	{
		LinkedList<FreeFrames> pieces = new LinkedList<FreeFrames>;
		FTLock.acquire();
		int checkContiguous = ContiguousAllocate(numPages);
		if(checkContiguous == -1)
		{
			ListIterator walk = freeList.listIterator();
			int frameCount = 0;
			while(walk.hasNext())
			{
				FreeFrames smallChunks = walk.next();
				frameCount = frameCount + smallChunks.getFrames();
				if (frameCount < numPages)
					pieces.add(smallChunks);
				else
					return pieces;
			}
		}
		else
		{
			int start = ContiguousAllocate(numPages);
			FreeFrames contig = new FreeFrames(start, numPages);
			pieces.add(contig);
		}
		FTLock.release();
		return pieces;
				
	}

	/**
	 * Method takes a linked list to be unallocated as its argument
	 * Uses FreeFrames method getStartPosForNext to determine the start positions of each section of frames to be added back to free list
	 * A new FreeFrames object is created using the start position and number of frames in each element of the unallocated list and that object is added back to freeList
	 * The number of freeFrames is updated as each element is traversed
	 * A lock is used to ensure synchronization
	 */
	private void Unallocate(LinkedList<FreeFrames> unalloList)
	{
		FTLock.acquire();
		int start;
		ListIterator walk = unalloList.listIterator();
		while(walk.hasNext())
		{
			if(freeList.getLast() == NULL)
			{
				start = 0;
			}
			else
			{	
				FreeFrames helper = freeList.getLast();
				start = helper.getStartPosForNext();
			}
			FreeFrames UnalloTemp = walk.next;
			FreeFrames Unallo = new FreeFrames(start, UnalloTemp.getFrames());
			freeFrames = freeFrames + UnalloTemp.getFrames();					
			freeList.add(Unallo);
		}
		FTLock.release();
				
	}
	
    /**
     * Test the console device.
     */	
    public void selfTest() {
	super.selfTest();

	System.out.println("Testing the console device. Typed characters");
	System.out.println("will be echoed until q is typed.");

	char c;

	do {
	    c = (char) console.readByte(true);
	    console.writeByte(c);
	}
	while (c != 'q');

	System.out.println("");
    }

    /**
     * Returns the current process.
     *
     * @return	the current process, or <tt>null</tt> if no process is current.
     */
    public static UserProcess currentProcess() {
	if (!(KThread.currentThread() instanceof UThread))
	    return null;
	
	return ((UThread) KThread.currentThread()).process;
    }

    /**
     * The exception handler. This handler is called by the processor whenever
     * a user instruction causes a processor exception.
     *
     * <p>
     * When the exception handler is invoked, interrupts are enabled, and the
     * processor's cause register contains an integer identifying the cause of
     * the exception (see the <tt>exceptionZZZ</tt> constants in the
     * <tt>Processor</tt> class). If the exception involves a bad virtual
     * address (e.g. page fault, TLB miss, read-only, bus error, or address
     * error), the processor's BadVAddr register identifies the virtual address
     * that caused the exception.
     */
    public void exceptionHandler() {
	Lib.assertTrue(KThread.currentThread() instanceof UThread);

	UserProcess process = ((UThread) KThread.currentThread()).process;
	int cause = Machine.processor().readRegister(Processor.regCause);
	process.handleException(cause);
    }

    /**
     * Start running user programs, by creating a process and running a shell
     * program in it. The name of the shell program it must run is returned by
     * <tt>Machine.getShellProgramName()</tt>.
     *
     * @see	nachos.machine.Machine#getShellProgramName
     */
    public void run() {
	super.run();

	UserProcess process = UserProcess.newUserProcess();
	
	String shellProgram = Machine.getShellProgramName();	
	Lib.assertTrue(process.execute(shellProgram, new String[] { }));

	KThread.currentThread().finish();
    }

    /**
     * Terminate this kernel. Never returns.
     */
    public void terminate() {
	super.terminate();
    }

    /** Globally accessible reference to the synchronized console. */
    public static SynchConsole console;

	private LinkedList<FreeFrames> freeList = new LinkedList<FreeFrames>;
	private int freeFrames;
	private Lock FTLock = new Lock();
	
    // dummy variables to make javac smarter
    private static Coff dummy1 = null;
	

}
