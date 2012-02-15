package cdn.node;

/* Java imports */
import java.util.*;
import java.io.*;
import java.net.*;

/* Local imports */
import cdn.wireformats.*;
import cdn.communications.Link;
import cdn.communications.ConnectionAccepterThread;
import cdn.mst.MST;

/* ************************************************************************************************************************ */
/*                                                          Router node class                                               */
/*                                                         -------------------                                              */
/* 	This is the router class. It is in charge of managing it's own MST and routing packets around the CDN.              */
/* ************************************************************************************************************************ */


public class Router extends Server{
	
	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private String ID;
	private String IP;
	private String hostname;
	private int port;
	private Link discovery;
	private ArrayList<Link> links = new ArrayList<Link>();
	private ArrayList<Message> messages = new ArrayList<Message>();
	private ArrayList<Link> linksForMessages = new ArrayList<Link>();

	/* **************************************************************************************************************** */
	/*                                        Constructors and other inital methods                                     */
	/* **************************************************************************************************************** */

	public Router(String id, String hn, int p, String discoveryHN, int discoveryPort){
		ID = id;
		hostname = hn;
		port = p;
		try{
			ServerSocket serv = new ServerSocket(port);
			IP = serv.getInetAddress().toString();
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(serv, this);
		} catch (IOException e){}
		regWithDiscovery(discoveryHN, discoveryPort);
	}

	public void regWithDiscovery(String hn, int p){
		try{
			discovery = new Link(new Socket(hn, p), this);
		}catch(Exception e) {e.printStackTrace();}
		links.add(discovery);
		RegisterRequest request = new RegisterRequest(IP, port, ID);
		discovery.sendData(request);
	}

	/* **************************************************************************************************************** */
	/*                                                Getter and Setter Methods                                         */
	/* **************************************************************************************************************** */

	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
	}
	
	/* **************************************************************************************************************** */
	/*                                                Message handling methods                                          */
	/* **************************************************************************************************************** */

	/* This messages recieves the messages and handles them */
	public void acceptMsg(byte[] msg, Link l){
		int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
		synchronized(messages){
			switch(type){
				case Message.ROUTER_INFO:
					messages.add(new RouterInfo(msg));
					linksForMessages.add(l);
					break;
				case Message.CHAT:
					messages.add(new ChatMessage(msg));
					linksForMessages.add(l);
					break;
				case Message.REGISTER_RESPONSE:
					messages.add(new RegisterResponse(msg));
					linksForMessages.add(l);
					break;
				case Message.PEER_ROUTER_LIST:
					messages.add(new PeerRouterList(msg));
					linksForMessages.add(l);
					break;
				case Message.LINK_WEIGHT_UPDATE:	
					System.out.println("Yeah, got the right type...");
					messages.add(new LinkWeightUpdate(msg));
					linksForMessages.add(l);
					break;
				default:
					System.out.println("Router::acceptMsg: Message type unsupported.");
					break;
			}
		}
		handleMessages();
	}

	public void handleMessages(){
		synchronized(messages){
			for(int i = 0; i < messages.size(); i++){
				int type = messages.get(i).getType();
				switch(type){
					case Message.ROUTER_INFO:
						gotRouterInfo(((RouterInfo)(messages.get(i))), linksForMessages.get(i));
						break;
					case Message.CHAT:
						gotChatMessage((ChatMessage)(messages.get(i)));
						break;
					case Message.REGISTER_RESPONSE:
						gotRegisterResponse((RegisterResponse)(messages.get(i)));
						break;
					case Message.PEER_ROUTER_LIST:
						connectToPeers((PeerRouterList)(messages.get(i)));
						break;
					case Message.LINK_WEIGHT_UPDATE:
						updateLinkWeights((LinkWeightUpdate)(messages.get(i)));
						break;
					default:
						break;
				}
			}
			messages = new ArrayList<Message>();
			linksForMessages = new ArrayList<Link>();
		}
	}
	public void gotRouterInfo(RouterInfo i, Link l){
		System.out.println("Connected to Router " + i.getID());
		l.setID(i.getID());
		//Honestly, I'm not sure I need this...
	}
	
	//TODO take this out once I have real data
	public void gotChatMessage(ChatMessage msg){
		System.out.println(msg.getPayload());
	}
	
	public void gotRegisterResponse(RegisterResponse msg){
		System.out.println(msg.getInfo());
	}

	public void connectToPeers(PeerRouterList msg){
		ArrayList<RouterInfo> peers = msg.getPeers();
		for(int i = 0; i < peers.size(); i++){
			RouterInfo peer = peers.get(i);
			initalizeConnection(peer.getHostName(), peer.getPort(), peer.getID());
		}
	}

	//TODO try to streamline this and regWithDiscovery
	public void initalizeConnection(String host, int servPort, String servID){
		try{
			Link l = new Link(new Socket(host, servPort), this);
			l.setID(servID);
			addLink(l);
			RouterInfo myInfo = new RouterInfo(ID, hostname, port);
			l.sendData(myInfo);
			System.out.println("Connected to " + servID);
		} catch (Exception e) {
			System.out.println("Router::initilizeConnection: something broke");
		}
	}
	
	public void updateLinkWeights(LinkWeightUpdate msg){	
		System.out.println("Router::LinkWeightUpdate: Msg received, now implement this");
		MST mst = new MST(msg, links.size()-1, ID);
	}

	/* **************************************************************************************************************** */
	/*                                                Command handling methods                                          */
	/* **************************************************************************************************************** */

	public void deregister(){
		discovery.sendData(new DeregisterRequest(IP, port, ID));
	}

	//TODO take me out
	public void flood(ChatMessage msg){
	//	System.out.println("My links: " + links.size());
		for(int i = 1; i < links.size(); i++){
			System.out.println("Sending to  " + links.get(i).getID());
			links.get(i).sendData(msg);
		}
	}

	/* **************************************************************************************************************** */
	/*                                                Main                                                              */
	/* **************************************************************************************************************** */

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
