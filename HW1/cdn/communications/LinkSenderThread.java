package cdn.communications;

import java.io.*;

import cdn.wireformats.*;

public class LinkSenderThread extends Thread{
	private Message msg;
	private DataOutputStream out;

	public LinkSenderThread(Message m, OutputStream o){
		msg = m;
		out = new DataOutputStream(o);
	}

	public void run(){
		byte[] data = msg.marshall();
		synchronized(out){
			try{
				System.out.println("send " + data.length);
				out.write(data, 0, data.length);
				out.flush();
			} catch (IOException e){
				System.out.println("LinkSenderThread: Failed to write to ouput stream");
				System.exit(1);
			}
		}
	}
}
