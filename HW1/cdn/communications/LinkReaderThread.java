package cdn.communications;

import java.io.*;

public class LinkReaderThread extends Thread{
	
	/* Member vaiables */
	private InputStream in;
	private Link link;

	/* Constructors */
	public LinkReaderThread(InputStream i, Link l){
		in = i;
		link = l;
	}
	
	/* receive method */
	public void run(){
		DataInputStream din = new DataInputStream(in);
		byte[] msg = null;
		try{
			msg = new byte[in.available()];
		} catch (IOException e){
			System.out.println("LinkReceiverThread::run: No bytes to read");
		}
		synchronized(din){
			try{
				din.readFully(msg);
			} catch (IOException e){
				System.out.println("LinkReaderThread: failed to read from input stream");
			}
		}
		link.setBytes(msg);
	}
}
