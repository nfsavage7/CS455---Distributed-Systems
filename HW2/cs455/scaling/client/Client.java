/* pacakge statement */
package cs455.scaling.client;

/* java imports */
import java.util.Scanner;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Set;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;

/* local imports */ 
import cs455.scaling.util.RandomData;

/* ************************************************************************************************************************ */
/*                                                          Client                                                          */
/*                                                        ----------                                                        */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Client class. It sends data packets to the server to be serviced at the rate sepecifed on the command   */
/*  line                                                                                                                    */
/* ************************************************************************************************************************ */

public class Client{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private SocketChannel server;
	private int rate;
	private Selector selector;
	private LinkedList<Message> pendingHashes = new LinkedList<Message>();

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Client(String host, int port, int r){
		try{
			InetSocketAddress servIP = new InetSocketAddress(host, port);
			System.out.println("Client at " + servIP.socket().getRemoteSocketAddress().getHostName() + " running");
			server = SocketChannel.open(servIP);
			server.configureBlocking(false);
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_READ);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Client:: Failed to connect to server");
			System.exit(1);
		}
		rate = r;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */
	
	public void sendMsg(){
		TransmitionHandler handler = new TransmitionHandler(this, server, rate);
		handler.start();
	}

	private int bytesToInt(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocateDirect(4);
		for(int i = 0; i < bytes.length; i++){
			buffer.put(bytes[i]);
		}
		return buffer.getInt(0);
	}
	
	public synchronized void addMessage(Message msg){
		pendingHashes.offer(msg);
	}

	public void listen(){
		while(true){
			try{
				selector.selectNow();
			} catch (Exception e){
				System.out.println("Something is wrong with listen Boss");
			}
			Set keys = selector.selectedKeys();
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey) iter.next();
				iter.remove();
				if(key.isReadable()){
					try{
						ByteBuffer buff = ByteBuffer.allocate(4);
						int bytes = server.read(buff);
						int bytesToRead = bytesToInt(buff.array());
						buff = ByteBuffer.allocate(bytesToRead);
						bytes = server.read(buff);
						Message msg = getMessage(new String(buff.array()));
						System.out.println("[Msg-" + msg.getID() + "] Reveived: " + msg);
						if(pendingHashes.remove(msg)){
							System.out.println("[ClientStatus] Message " + msg.getID() + " transmitted correctly");
						} else {
							System.out.println("[ClientStatus] Unrecognized hash");
						}
						System.out.println();
					} catch (Exception e){
						e.printStackTrace();
						System.out.println("Read isn't workign right Boss");
					}
				}
			}
		}
	}
	
	private synchronized Message getMessage(String hash){
		for(Message msg : pendingHashes){
			if(msg.getHash().equals(hash)){
				return msg;
			}
		}
		return null;
}

	/* **************************************************************************************************************** */
	/*                                                     Main                                                         */
	/* **************************************************************************************************************** */

	public static void main (String[] args){
		/* USAGE */
		if(args.length != 3){
			System.out.println("usage: java cs455.scaling.client.Client <server-host> <server-port> <message-rate>");
			System.exit(1);
		}
		
		Scanner in = new Scanner(args[1]);
		int port = in.nextInt();
		in = new Scanner(args[2]);
		int rate = in.nextInt();
		Client client = new Client(args[0], port, rate);
		client.sendMsg();
		client.listen();
	}
}
