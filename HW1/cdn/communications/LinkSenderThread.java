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
	private Link link;

	/* **************************************************************************************************************** */
	/*                                          Constructors and inital methods                                         */
	/* **************************************************************************************************************** */

	public LinkSenderThread(Message m, Socket s, Link l){
		msg = m;
		sock = s;
		link = l;
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
				link.close();
			}
		}
	}
}
