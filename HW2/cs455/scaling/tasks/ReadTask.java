/* pacakge statement */
package cs455.scaling.tasks;

/* java imports */
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.ByteBuffer;
import java.net.InetAddress;

/* local imports */
import cs455.scaling.server.Server;
import cs455.scaling.util.RandomData;
/* ************************************************************************************************************************ */
/*                                                         ReadTask                                                         */
/*                                                       ------------                                                       */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Conupter Science at Colorado State University            */
/* 	This is the Read Task. It is generated every time the server has a message to read from the client. This is handled */
/* by the thread pool                                                                                                       */
/* ************************************************************************************************************************ */

public class ReadTask implements Task{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private SelectionKey key;
	private SocketChannel source;
	private Server server;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public ReadTask(SelectionKey k, Server serv){
		key = k;
		source = (SocketChannel)key.channel();
		server = serv;
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	private String getHostName(){
		String full = ((InetAddress)(source.socket().getInetAddress())).getHostName();
		return full.substring(0, full.indexOf("."));
	}

	/* **************************************************************************************************************** */
	/*                                              Overwritten methods                                                 */
	/* **************************************************************************************************************** */

	public void execute(String worker){
		//Read in the data and create a RandomData
		//Create a WriteTask for that Data
		//Return the WriteTask to the server who will put it in the thread pool
		
		ByteBuffer buffer = ByteBuffer.allocate(8192);
		try{
			synchronized(source){
				int bytes = source.read(buffer);
				if(bytes == -1){
					server.deregister(key);
				} else if (bytes > 0) {
					while(buffer.hasRemaining()){
						bytes = source.read(buffer);
					}
					RandomData data = new RandomData(buffer.array());
					server.print("[ClientMessage-" + getHostName() + "] Hash: " + data.getHash());
					server.print("[ServerStatus] Message from Client at " + getHostName() + " handled by " + worker + "\n");
					server.write(data, source);
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			synchronized(key){
				key.attach(null);
			}
		}
		
	}
	
}
