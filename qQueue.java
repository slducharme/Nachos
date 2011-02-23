package nachos.threads;

import java.util.LinkedList;
import java.util.ListIterator;
/*
 * qQueue is a class to store all desired objects required for phase one. The add methods are kept simple by simple overrides, so that the desired items
 * can be added in their specific ways. Remove methods that are required to return, do so with a <Object> and need to be typecast where they are called.
 * Objects placed onto a qQueue are stored in a linked list. The class has a lock object to ensure that only one thread can be altering an individual object of
 * type qQueue. Example: A thread should be able to modify the join qQueue (jqueue) without waiting for the lock from a thread modifying a alarm qQueue. They should however have to
 * wait on other thread modifying the same object, to ensure atomicity.
 *
 * Developed Jason Major. Shawna Durcharme for phase #1 nachos.
 * 
 */
public class qQueue
{

	protected LinkedList <Object>linked;
	Lock queueLock = new Lock();

	public qQueue()
	{
		linked = new LinkedList<Object>();
	}

	public void add(KThread thread) 
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		linked.add(thread);
		queueLock.release();
	}

	public void add(Pair shared)
	{
		if (!queueLock.isHeldByCurrentThread()) 
		{
			 queueLock.acquire();
		}
		linked.add((Pair)shared);
		queueLock.release();
	}

	public void add(alarmObjs snooze) 
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}

		if (!isEmpty()) 
		{ /* if linked isn't empty, need to add at correct position. */

			ListIterator walk = linked.listIterator();
			while(walk.hasNext()) 
			{
				alarmObjs current = (alarmObjs) walk.next();
				if (current.compareTo(snooze)== 1) 
				{
					linked.add(walk.previousIndex(),snooze);
					break;
				}
				else if ( current.compareTo(snooze)== 0) 
				{
					linked.add(walk.nextIndex(), snooze);
					break;
				}
				else
				{
				   if(walk.hasNext()){ walk.next();}
				   else{ break; }
				}
			}
			linked.add(walk.nextIndex(), snooze); /* went through entire list, walk has no next element, meaning snooze will need to be woken after all other thread, add snooze to end of list. */
		}
		else 
		{
			 linked.add(snooze); //List is empty, append object.
		}
		if (queueLock.isHeldByCurrentThread()) 
		{
			queueLock.release();
		}
	}
	
	public void remove(Object target) 
	{

		if (!queueLock.isHeldByCurrentThread())
		{
		 queueLock.acquire();
		}
		linked.remove(target);
		queueLock.release();
	}
	
	public void remove(int index) 
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		linked.remove(index);
		queueLock.release();
		//return answer;
	}
	
	public Object removeFirst() 
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		Object answer = linked.removeFirst();
		queueLock.release();
		return answer;
	}

	public Object get(int index)
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		Object answer = linked.get(index);
		queueLock.release();
		return answer;
	}
 /* Return if the qQueue is empty(NO elements) */
	public boolean isEmpty() 
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		if (linked.size() == 0)
		{
			queueLock.release();
			return true;
		}
		else
		{
			 queueLock.release();
			 return false;
		}
	}
/* Returns the size of the qQueue */
	public int size()
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		int size = linked.size();
		queueLock.release();
		return size;
	}
/* Returns the first object ontop of the queue */
	public Object getFirst()
	{
		if (!queueLock.isHeldByCurrentThread())
		{
			queueLock.acquire();
		}
		Object answer = linked.getFirst();
		queueLock.release();
		return answer;
	}
/* With objects of type alarmObjs it is easier to have this method in place than to constantly type cast. Allows the caller to see the alarm time of a object
 * for comparison. Returns a -49 as an escape to prevent infinite loops inplace of 0 incase the calling threads sleep time is actually 0 as well
 **/
        public long peeking(){

            if(linked.isEmpty()){

                return -49;

            }
            else{

               return ( (alarmObjs) linked.getFirst() ).getTimer();

            }
        }
  /**
   * Each method in qQueue closely mimics linkedlist methods and therefore little testing is required. Most testing of this class
   * is witnessed in it's utilization in other selfTest() methods. Important to test that alarmObjs are sorted however.
   *
   */
        public static void selfTest() {
            
            boolean success = true;
            KThread spartan = new KThread( new KThread.TestThread(1)).setName("Spartan Threads");
            KThread spartan2 = new KThread( new KThread.TestThread(1)).setName("Spartan Threads");
            KThread spartan3 = new KThread( new KThread.TestThread(1)).setName("Spartan Threads");
            KThread spartan4 = new KThread( new KThread.TestThread(1)).setName("Spartan Threads");
            alarmObjs ob1 = new alarmObjs(spartan, 100);
            alarmObjs ob2 = new alarmObjs(spartan2, 150);
            alarmObjs ob3 = new alarmObjs(spartan3, 50);
            alarmObjs ob4 = new alarmObjs(spartan4, 160);
            
            
            qQueue alarms = new qQueue();
            alarms.add(ob1);
            if( alarms.isEmpty()){ success = false; }
            alarms.add(ob2);
            alarms.add(ob3);
            alarms.add(ob4);
            for (int i = 0; i < (alarms.size()-1) ; i++){               // Ensures the list is processed
                
                if(((alarmObjs)alarms.get(i)).getTimer() > ((alarmObjs) alarms.get(i+1)).getTimer()){ //Ensures elements of a alarmQueue are being sucessfully prioritized.
                    
                    success = false;
                }
                
            }
            if(success){ System.out.println(" Test Objects in an Alarm queue were prioritized."); } //Outputs confirmation that qQueues of type alarmObjs are Prioritiezed.
        }

}