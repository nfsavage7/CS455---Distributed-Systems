package cdn.communications;

import java.io.*;

public class LinkReaderThread extends Thread{
	
	private InputStream in;
	private Link link;

	public LinkReaderThread(InputStream i, Link l){
		in = i;
		link = l;
	}

	public void run(){
		int bytesToRead = 0;
		while(bytesToRead == 0){
			try{
				if(in.available() > 0){
					bytesToRead = in.available();
				}
			} catch (IOException e){}
		}
		DataInputStream din = new DataInputStream(in);
		byte[] msg = new byte[bytesToRead];
		synchronized(din){
			try{
				din.readFully(msg);
				System.out.println(msg.length);
			} catch (IOException e){
				System.out.println("LinkReaderThread: failed to read from input stream");
				System.exit(1);
			}
		}
		link.setBytes(msg);
	}
}
