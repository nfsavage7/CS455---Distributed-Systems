package cdn.communications;

/* java imports */
import java.io.*;
import java.net.*;

/* ************************************************************************************************************************ */
/*                                                     LinkReaderThread class                                               */
/*                                                    ------------------------                                              */
/* 	This is the Link Reader Thread. When a link is created, it spanws one of these to receive the messages. This is     */
/* based on a signalling mechanism. When a message is ready, it immediately gets called on the server it is intended for.   */
/* ************************************************************************************************************************ */

public class LinkReaderThread extends Thread{
	
	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private Socket sock;
	private InputStream in;
	private Link link;

	/* **************************************************************************************************************** */
	/*                                       Constructors and other inital methods                                      */
	/* **************************************************************************************************************** */

	public LinkReaderThread(Socket s, Link l){
		sock = s;
		try{
			in = sock.getInputStream();
		} catch (IOException e){}
		link = l;
		start();
	}
	
	/* **************************************************************************************************************** */
	/*                                                Revieve method                                                    */
	/* **************************************************************************************************************** */

	public void run(){
		while(true){
			try{
				if(in.available() > 0){
					DataInputStream din = new DataInputStream(in);
					byte[] msg = null;
					try{
						msg = new byte[in.available()];
					} catch (IOException e){
						System.out.println("LinkReceiverThread::run: No bytes to read");
					}
					try{
						din.readFully(msg);
					} catch (IOException e){
						link.close();
						break;
					}
					link.deliverMessage(msg);
				}
			} catch (IOException e){
				link.close();
				break;
			}
		}
	}
}
