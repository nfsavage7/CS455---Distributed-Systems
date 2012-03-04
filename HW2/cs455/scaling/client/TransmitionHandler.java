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

	private SocketChannel server;
	private int sleep;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public TransmitionHandler(SocketChannel serv, int rate){
		server = serv;
		sleep = 1000/rate;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public void run(){
		int i = 0;
		while(i < 5){
			i++;
			System.out.println("Sent " + i);
			RandomData data = new RandomData();
			ByteBuffer buffer = ByteBuffer.wrap(data.getBytes());
			try{
				int bytes = server.write(buffer);
				System.out.println("Sending: " + data.getHash());
			} catch(Exception e){
				System.out.println("TransmitionHandler::run: could not write to the socket chanel");
			}
			try{
				sleep(sleep);
			} catch (InterruptedException e){}
		}
	}

}
