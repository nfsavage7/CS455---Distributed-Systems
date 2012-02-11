package cdn.communications;

/* java imports */
import java.io.*;
import java.net.*;

/* local imports */
import cdn.wireformats.Message;

/* ************************************************************************************************************************ */
/*                                                 	LinkSenderThread class                                              */
/*                                                     ------------------------                                             */
/* 	This is the Link Sender Thread. It gets spawned when a Link is created and it is inchage of sending messages        */
/* between nodes in the network.                                                                                            */
/* ************************************************************************************************************************ */

public class LinkSenderThread extends Thread{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private Message msg;
	private Socket sock;
	private DataOutputStream out;

	/* **************************************************************************************************************** */
	/*                                          Constructors and inital methods                                         */
	/* **************************************************************************************************************** */

	public LinkSenderThread(Message m, Socket s){
		msg = m;
		sock = s;
		try{
			out = new DataOutputStream(s.getOutputStream());
		} catch (Exception e){}
	}

	/* **************************************************************************************************************** */
	/*                                                   Send method                                                    */
	/* **************************************************************************************************************** */

	public void run(){
		byte[] data = msg.marshall();
		synchronized(sock){
			try{
				out.write(data, 0, data.length);
				out.flush();
			} catch (IOException e){
				System.out.println("LinkSenderThread: Failed to write to ouput stream");
			}
		}
	}
}
