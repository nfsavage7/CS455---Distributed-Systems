package cdn.node;

/* Java imports */
import java.util.*;
import java.net.*;
import java.io.*;

/* Local imports */
import cdn.wireformats.*;
import cdn.communications.Link;
import cdn.communications.ConnectionAccepterThread;
import cdn.mst.MstUpdateThread;

/* ************************************************************************************************************************ */
/*                                                    Discovery node class                                                  */
/*                                                   ----------------------                                                 */
/* 	This class is responsible for managing which routers are connected to which other ones. It is also in charge of     */
/* managing the weights of all of the links.                                                                                */
/* ************************************************************************************************************************ */


public class Discovery extends Server{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int port;								//Port that I am listening on for new connections
	private MstUpdateThread mstThread;						//Timer to update the link weights
	private ArrayList<Link> links = new ArrayList<Link>();				//Links to each router
	private ArrayList<RouterInfo> routers = new ArrayList<RouterInfo>();		//Information about each router
	private HashMap<String, ArrayList<RouterInfo>> cdn;				//The links between each router
	private ArrayList<LinkInfo> uniqueConnections = new ArrayList<LinkInfo>();	//This is a list of all the bidirectional links in the cdn
	private ArrayList<Message> messages = new ArrayList<Message>();			//Queue of incomming messages
	private ArrayList<Link> linksForMessages = new ArrayList<Link>();		//Links that each message came in over
	
	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Discovery(int p){
		port = p;
		try{
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(new ServerSocket(port), this);

		} catch (Exception e){
			System.out.println("Discovery:failed to bind to server socket");
		}
	}
	
	/* **************************************************************************************************************** */
	/*                                               Getter and setter methods                                          */
	/* **************************************************************************************************************** */

	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
	}

	public void removeLink(Link l){
		int index = links.indexOf(l);
		links.remove(index);
		routers.remove(index);
		System.out.println("Lost connection to router " + l.getID() + ". Please re-setup the cdn");
		if(mstThread != null){
			mstThread.setDone();
		}
	}
	
	public RouterInfo getRouterInfo(String id){
		for(int i = 0; i < routers.size(); i++){
			if(routers.get(i).getID().equals(id)){
				return routers.get(i);
			}
		}
		return null;
	}
	
	/* **************************************************************************************************************** */
	/*                                                Message handling methods                                          */
	/* **************************************************************************************************************** */

	/* This method gets the messages and handles them */
	public void acceptMsg(byte[] msg, Link l){
		int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
		switch(type){
			case Message.REGISTER_REQUEST:
				messages.add(new RegisterRequest(msg));
				linksForMessages.add(l);
				break;
			case Message.DEREGISTER_REQUEST:
				messages.add(new DeregisterRequest(msg));
				linksForMessages.add(l);
				break;
			case Message.REMOVE_LINK:
				messages.add(new RemoveLink(msg));
				linksForMessages.add(l);
				break;
			default:
				System.out.println("Message type unsupported");
				break;
		}
		handleMessages();
	}

	public void handleMessages(){
		for(int i = 0; i < messages.size(); i++){
			int type = messages.get(i).getType();
			switch(type){
				case Message.REGISTER_REQUEST:
					registerRouter(((RegisterRequest)messages.get(i)), linksForMessages.get(i));
					break;
				case Message.DEREGISTER_REQUEST:
					deregisterRouter(((DeregisterRequest)messages.get(i)), linksForMessages.get(i));
					break;
				case Message.REMOVE_LINK:
					RemoveLink msg = (RemoveLink)messages.get(i);
					for(int j = 0; j < links.size(); j++){
						if(links.get(j).getID().equals(msg.getID())){
							removeLink(links.get(j));
							break;
						}
					}
					break;
				default:
					break;
			}
		}
		messages = new ArrayList<Message>();
		linksForMessages = new ArrayList<Link>();
	}
	
	public void registerRouter(RegisterRequest request, Link l){
		
		byte status;
		String information;
		
		//Now reply
		boolean idTaken = false;
		for(int i = 0; i < routers.size(); i++){
			if(routers.get(i).getID().equals(request.getID())){
				idTaken = true;
				break;
			}
		}
		if(idTaken){
			status = 1;
			information = "Registration request failed. That router ID is already registered.";
		 } else if (l.getIP().equals(request.getIP())){
			status = 2;
			information = "Registration request failed. Mismatched IP adresses.";
		} else {
		/* Store the router's info */
			RouterInfo info = new RouterInfo(request.getID(), l.getHostname(), request.getPort());
			routers.add(info);
			System.out.println("Router " + routers.get(routers.size()-1).getID() + " is now registered.");
			l.setID(request.getID());

			status = 0; // sucess!
			information = "Registration request sucessful. The number of router currently constituting the CDN is (" + routers.size() + ")";
		}
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

	/* **************************************************************************************************************** */
	/*                                                Command handling methods                                          */
	/* **************************************************************************************************************** */

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

	public void listWeights(){
		for(int i = 0; i < uniqueConnections.size(); i++){
			System.out.println(uniqueConnections.get(i));
		}
	}

	/* **************************************************************************************************************** */
	/*                                           CDN setup and support methods                                          */
	/* **************************************************************************************************************** */

	public void setupCDN(int numConnections, int sleep){
		boolean done = false;
		HashMap<String, ArrayList<RouterInfo>> connectionsToSend = new HashMap<String, ArrayList<RouterInfo>>() ;
		while(!done){
			connectionsToSend = calculateCDN(numConnections);
			if(connectionsToSend == null){
				continue;
			}
			done = noIslands(cdn);
		}
		sendPeers(connectionsToSend);
		updateLinkWeights();
		if(mstThread != null){
			mstThread.setDone();
		}
		mstThread = new MstUpdateThread(sleep, this);
	}

	public HashMap<String, ArrayList<RouterInfo>>  calculateCDN(int numConnections){
		long startTime = System.nanoTime();
		cdn = new HashMap<String, ArrayList<RouterInfo>>();
		HashMap<String, ArrayList<RouterInfo>> connectionsToSend = new HashMap<String, ArrayList<RouterInfo>>();
		for(int i = 0; i < links.size(); i++){
			if(System.nanoTime() - startTime >100000000){
				return null;
			}
			Random rand = new Random();
			ArrayList<RouterInfo> peers = new ArrayList<RouterInfo>();
			Link l = links.get(i);
			//If I already have peers, get my list
			if(cdn.containsKey(l.getID())){
				peers = cdn.get(l.getID());
				if(peers.size() == numConnections){
					continue;
				}
			}
			ArrayList<RouterInfo> peersToSend = new ArrayList<RouterInfo>();
			while(peers.size() != numConnections){
				if(System.nanoTime() - startTime >100000000){
					return null;
				}
				int router = rand.nextInt(links.size());
				//if peer is not already a peer and I am not peer
				if(!peers.contains(routers.get(router)) && !routers.get(router).getID().equals(l.getID())){
					ArrayList<RouterInfo> hisPeers = new ArrayList<RouterInfo>();
					//if peer already has some peers
					if(cdn.containsKey(links.get(router).getID())){
						hisPeers = cdn.get(routers.get(router).getID());
						//If peer is already at Cr connections or we are already peers
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

		}
		return connectionsToSend;
	}

	public boolean noIslands(HashMap<String, ArrayList<RouterInfo>> connections){
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
		for(int i = 0; i < links.size(); i++){
			if(connectionsToSend.containsKey(links.get(i).getID())){
				if(connectionsToSend.get(links.get(i).getID()).size() > 0){
					PeerRouterList list = new PeerRouterList(connectionsToSend.get(links.get(i).getID()));
					links.get(i).sendData(list);
				}
			}
		}
	}
	
	/* **************************************************************************************************************** */
	/*                                           MST setup and support methods                                          */
	/* **************************************************************************************************************** */

	public void updateLinkWeights(){
		uniqueConnections = new ArrayList<LinkInfo>();
		boolean addLink;
		for( int i = 0; i < links.size(); i++){
			ArrayList<RouterInfo> connections = cdn.get(links.get(i).getID());
			for( int k = 0; k < connections.size(); k++){
				addLink = true;
				for(int j = 0; j < uniqueConnections.size(); j++){
					RouterInfo me = uniqueConnections.get(j).hasRouter(routers.get(i));
					RouterInfo peer = uniqueConnections.get(j).hasRouter(connections.get(k));
					if(me != null && peer != null){
						addLink = false;
						break;
					}
				}
				if(addLink){
					//Add random weight
					Random r = new Random();
					int w = 0;
					while(w < 1){
						w = r.nextInt(11);
					}
					uniqueConnections.add(new LinkInfo(getRouterInfo(links.get(i).getID()),getRouterInfo(connections.get(k).getID()), w));
				} 
			}
		}
		sendWeights(new LinkWeightUpdate(uniqueConnections));
		System.out.println("Updated the link weights");
		
	}
	
	private void sendWeights(LinkWeightUpdate msg){
		for(int i = 0; i < links.size(); i++){
			links.get(i).sendData(msg);
		}
	}

	/* **************************************************************************************************************** */
	/*                                                Main                                                              */
	/* **************************************************************************************************************** */

	/* The main thread handles command line messages */
	public static void main(String args[]){
		if(args.length != 1 && args.length != 2){
			System.out.println("Usage: java cdn.node.Discovery <port number> <update interval>");
			System.exit(1);
		}
		
		Scanner in = new Scanner(args[0]);
		int port = in.nextInt();
		int sleep = 120;
		if(args.length == 2){
			in = new Scanner(args[1]);
			sleep = in.nextInt();
		}
		Discovery discovery = new Discovery(port);

		in = new Scanner(System.in);
		while(in.hasNextLine()){
			String cmd = in.nextLine();
			if(cmd.equals("list-routers")){
				discovery.printRouterInfo();
			} else if (cmd.equals("setup-cdn")){
				discovery.setupCDN(4, sleep);
			} else if (cmd.startsWith("setup-cdn ")){
				Scanner tmp = new Scanner(cmd);
				tmp.useDelimiter(" ");
				tmp.next();
				discovery.setupCDN(tmp.nextInt(), sleep);
			} else if (cmd.equals("list-weights")){
				discovery.listWeights();
			} else if (cmd.equals("close")){
				discovery.close();
				break;
			} else if (cmd.equals("help")){
				System.out.println("Valid commands are:");
				System.out.println("	close");
				System.out.println("	help");
				System.out.println("	list-routers");
				System.out.println("	list-weights");
				System.out.println("	setup-cdn");
				System.out.println("	setup-cdn <number of connections per router>");
			} else {
				System.out.println("invalid command. for help, type \"help\"");
			}
		}
	
	}
}
