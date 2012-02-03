package cdn.communications;

import java.net.*;
import java.io.*;

import cdn.wireformats.*;

public class Link{
	
	/* Member vaiables */
	private Socket sock;
	private byte[] bytes;
	
	/* Constructors */
	public Link(Socket s){
		sock = s;
		bytes = new byte[0];
	}

	/* getter and setter methods */

/*	public String setLinkId(String linkId){
		info.setID(linkId);
	}*/

	public String getHostname(){
		return sock.getInetAddress().getHostName();
	}

	/* send and helper methods methods */
	public void sendData(Message msg){
		try{
			LinkSenderThread sender = new LinkSenderThread(msg, sock.getOutputStream());
			sender.start();
		} catch (IOException e){}
	}
	
	/* Receive and helper methods*/
	public boolean hasMessage(){
		try{
			return sock.getInputStream().available() > 0;
		} catch (IOException e){
			return false;
		}
	}


	public void receiveData(){
		try{
			LinkReaderThread reader = new LinkReaderThread(sock.getInputStream(), this);
			reader.start();
			reader.join();
		} catch (Exception e){}
	}

	public void setBytes(byte[] b){
		bytes = b;
	}
	
	public byte[] getBytesReceived(){
		byte[] ret = bytes;
		bytes = new byte[0];
		return ret;
	}
}
