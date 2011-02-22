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

        if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
linked.add(thread);
queueLock.release();
}

public void add(Pair shared){

                if (!queueLock.isHeldByCurrentThread()) {
                         queueLock.acquire();

                }
linked.add((Pair)shared);
queueLock.release();
}

public void add(alarmObjs snooze) {

     if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }

   if (!isEmpty()) { /* if linked isn't empty, need to add at correct position. */

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

               if(walk.hasNext()){ walk.next();}
               else{ break; }

            }
        }
    linked.add(walk.nextIndex(), snooze); /* went through entire list, walk has no next element, meaning snooze will need to be woken after all other thread, add snooze to end of list. */
    }
 else {
         linked.add(snooze); //List is empty, append object.
     }
        if (queueLock.isHeldByCurrentThread()) {
            queueLock.release();
        }
}

public void remove(Object target) {

if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
        linked.remove(target);
queueLock.release();
}
public void remove(int index) {

if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
        linked.remove(index);
queueLock.release();
}
public Object removeFirst() {

if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
 Object answer = linked.removeFirst();
queueLock.release();
return answer;
}

public Object get(int index){
    
        if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
        Object answer = linked.get(index);
        queueLock.release();
        return answer;
}
public boolean isEmpty() {

    if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
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
    
    if (!queueLock.isHeldByCurrentThread())
    {
     queueLock.acquire();

    }
         int size = linked.size();
         queueLock.release();
         return size;
    }

public Object getFirst(){

     if (!queueLock.isHeldByCurrentThread())
     {

      queueLock.acquire();

     }

        Object answer = linked.getFirst();
        queueLock.release();
        return answer;
    }
 public alarmObjs popAlarm(){

     alarmObjs answer;
     if (!queueLock.isHeldByCurrentThread() ){ queueLock.acquire(); }

        answer = (alarmObjs) linked.removeFirst();
        queueLock.release();
        return answer;
    }

  public void alarmTill(long wakeCall){


     if (!queueLock.isHeldByCurrentThread() ){ queueLock.acquire(); }
                if(!isEmpty()){
                ListIterator walk = linked.listIterator();
     while(((alarmObjs)walk.next()).getTimer() <= wakeCall && walk.hasNext()){
                    alarmObjs current = (alarmObjs) walk.next();
                    current.sleepy.ready();
                    if(walk.hasNext()){walk.next();}
                    else{break;}
        }
      }
    }
}