/* pacakge statement */
package cs455.scaling.tasks;

/* java imports */
import java.nio.channels.SelectionKey;

/* local imports */ 
import cs455.scaling.server.Server;

/* ************************************************************************************************************************ */
/*                                                   Accept Connection Task                                                 */
/*                                                 --------------------------                                               */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Accept Connection Task. This is generated when the server has an incomming connection.                  */
/* ************************************************************************************************************************ */

public class AcceptConnectionTask extends Task{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private Server server;
	private SelectionKey key;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public AcceptConnectionTask(Server s, SelectionKey k){
		server = s;
		key = k;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public void execute(){
		server.accept(key);
	}
}
