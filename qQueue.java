package nachos.threads;

import java.util.LinkedList;
import java.util.ListIterator;

public class qQueue{

	protected LinkedList <Object>linked;
	Lock queueLock = new Lock();
	
	public qQueue(){
	
		linked = new LinkedList<Object>();

	}


	public void add(KThread thread) {

        queueLock.acquire();
	linked.add(thread);
	queueLock.release();
	}
	
	public void add(Pair shared){

                queueLock.acquire();
		linked.add((Pair)shared);
		queueLock.release();
	}
	
    public void add(alarmObjs snooze) {

        queueLock.acquire();

   if (this.isEmpty() != true) {                /* if linked isn't empty, need to add at correct position. */

       ListIterator walk = linked.listIterator();
    while(walk.hasNext()) {

            alarmObjs current = (alarmObjs) walk.next();

            if (current.compareTo(snooze)== 1) {

                linked.add(walk.previousIndex(),snooze);

                break;

            }

            else if ( current.compareTo(snooze)== 0) {

                linked.add(walk.nextIndex(), snooze);
                break;
            }
            else{

                walk.next();

            }
        }
    linked.add(walk.nextIndex(), snooze);       /* went through entire list, walk has no next element, meaning snooze will need to be woken after all other thread, add snooze to end of list. */
    }
 else {
         linked.add(snooze); //List is empty, append object.
     }
        queueLock.release();
}

    public void remove(Object target) {
	
	queueLock.acquire();
      linked.remove(target);
	queueLock.release();
	}
    public void remove(int index) {

	queueLock.acquire();
        linked.remove(index);
	queueLock.release();
	}
    public Object removeFirst() {

	queueLock.acquire();
    Object answer = linked.removeFirst();
	queueLock.release();
	return answer;
}
	
    public Object get(int index){
    
        queueLock.acquire();
        Object answer = linked.get(index);
        queueLock.release();
        return answer;
}
public boolean isEmpty() {

    queueLock.acquire();
    if (linked.size() == 0){

        queueLock.release();
	return true;
    }
          
    else{
         queueLock.release();
         return false;
    }
}
    
    public int size(){
    
         queueLock.acquire();
         int size = linked.size();
         queueLock.release();
         return size;
    }

    public Object getFirst(){

        queueLock.acquire();
        Object answer = linked.getFirst();
        queueLock.release();
        return answer;
    }

}
