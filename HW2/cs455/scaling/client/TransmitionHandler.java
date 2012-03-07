/* pacakge statement */
package cs455.scaling.client;

/* java imports */
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/* local imports */
import cs455.scaling.util.RandomData;

/* ************************************************************************************************************************ */
/*                                                     Transmision Handler                                                  */
/*                                                   -----------------------                                                */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This class sends the messages to the server at the specified intervals. It also tracks the hashes that the server   */
/* sends back                                                                                                               */
/* ************************************************************************************************************************ */

public class TransmitionHandler extends Thread{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private Client client;
	private SocketChannel server;
	private int sleep;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public TransmitionHandler(Client c, SocketChannel serv, int rate){
		client = c;
		server = serv;
		sleep = 1000/rate;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public void run(){
		int i = 0, count = 1;
		while(i >= 0){
			i++;
			RandomData data = new RandomData();
			ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
			try{
				int bytes = server.write(buffer);
				Message msg = new Message(data.getHash(), count);
				client.addMessage(msg);
				count++;
			} catch(Exception e){
				System.out.println("Server terminated connection");
				client.finish();
				return;
			}
			try{
				sleep(sleep);
			} catch (InterruptedException e){}
		}
	}

}
