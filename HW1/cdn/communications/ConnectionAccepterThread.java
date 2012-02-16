package cdn.communications;

/* Java imports */
import java.net.*;
import java.io.*;

/* Local imports */
import cdn.node.*;

/* ************************************************************************************************************************ */
/*                                                  ConnectionAccepterThread class                                          */
/*				                   --------------------------------                                         */
/* 	This is the Connection Accepter Thread. It listens for incomming connects and passes the connection to it's         */
/* specified server node.                                                                                                   */
/* ************************************************************************************************************************ */

public class ConnectionAccepterThread extends Thread{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private ServerSocket servSock;
	private Server server;

	/* **************************************************************************************************************** */
	/*                                            Constructors and inital methods                                       */
	/* **************************************************************************************************************** */

	public ConnectionAccepterThread(ServerSocket sock, Server s){
		servSock = sock;
		server = s;
		start();
	}
	
	/* **************************************************************************************************************** */
	/*                                                  Accept Method                                                   */
	/* **************************************************************************************************************** */
	public void run(){
		while(true){
			try{
				Link l = new Link(servSock.accept(), server);
				server.addLink(l);
			} catch (IOException e){
				System.out.println("ConnectionAccepterThread::run: could not open socket.");
			}
		}


	}
}
