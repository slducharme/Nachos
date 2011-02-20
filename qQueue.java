package nachos.threads;

import java.util.LinkedList;

import nachos.machine.*;

public class qQueue{

	protected LinkedList linked;
	
	
	public qQueue(){
	
		linked = new LinkedList(null);

	}


	public add(Kthread thread) {
	
	linked.add(thread);
	
	}
	
	public add(Pair shared){
	
		linked.add(shared);
		
	}
	
	// public add(AlarmObjs snooze) {}
	
	public void remove(Object target) {
	
			linked.remove(target);
			
	}
	
	public boolean isEmpty() {
	
		if (linked.size() == 0)
		
			return true;
	
		else return false;
	}
    
    public  int size(){
    
        return linked.size();
    

}