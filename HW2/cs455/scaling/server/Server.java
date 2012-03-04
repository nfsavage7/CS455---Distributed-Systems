/* pacakge statement */
package cs455.scaling.server;

/* java imports */
import java.util.Scanner;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Set;

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

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Server(int port, int numThreads){
		try{
			server = ServerSocketChannel.open();
			server.configureBlocking(false);
			server.socket().bind(new InetSocketAddress(port));
			selector = Selector.open();
			server.register(selector, SelectionKey.OP_ACCEPT);
		} catch (Exception e){
			System.out.println("Server:: Something broke");
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
		System.out.println("Got here");
		try{
			sock.configureBlocking(false);
			System.out.println("Here");
			sock.register(selector, SelectionKey.OP_READ);
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Still not right Boss");
		}
	}

	public synchronized void deregister(SelectionKey key){
		if(key.isValid()){
			key.cancel();
		}
		System.out.println("Deregister");
	}

	public void write(RandomData data, SocketChannel dest){
		WriteTask task = new WriteTask(data, dest);
		manager.addTask(task);
	}

	public void listen(){
		while(true){
			Set keys = getKeys();
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey)iter.next();
				iter.remove();
				if(key.isValid() && key.isAcceptable()){
					accept();
				}
				if(key.isValid() && key.isReadable()){
					if(key.attachment() == null){
						ReadTask task = new ReadTask(key, this);
						key.attach(task);
						manager.addTask(task);
					}
				}
			}
		}
	}

	public void accept(){
		try{
			SocketChannel client = server.accept();
			setKeys(client);
			System.out.println("Got connection from " + client);
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
