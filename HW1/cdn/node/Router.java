package cdn.node;

import java.util.*;
import java.io.*;
import java.net.*;

import cdn.communications.Link;
import cdn.communications.RouterReceiveThread;
import cdn.wireformats.RouterInfo;
import cdn.wireformats.ChatMessage;

//TODO take out wire fromats from Router and Link, then figure out how you wnat to lable links
//Once the current functionality works like that go ahead and make the wire formats, also look at the wire format facotry. 

public class Router {
	
	private String ID;
	private String hostname;
	private int port;
	private ServerSocket servSock;
	private HashMap<String,Link> links = new HashMap<String,Link>();

	//TODO Take this out, this is testing milestone 1 ONLY!!!!!!!
	private String key;
	private RouterInfo info;
	private boolean gotRI = false;

	public Router(String id, String hn, int p){
		ID = id;
		hostname = hn;
		port = p;
		try{
			servSock = new ServerSocket(port);
		} catch (IOException e){}
	}

	//TODO TAKE THIS OUT this is testing for milestone 1
	public String getKey(){
		return key;
	}
	
	//This method, and everything above have been updated to the new design
	//Still need to test this, ret val subject to change
	//Need to get Message up and running first
	public boolean acceptConnections(){
		try{
			Socket sock = servSock.accept();
			Link l = new Link(sock);
			RouterReceiveThread reader = new RouterReceiveThread(this, l);
			reader.start();
			while(!gotRI){}
			key = info.getID();
			links.put(key, l);
			System.out.println("Connected to " + key);
			return true;
		} catch (Exception e){
			System.out.println("Router::acceptConnections: port already in use");
		}
		return false;
	}
	
	public void initalizeConnection(int servPort, String servID){
		try{
			Link l = new Link(new Socket("localhost", servPort));
			RouterInfo myInfo = new RouterInfo(ID, hostname, port);
			l.sendData(myInfo);
			links.put(servID, l);
			key = servID;
			System.out.println("Connected to " + servID);
		} catch (Exception e) {
			System.out.println("Router::initilizeConnection: something broke");
		}
	}
	
	//TODO reamp for new design
	public void sendMessage(String msg, String serv){
		//TODO get Router send and receive working for chat application, then start adding in wireformats and the discovery node
		ChatMessage message = new ChatMessage(msg);
		Link l = links.get(serv);
		l.sendData(message);
	}

	public void gotRouterInfo(RouterInfo i){
		info = i;
		System.out.println(info);
		gotRI = true;
	}
	
	public void gotChatMessage(ChatMessage msg){
		System.out.println(msg.getPayload());
	}
	
		
	//This main makes me want to vomit. Might as well as write the real main now anyways
	public static void main(String args[]){
		Scanner in = new Scanner(args[1]);
		Router router = new Router(args[0], "localhost", in.nextInt());
		if(args.length == 2){
			router.acceptConnections();
		} else {
			in = new Scanner(args[3]);
			router.initalizeConnection(in.nextInt(), args[2]);
		
		}
		//ATTEMPT AT COMMAND LINE READING lolz
		in = new Scanner(System.in);
		while(in.hasNextLine()){
			router.sendMessage(in.nextLine(), router.getKey());
		}

	}
}
