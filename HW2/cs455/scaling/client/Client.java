/* pacakge statement */
package cs455.scaling.client;

/* java imports */
import java.util.Scanner;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.Set;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.lang.StringBuffer;

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
	private long time;
	private long span = 1000000000;
	private Selector selector;
	private LinkedList<Message> pendingHashes = new LinkedList<Message>();
	private boolean finish = false;
	private StringBuffer print;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Client(String host, int port, int r){
		try{
			time = System.nanoTime();
			print = new StringBuffer();
			InetSocketAddress servIP = new InetSocketAddress(host, port);
			server = SocketChannel.open(servIP);
			server.configureBlocking(false);
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_READ);
			System.out.println("Client at " + InetAddress.getLocalHost().getHostName() + " running");
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

	public synchronized void finish(){
		finish = true;
	}
	
	public synchronized boolean isFinished(){
		return finish;
	}
	
	public synchronized void print(String str){
		print.append(str +"\n");
	}

	public void listen(){
		while(!isFinished()){
			if(System.nanoTime() - time >= span*60){
				time = System.nanoTime();
				synchronized(print){
					System.out.println(print);
					print = new StringBuffer();
				}
			}
			try{
				selector.selectNow();
			} catch (Exception e){}
			Set keys = selector.selectedKeys();
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey) iter.next();
				iter.remove();
				synchronized(pendingHashes){
				 	if(key.isReadable() && pendingHashes.size() > 0){
						try{
							synchronized(server){
								ByteBuffer buff = ByteBuffer.allocate(4);
								int bytes = server.read(buff);
								int bytesToRead = bytesToInt(buff.array());
								buff = ByteBuffer.allocate(bytesToRead);
								while(buff.hasRemaining()){
									bytes = server.read(buff);
								}
								Message msg = getMessage(new String(buff.array()));
								print("[Msg-" + msg.getID() + "] Received: " + msg);
									if(pendingHashes.remove(msg)){
										print("[ClientStatus] Message " + msg.getID() + " transmitted correctly");
									} else {
										print("[ClientStatus] Unrecognized hash");
									}
							}
							print("");
						} catch (Exception e){}
					}
				}
			}
		}
		Ssytem.out.println(print);
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
