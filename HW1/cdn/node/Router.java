package cdn.node;

/* Java imports */
import java.util.*;
import java.io.*;
import java.net.*;

/* Local imports */
import cdn.communications.Link;
import cdn.communications.RouterReceiveThread;
import cdn.communications.ConnectionAccepterThread;
import cdn.wireformats.*;


public class Router {
	
	/* Member variables */
	private String ID;
	private String IP;
	private String hostname;
	private int port;
	private Link discovery;
	private ArrayList<Link> links = new ArrayList<Link>();
	private	RouterReceiveThread reader;

	/* Constructors  and other inital methods */
	public Router(String id, String hn, int p, String discoveryHN, int discoveryPort){
		ID = id;
		hostname = hn;
		port = p;
		try{
			ServerSocket serv = new ServerSocket(port);
			IP = serv.getInetAddress().toString();
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(serv, this);
			accepter.start();
		} catch (IOException e){}
		regWithDiscovery(discoveryHN, discoveryPort);
	}

	public void regWithDiscovery(String hn, int p){
		try{
			discovery = new Link(new Socket(hn, p));
		}catch(Exception e) {e.printStackTrace();}
		links.add(discovery);
		reader = new RouterReceiveThread(this, links);
		RegisterRequest request = new RegisterRequest(IP, port, ID);
		discovery.sendData(request);
	}

	/* THese are all of the getter and setter methods */
	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
		System.out.println("Got new link. " + links.size());
	}
	
	//TODO try to streamline this and regWithDiscovery
	public void initalizeConnection(String host, int servPort, String servID){
		try{
			Link l = new Link(new Socket(host, servPort));
			l.setID(servID);
			addLink(l);
			System.out.println("Here Boss");
		//	reader = new RouterReceiveThread(this, links);
			RouterInfo myInfo = new RouterInfo(ID, hostname, port);
			l.sendData(myInfo);
			System.out.println("Connected to " + servID);
		} catch (Exception e) {
			System.out.println("Router::initilizeConnection: something broke");
		}
	}

	public void deregister(){
		discovery.sendData(new DeregisterRequest(IP, port, ID));
	}

	/* Message handling methods */	
	public void gotRouterInfo(RouterInfo i, Link l){
		//TODO Right here Boss!
		System.out.println("Connected to Router " + i.getID());
		l.setID(i.getID());
		//Honestly, I'm not sure I need this...
	}
	
	//TODO take me out
	public void flood(ChatMessage msg){
		System.out.println("My links: " + links.size());
		for(int i = 1; i < links.size(); i++){
			System.out.println("Sending to  " + links.get(i).getID());
			links.get(i).sendData(msg);
		}
	}
	
	//TODO take this out once I have real data
	public void gotChatMessage(ChatMessage msg){
		System.out.println(msg.getPayload());
	}
	
	public void gotRegisterResponse(RegisterResponse msg){
		System.out.println(msg.getInfo());
	}

	public void connectToPeers(PeerRouterList msg){
		links = new ArrayList<Link>();
		links.add(discovery);
		ArrayList<RouterInfo> peers = msg.getPeers();
		for(int i = 0; i < peers.size(); i++){
			RouterInfo peer = peers.get(i);
			initalizeConnection(peer.getHostName(), peer.getPort(), peer.getID());
		}
	}

	/* The main thread hands command line messages */
	public static void main(String args[]){
		Scanner in = new Scanner(args[1]);
		int port = in.nextInt();
		in = new Scanner(args[3]);
		int discoveryPort = in.nextInt();
		Router router = new Router(args[0], "localhost", port, args[2], discoveryPort);
		
		in = new Scanner(System.in);
		while(in.hasNextLine()){
			//TODO add in router commands
			String cmd = in.nextLine();
			if(cmd.equals("exit-cdn")){
				router.deregister();
			} else {
				router.flood(new ChatMessage(cmd));
			}
		}

	}
}
