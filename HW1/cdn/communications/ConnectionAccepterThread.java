package cdn.communications;

import java.net.*;
import java.io.*;

import cdn.node.Discovery;

public class ConnectionAccepterThread extends Thread{

	private ServerSocket servSock;
	private Discovery discovery;
	private int maxConnections = -1;

	public ConnectionAccepterThread(ServerSocket s, Discovery d){
		servSock = s;
		discovery = d;
	}
	
	public ConnectionAccepterThread(ServerSocket s, int mc){
		servSock = s;
		maxConnections = mc;
	}
	
	public void run(){
		int connections = 0;
		while(true){
			if(maxConnections != -1){
				if(connections == maxConnections){
					break;
				}
			}
			try{
				Link l = new Link(servSock.accept());
				discovery.addLink(l);
			} catch (IOException e){
				System.out.println("ConnectionAccepterThread::run: could not open socket.");
			}
		}
	}
}
