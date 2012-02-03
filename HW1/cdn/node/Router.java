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
	private String hostname;
	private int port;
	private Link discovery;
	private ArrayList<Link> links = new ArrayList<Link>();

	/* Constructors  and other inital methods */
	public Router(String id, String hn, int p, String discoveryHN, int discoveryPort){
		ID = id;
		hostname = hn;
		port = p;
		try{
			ConnectionAccepterThread accepter = new ConnectionAccepterThread(new ServerSocket(port), this);
			accepter.start();
		} catch (IOException e){}
		reqWithDiscovery(discoveryHN, discoveryPort);
	}

	public void regWithDiscovery(String hn, int p){
		try{
			discovery = new Link(new Socket(hn, p));
		}catch(Exception e) {e.printStackTrace();}
		links.add(discovery);
		RouterReceiveThread reader = new RouterReceiveThread(this, links);
		reader.start();
		String IP = servSock.getInetAddress().toString();
		RegisterRequest request = new RegisterRequest(IP, port, ID);
		discovery.sendData(request);
	}

	/* THese are all of the getter and setter methods */
	public ArrayList<Link> getLinks(){
		return links;
	}

	public void addLink(Link l){
		links.add(l);
	}
	
	public void initalizeConnection(int servPort, String servID){
		try{
			Link l = new Link(new Socket("localhost", servPort));
			links.add(l);
			RouterReceiveThread reader = new RouterReceiveThread(this, links);
			reader.start();
			RouterInfo myInfo = new RouterInfo(ID, hostname, port);
			l.sendData(myInfo);
			key = servID;
			System.out.println("Connected to " + servID);
		} catch (Exception e) {
			System.out.println("Router::initilizeConnection: something broke");
		}
	}



	/* Message handling methods */	
	//TODO see below todo lolz
	public void gotRouterInfo(RouterInfo i){
		info = i;
		key = i.getID();
	}
	
	//TODO take this out once I have real data
	public void gotChatMessage(ChatMessage msg){
		System.out.println(msg.getPayload());
	}
	
	public void gotRegisterResponse(RegisterResponse msg){
		System.out.println(msg.getInfo());
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
		}

	}
}
