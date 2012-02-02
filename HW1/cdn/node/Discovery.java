package cdn.node;

import java.util.*;
import java.net.*;
import java.io.*;

import cdn.communications.*;
import cdn.wireformats.*;

public class Discovery {

	private int port;
	private ArrayList<Link> links = new ArrayList<Link>();
	private ArrayList<RouterInfo> routers = new ArrayList<RouterInfo>();
	
	public Discovery(int p){
		port = p;
		try{
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(new ServerSocket(port), this);
			accepter.start();
		} catch (IOException e){
			System.out.println("Discovery:failed to bind to server socket");
		}
		DiscoveryReceiverThread listener = new DiscoveryReceiverThread(links, this);
		listener.start();
	}

	public ArrayList<Link> getLinks(){
		return links;
	}

	//TODO Might have to add link to routers as well *shrug* 
	public void addLink(Link l){
		links.add(l);
	}

/*
	public Link getLink(String RouterID){
			
	}
*/

	/* These methods handle messages over the wire */
	public void registerRouter(RegisterRequest request, Link l){
		RouterInfo info = new RouterInfo(request.getID(), l.getHostname(), request.getPort());
		routers.add(info);
		System.out.println("Router " + routers.get(routers.size()-1).getID() + " is now registered.");

		/* Now reply */
		byte status = 0; // sucess!
		String information = "Registration request sucessful. The number of router currently constituting the CDN is (" + routers.size() + ")";
		RegisterResponse response = new RegisterResponse(status, information);
		l.sendData(response);
	}

	/* These methods handle command line messages */
	public void printRouterInfo(){
		System.out.println(routers.size());
		for(int i = 0; i < routers.size(); i++){
			System.out.println(routers.get(i));
		}
	}

	public static void main(String args[]){
		Scanner in = new Scanner(args[0]);
		Discovery discovery = new Discovery(in.nextInt());

		in = new Scanner(System.in);
		while(in.hasNextLine()){
			if(in.nextLine().equals("list-routers")){
				discovery.printRouterInfo();
			} else {
				System.out.println("Command unrecognized");
			}
		}
	
	}
}
