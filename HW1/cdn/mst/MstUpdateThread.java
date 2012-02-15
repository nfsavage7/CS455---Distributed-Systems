package cdn.mst;

/* local imports */
import cdn.node.Discovery;

/* ************************************************************************************************************************ */
/*                                                 	    MstUpdateThread                                                 */
/*                                                         -----------------                                                */
/* 	This is the MST Update Thread. It is in charge up periodically updatign the MST.                                    */
/* ************************************************************************************************************************ */

public class MstUpdateThread extends Thread{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */
	
	private int sleepTime;		//This is in ms
	private Discovery discovery;
	private boolean done = false;

	/* **************************************************************************************************************** */
	/*                                          Constructors and inital methods                                         */
	/* **************************************************************************************************************** */

	public MstUpdateThread(int s, Discovery d){
		super("MST Update Thread");
		sleepTime = s*1000;
		discovery = d;
		start();
	}

	synchronized public  boolean getDone(){
		return done;
	}
	
	synchronized public void setDone(){
		done = true;
	}

	/* **************************************************************************************************************** */
	/*                                                 Update method                                                    */
	/* **************************************************************************************************************** */

	public void run(){
		while(!getDone()){
			try{
				Thread.sleep(sleepTime);
				discovery.updateLinkWeights();
			} catch (InterruptedException e){}
		}
	}
}
