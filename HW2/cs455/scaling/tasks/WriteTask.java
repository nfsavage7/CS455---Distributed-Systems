/* pacakge statement */
package cs455.scaling.tasks;

/* java imports */
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;

/* local imports */ 
import cs455.scaling.util.RandomData;

/* ************************************************************************************************************************ */
/*                                                      Write Task                                                          */
/*                                                    --------------                                                        */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Write Task class. It is responsible for sending the hash of the recieved data back to the client        */
/* ************************************************************************************************************************ */

public class WriteTask implements Task{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private RandomData data;
	private SocketChannel dest;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public WriteTask(RandomData data, SocketChannel dest){
		this.data = data;
		this.dest = dest;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public byte[] intToBytes(int i){
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(i);
		return buffer.array();	
	}


	public void execute(){
		ByteBuffer len = ByteBuffer.wrap(intToBytes(data.getHash().length()));
		ByteBuffer msg = ByteBuffer.wrap(data.getHash().getBytes());
		System.out.println("Sending: " +data.getHash());
		try{
			dest.write(len);
			dest.write(msg);
		} catch (Exception e){
			System.out.println("WriteTask::execute: failed to send hash to client");
		}
	}

}
