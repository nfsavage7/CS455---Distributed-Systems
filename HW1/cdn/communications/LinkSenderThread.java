package cdn.communications;

import java.io.*;
import java.net.*;

import cdn.wireformats.*;

public class LinkSenderThread extends Thread{

	/* Member vaiables */
	private Message msg;
	private Socket sock;
	private DataOutputStream out;

	/* Constructors */
	public LinkSenderThread(Message m, Socket s){
		msg = m;
		sock = s;
		try{
			out = new DataOutputStream(s.getOutputStream());
		} catch (Exception e){}
	}

	/* Send method */
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
