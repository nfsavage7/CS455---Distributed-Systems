/* pacakge statement */
package cs455.scaling.server;

/* java imports */
import java.util.Scanner;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Set;
import java.lang.StringBuffer;

/* local imports */ 
import cs455.scaling.threadPool.ThreadPoolManager;
import cs455.scaling.tasks.ReadTask;
import cs455.scaling.util.RandomData;
import cs455.scaling.tasks.WriteTask;

/* ************************************************************************************************************************ */
/*                                                           Server                                                         */
/*                                                         ----------                                                       */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Server class. It uses the Thread Pool Manager to complete tasks given to it by the clients              */
/* ************************************************************************************************************************ */

public class Server{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	
	private ServerSocketChannel server;
	private Selector selector;
	private ThreadPoolManager manager;
	private long time;
	private long span = 1000000000;
	private StringBuffer print;
	private int packets = 0, clients = 0;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Server(int port, int numThreads){
		try{
			time = System.nanoTime();
			print = new StringBuffer();
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server at " + InetAddress.getLocalHost().getHostName() + " running");
		} catch (Exception e){
			System.out.println("Server:: failed to bind to socket");
		}
		manager = new ThreadPoolManager(numThreads);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public synchronized Set getKeys(){
		try{
			selector.selectNow();
		} catch (Exception e){}
		return selector.selectedKeys();
	}

	public synchronized void setKeys(SocketChannel sock){
		try{
			sock.configureBlocking(false);
			sock.register(selector, SelectionKey.OP_READ);
		} catch (Exception e){}
	
	}

	public synchronized void deregister(SelectionKey key){
		if(key.isValid()){
			key.cancel();
		}
	}

	public void write(RandomData data, SocketChannel dest){
		WriteTask task = new WriteTask(data, dest);
		manager.addTask(task);
	}
	
	public synchronized void print(String str){
		print.append(str + "\n");
	}

	public void listen(){
		while(true){
			if(System.nanoTime() - time >= span*60){
				time = System.nanoTime();
				synchronized(print){
					System.out.println("Total Clients connected: " + clients);
					System.out.println("Packets/sec: " + packets);
					packets = 0;
					System.out.println(print);
					print = new StringBuffer();
				}
			}

			Set keys = getKeys();
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey)iter.next();
				iter.remove();
				if(key.isValid() && key.isAcceptable()){
					accept();
				}
				if(key.isValid() && key.isReadable()){
					synchronized(key){
						if(key.attachment() == null){
							ReadTask task = new ReadTask(key, this);
							packets++;
							key.attach(task);
							manager.addTask(task);
						}
					}
				}
			}
		}
	}

	public void accept(){
		try{
			SocketChannel client = server.accept();
			setKeys(client);
			clients++;
		} catch (Exception e){
			System.out.println("Could not connect to client");
		}
	}

	/* **************************************************************************************************************** */
	/*                                                     Main                                                         */
	/* **************************************************************************************************************** */
	
	public static void main(String[] args){
		/* USAGE */
		if(args.length != 2){
			System.out.println("usage: java cs455.scaling.server.Server <port-number> <thread-pool-size>");
			System.exit(1);
		}
		Scanner in = new Scanner(args[0]);
		int port = in.nextInt();
		in = new Scanner(args[1]);
		int numThreads = in.nextInt();
		Server server = new Server(port, numThreads);
		server.listen();
	}
}
