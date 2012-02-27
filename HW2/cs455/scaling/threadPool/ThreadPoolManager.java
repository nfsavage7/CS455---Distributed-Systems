package cs455.scaling.threadPool;

/* java imports */
import java.util.Queue;
import java.util.LinkedList;
import java.util.ListIterator;

/* Local imports */
import cs455.scaling.tasks.*;
/* ************************************************************************************************************************ */
/*                                                    Thread Pool Manager                                                   */
/*                                                   ---------------------                                                  */
/* 	This class is responsible for managing the tasks that need to be done. It will also assign the tasks to the threads */
/* ************************************************************************************************************************ */

public class ThreadPoolManager{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private Worker[] workers;

	private Queue<Task> taskQueue = new LinkedList<Task>();
	private Queue<Worker> workerQueue = new LinkedList<Worker>();

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public ThreadPoolManager(int threads){
		workers = new Worker[threads];
		for(int i = 0; i < threads; i++){
			workers[i] = new Worker(this);
			workerQueue.offer(workers[i]);
		}
		System.out.println("Worker Queue size: " + workerQueue.size());
	}

	/* **************************************************************************************************************** */
	/*                                               Getter and setter methods                                          */
	/* **************************************************************************************************************** */

	public void addTask(Task t){
		ListIterator iter = ((LinkedList)(taskQueue)).listIterator(0);
		while(iter.hasNext()){
			Task tmp = (Task)iter.next();
			iter.remove();
			if(tmp.equals(t)){
				return;
			}
		}
		taskQueue.offer(t);
		assignTasks();
	}

	public void assignTasks(){
		while (workerQueue.size() > 0 && taskQueue.size() > 0){
			Task t = taskQueue.poll();
			Worker w = workerQueue.poll();
			w.assign(t);
		}
	}

	public boolean hasTask(){
		if(taskQueue.size() > 0){
			return true;
		}
		return false;
	}

	/* **************************************************************************************************************** */
	/*                                                 Synchronized methods                                             */
	/* **************************************************************************************************************** */

	protected synchronized void workerFinished(Worker worker){
		workerQueue.offer(worker);
		if(taskQueue.size() > 0){
			assignTasks();
		}
	}

}
