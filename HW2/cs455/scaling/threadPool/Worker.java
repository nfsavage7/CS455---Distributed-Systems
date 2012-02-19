package cs455.scaling.threadPool;

/* java imports */

/* local imports */ 
import cs455.scaling.tasks.*;

/* ************************************************************************************************************************ */
/*                                                      Worker Thread                                                       */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/*															    */
/* 	This is the Worker class. It is a member of the thread pool and completes the task assigned to it by the Thread     */
/* Pool Manager.													    */
/* ************************************************************************************************************************ */

public class Worker extends Thread{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	
	private ThreadPoolManager manager;
	private Task task;
	private boolean newTask = false;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	protected Worker(ThreadPoolManager m){
		manager = m;
		start();
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	protected synchronized void assign(Task t){
		task = t;
		newTask = true;
	}
	
	protected synchronized boolean haveNewTask(){
		return newTask;
	}
	
	protected synchronized void done(){
		newTask = false;
		manager.workerFinished(this);
	}

	public void run(){
		while(true){
			if(haveNewTask()){
				System.out.println(task);
				try{
					Thread.sleep(100);
				} catch (Exception e){}
				done();
			}
		}
	}
		

}
