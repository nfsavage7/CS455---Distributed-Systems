package cdn.node;

/* Java imports */
import java.util.*;
import java.net.*;
import java.io.*;

/* Local imports */
import cdn.communications.*;
import cdn.wireformats.*;

public class Discovery {

	/* Member vaiables */
	private int port;
	private ArrayList<Link> links = new ArrayList<Link>();
	private ArrayList<RouterInfo> routers = new ArrayList<RouterInfo>();
	private HashMap<String, ArrayList<RouterInfo>> connections = new HashMap<String, ArrayList<RouterInfo>>();
	
	/* Constructors */
	public Discovery(int p){
		port = p;
		try{
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(new ServerSocket(port), this);
			accepter.start();
		} catch (IOException e){
			System.out.println("Discovery:failed to bind to server socket");
		}
		DiscoveryReceiverThread listener = new DiscoveryReceiverThread(this, links);
		listener.start();
	}
	
	/* Getter and setter methods */
	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
	}


	/* These methods handle messages over the wire */
	public void registerRouter(RegisterRequest request, Link l){
		/* Store the router's info */
		RouterInfo info = new RouterInfo(request.getID(), l.getHostname(), request.getPort());
		routers.add(info);
		System.out.println("Router " + routers.get(routers.size()-1).getID() + " is now registered.");
		l.setID(request.getID());

		/* Now reply */
		byte status = 0; // sucess!
		String information = "Registration request sucessful. The number of router currently constituting the CDN is (" + routers.size() + ")";
		RegisterResponse response = new RegisterResponse(status, information);
		l.sendData(response);
	}

	public void deregisterRouter(DeregisterRequest request, Link l){
		/* remove the router's info from the list */
		int index = 0;
		for(int i = 0; i < routers.size(); i++){
			if(routers.get(i).getID().equals(request.getID())){
				routers.remove(i);
				index = i;
				break;
			}
		}
		/* remove the active link */
		links.get(index).close();
		links.remove(index);
	}

	/* These methods handle command line messages */
	public void printRouterInfo(){
		for(int i = 0; i < routers.size(); i++){
			System.out.println(routers.get(i));
		}
	}

	//TODO check for islands somehow!
	public void setupCDN(){
		for(int i = 0; i < links.size(); i++){
			Random rand = new Random();
			ArrayList<RouterInfo> peers = new ArrayList<RouterInfo>();
			Link l = links.get(i);
			if(connections.containsKey(l.getID())){
				peers = connections.get(l.getID());
				if(peers.size() == 2){
					continue;
				}
			}
			while(peers.size() != 2){
				int router = rand.nextInt(links.size());
				//if peer is not already a peer and I am not peer
				if(!peers.contains(routers.get(router)) && !routers.get(router).getID().equals(l.getID())){
					ArrayList<RouterInfo> hisPeers = new ArrayList<RouterInfo>();
					if(connections.containsKey(l.getID())){
						hisPeers = connections.get(routers.get(router).getID());
						if(hisPeers.size() == 2){
							continue;
						}
					}
					peers.add(routers.get(router));
					hisPeers.add(routers.get(i));
					connections.put(routers.get(router).getID(), hisPeers);
				}
			}
			System.out.println("Connections for router " + l.getID());
			connections.put(l.getID(), peers);
			/* Contact the Routers */
			PeerRouterList list = new PeerRouterList(peers);
			for(int j = 0; j < peers.size(); j ++){
				System.out.print( peers.get(j).getID() + " " );
			}
			System.out.print("\n");
			l.sendData(list);
		}

	}

	/* The main thread handles command line messages */
	public static void main(String args[]){
		Scanner in = new Scanner(args[0]);
		Discovery discovery = new Discovery(in.nextInt());

		in = new Scanner(System.in);
		while(in.hasNextLine()){
			String cmd = in.nextLine();
			if(cmd.equals("list-routers")){
				discovery.printRouterInfo();
			} else if (cmd.equals("setup-cdn")){
				System.out.println("Working on it boss");
				discovery.setupCDN();
				System.out.println("Done with the CDN, Sir");
			} else {
				System.out.println("Command unrecognized");
			}
		}
	
	}
}
