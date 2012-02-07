package cdn.communications;

import java.io.*;
import java.net.*;

public class LinkReaderThread extends Thread{
	
	/* Member vaiables */
	private Socket sock;
	private InputStream in;
	private Link link;

	/* Constructors */
	public LinkReaderThread(Socket s, Link l){
		sock = s;
		try{
			in = sock.getInputStream();
		} catch (IOException e){}
		link = l;
	}
	
	/* receive method */
	public void run(){
		DataInputStream din = new DataInputStream(in);
		byte[] msg = null;
		synchronized(sock){
			try{
				msg = new byte[in.available()];
			} catch (IOException e){
				System.out.println("LinkReceiverThread::run: No bytes to read");
			}
		//	synchronized(din){
				try{
					din.readFully(msg);
				} catch (IOException e){
					System.out.println("LinkReaderThread: failed to read from input stream");
				}
		//	}
		}
		link.setBytes(msg);
	}
}
