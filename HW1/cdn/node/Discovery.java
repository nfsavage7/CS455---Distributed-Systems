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
	private DiscoveryReceiverThread listener;
	private HashMap<String, ArrayList<RouterInfo>> cdn;
	
	/* Constructors */
	public Discovery(int p){
		port = p;
		try{
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(new ServerSocket(port), this);
			accepter.start();

		} catch (Exception e){
			System.out.println("Discovery:failed to bind to server socket");
		}
		listener = new DiscoveryReceiverThread(this, links);
	}
	
	/* Getter and setter methods */
	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
	}
	
/*	public void makeNewListener(){
		listener = new DiscoveryReceiverThread(this, links);
	}

*/	/* These methods handle messages over the wire */
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
	
	public void close(){
		for(int i = 0; i < links.size(); i++){
			links.get(i).close();
		}
	}

	public void setupCDN(int numConnections){
		boolean done = false;
		HashMap<String, ArrayList<RouterInfo>> connectionsToSend = new HashMap<String, ArrayList<RouterInfo>>() ;
		while(!done){
			//need to add in sending of the peer lists
			connectionsToSend = calculateCDN(numConnections);
			if(connectionsToSend == null){
				continue;
			}
			done = noIslands(cdn);
		}
		sendPeers(connectionsToSend);
		System.out.println("Done with CDN!");
	}

	//TODO check for islands somehow!
	//TODO take out static num of connections = 2
	public HashMap<String, ArrayList<RouterInfo>>  calculateCDN(int numConnections){
		long startTime = System.nanoTime();
		cdn = new HashMap<String, ArrayList<RouterInfo>>();
		HashMap<String, ArrayList<RouterInfo>> connectionsToSend = new HashMap<String, ArrayList<RouterInfo>>();
		for(int i = 0; i < links.size(); i++){
			if(System.nanoTime() - startTime >100000000){
			//	System.out.println("First timeout");
				return null;
			}
			Random rand = new Random();
			ArrayList<RouterInfo> peers = new ArrayList<RouterInfo>();
			Link l = links.get(i);
			//If I already have peers, get my list
			if(cdn.containsKey(l.getID())){
				peers = cdn.get(l.getID());
				if(peers.size() == numConnections){
					System.out.println("Skip");
			//		continue;
				}
			}
			ArrayList<RouterInfo> peersToSend = new ArrayList<RouterInfo>();
			while(peers.size() != numConnections){
				if(System.nanoTime() - startTime >100000000){
			//		System.out.println("Second timeout");
					return null;
				}
				int router = rand.nextInt(links.size());
			//	System.out.println(router);
				//if peer is not already a peer and I am not peer
				if(!peers.contains(routers.get(router)) && !routers.get(router).getID().equals(l.getID())){
					ArrayList<RouterInfo> hisPeers = new ArrayList<RouterInfo>();
					//if peer already has some peers
					if(cdn.containsKey(links.get(router).getID())){
						hisPeers = cdn.get(routers.get(router).getID());
						//If peer is already at Cr connections or we are already peers
					//	System.out.println("His peers: " + hisPeers.size());
						if(hisPeers.size() == numConnections || hisPeers.contains(l.getID())){
							continue;
						}
					}
					peers.add(routers.get(router));
					peersToSend.add(routers.get(router));
					hisPeers.add(routers.get(i));
					cdn.put(routers.get(router).getID(), hisPeers);
				}
			}
			cdn.put(l.getID(), peers);
			connectionsToSend.put(l.getID(), peersToSend);

			/* Contact the Routers */
			/*if(peersToSend.size() > 0 ){
				PeerRouterList list = new PeerRouterList(peersToSend);
				l.sendData(list);
			}*/

			/* debugging the cdn. TODO this is to be taken out */
			System.out.println("Connections for router " + l.getID());
			for(int j = 0; j < peers.size(); j ++){
				System.out.print( peers.get(j).getID() + " " );
			}
			System.out.print("\n");
		}
		System.out.println("Checking for islands");
		return connectionsToSend;
	}

	public boolean noIslands(HashMap<String, ArrayList<RouterInfo>> connections){
		//So, I'm thinking, hashmap of string to bool. pick the first router and his first link, every time you hit a router that isn't in the hashmap, add it. if all of the routers that a router is connected to, then we can check the hashmap to see if we have all of the routers in the hashmap
		HashMap<String, Boolean> connected = new HashMap<String, Boolean>();
		String router = links.get(0).getID();
		boolean done = false;
		connected.put(router, new Boolean(true));
		while(!done){
			ArrayList<RouterInfo> routers = connections.get(router);
			for(int i = 0; i < routers.size(); i++){
				if(!connected.containsKey(routers.get(i).getID())){
					router = routers.get(i).getID();
					connected.put(router, new Boolean(true));
					break;
				}
				if(i == routers.size()-1){
					done = true;
				}
			}
		}
		if(links.size() == connected.keySet().size()){
			return true;
		}
		return false;
	}

	public void sendPeers(HashMap<String, ArrayList<RouterInfo>> connectionsToSend){
	//	System.out.println(connectionsToSend.get(links.get(0).getID()));
		for(int i = 0; i < links.size(); i++){
			if(connectionsToSend.containsKey(links.get(i).getID())){
				System.out.println("Sending peers to router " + links.get(i).getID());
				if(connectionsToSend.get(links.get(i).getID()).size() > 0){
					PeerRouterList list = new PeerRouterList(connectionsToSend.get(links.get(i).getID()));
					links.get(i).sendData(list);
				}
			}
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
				discovery.setupCDN(4);
			} else if (cmd.startsWith("setup-cdn ")){
				Scanner tmp = new Scanner(cmd);
				tmp.useDelimiter(" ");
				tmp.next();
				discovery.setupCDN(tmp.nextInt());
			} else if (cmd.equals("close")){
				discovery.close();
				break;
			} else {
				System.out.println("Command unrecognized");
			}
		}
	
	}
}
