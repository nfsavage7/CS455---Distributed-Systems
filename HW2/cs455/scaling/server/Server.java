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
import cs455.scaling.tasks.AcceptConnectionTask;

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
			sock.register(selector, sock.validOps());
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Still not right Boss");
		}
	}

	public void listen(){
		while(true){
			Set keys = getKeys();
			Iterator iter = keys.iterator();
			while(iter.hasNext()){
				SelectionKey key = (SelectionKey)iter.next();
				iter.remove();
				if(key.isAcceptable()){
					manager.addTask(new AcceptConnectionTask(this, key));
				}
				if(key.isReadable()){
					System.out.println("Just trying to figure stuff out, ya know?");
				}
				if(key.isWritable()){
				//	System.out.println("Good job boss");
				}
			//	try{
			//		key.channel().close();
			//	} catch(Exception e){}
			}
		}
	}

	public void accept(SelectionKey key){
		try{
			SocketChannel client = server.accept();
			if(client != null){
				setKeys(client);
				System.out.println("Got connection from " + client);
			}
		//	key.channel().
		} catch (Exception e){
			//System.out.println("Could not connect to client");
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
