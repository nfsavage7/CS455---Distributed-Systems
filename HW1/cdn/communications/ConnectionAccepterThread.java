package cdn.communications;

/* Java imports */
import java.net.*;
import java.io.*;

/* Local imports */
import cdn.node.*;

public class ConnectionAccepterThread extends Thread{

	/* Member variables */
	private ServerSocket servSock;
	private Object server;
	private int maxConnections = -1;

	/* Constructors */

	//To be called by Discovery
	public ConnectionAccepterThread(ServerSocket sock, Object s){
		servSock = sock;
		server = s;
	}
	
	//To be called by Router
	public ConnectionAccepterThread(ServerSocket sock, Object s, int mc){
		servSock = sock;
		server = s;
		maxConnections = mc;
	}

	/* Determine who is listening for connections, then accept them accordingly */
	public void run(){
		if(server instanceof Discovery){
			discoveryRun();
		} else {
			routerRun();
		}
	}
	
	public void discoveryRun(){
		Discovery discovery = (Discovery)server;
		while(true){
			try{
				Link l = new Link(servSock.accept());
				System.out.println("Got the connection");
				discovery.addLink(l);
			} catch (IOException e){
				System.out.println("ConnectionAccepterThread::discoveryRun: could not open socket.");
			}
		}
	}

	public void routerRun(){
		int connections = 0;
		Router router = (Router)server;
		while (true) {
			//TODO take out once this functionality is fully implemented
			if(maxConnections != -1){
				if(connections == maxConnections){
					break;
				}
			}
			try{
				Link l = new Link(servSock.accept());
				router.addLink(l);
				connections++;
			} catch (IOException e) {
				System.out.println("ConnectionAcceptThread::routerRun: could not open socket.");
			}
		}
	}
}
