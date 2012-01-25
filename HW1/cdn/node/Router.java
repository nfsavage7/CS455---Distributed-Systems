package cdn.node;

import java.util.*;
import java.io.*;
import java.net.*;

//TODO Routers wait for connections, so I need to make them initate connections with eachother.
//Presently, I'm thinking to use command line args to help with this, but there might be a better way.
//May the force be with you!

public class Router {

	private int portnum;
	private ArrayList<Socket> peers;
	
	public Router(int pn){
		portnum = pn;
		peers = new ArrayList<Socket>();
	}
	
	public boolean acceptConnections(){
		try{
			ServerSocket serverSock = new ServerSocket(portnum);
			peers.add(serverSock.accept());
			return true;
		} catch (Exception e){
			System.out.println("Router::acceptConnections: port already in use");
		}
		return false;
	}

	public void initaliazeConnection(int servPort){
		try{
			peers.add(new Socket("localhost", servPort));
		} catch (Exception e) {
			System.out.println("Router::initilizeConnection: something broke");
		}
	}

	public boolean sendMessage(String msg){
		int endsize = 0, startsize = 0;
		try{
			DataOutputStream dout = new DataOutputStream(peers.get(0).getOutputStream());
			startsize = dout.size();
			byte[] bytesToSend =  msg.getBytes();
			dout.writeBytes(msg);
			endsize = dout.size();
			dout.flush();
		} catch (IOException e){
			System.out.println("Router::sendMessage: failed to write to output stream");
			return false;
		}
		if(endsize-startsize == msg.length()){
			return true;
		}
		return false;
	}

	public boolean recvMessage(){
		byte[] msg;
		try{
			msg = new byte[peers.get(0).getInputStream().available()];
			DataInputStream din = new DataInputStream(peers.get(0).getInputStream());
			din.readFully(msg);
		} catch (IOException e){
			System.out.println("Router::recvMessage: failed to read from input stream");
			return false;
		}
		String recvMsg = new String(msg);
		return true;

	}
	
	public static void main(String args[]){
		/*if(args.length != 2){
			System.out.println("USAGE: java cdn.node.Router <portnum> <msg>");
		}*/
		Scanner in = new Scanner(args[0]);
		int portnum = in.nextInt();
		Router router = new Router(portnum);
		if(args.length == 2){
			router.acceptConnections();
			router.sendMessage(args[1]);
			while(true){
				router.recvMessage();
			}
		} else {
			in = new Scanner(args[2]);
			int servPort = in.nextInt();
			router.initaliazeConnection(servPort);
			router.sendMessage(args[1]);
			router.recvMessage();
		}

	}
}
