/* pacakge statement */
package cs455.scaling.client;

/* java imports */
import java.util.Scanner;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
/* local imports */ 

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

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Client(String host, int port, int r){
		try{
			server = SocketChannel.open(new InetSocketAddress(host ,port));
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Client:: Failed to connect to server");
			System.exit(1);
		}
		System.out.println("Server: " + server);
		rate = r;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */
	
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
		while(true){}
	}
}
