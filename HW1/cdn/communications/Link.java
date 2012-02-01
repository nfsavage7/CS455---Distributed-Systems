package cdn.communications;

import java.net.*;
import java.io.*;

import cdn.wireformats.*;

public class Link{

	private Socket sock;
	private byte[] bytes;
	public static boolean hasBytes = false;

	public Link(Socket s){
		sock = s;
		bytes = new byte[0];
	}

/*	public String setLinkId(String linkId){
		info.setID(linkId);
	}*/

	public boolean hasMessage(){
		try{
			return sock.getInputStream().available() > 0;
		} catch (IOException e){
			return false;
		}
	}
	
	public void sendData(Message msg){
		try{
			LinkSenderThread sender = new LinkSenderThread(msg, sock.getOutputStream());
			sender.start();
		} catch (IOException e){}
	}

	public void receiveData(){
		try{
			LinkReaderThread reader = new LinkReaderThread(sock.getInputStream(), this);
			reader.start();
		} catch (IOException e){}
	}

	public void setBytes(byte[] b){
		bytes = b;
		hasBytes = true;
		System.out.println(hasBytes);
		System.out.println("bytes.length = " + bytes.length);
	}
	
	public byte[] getBytesReceived(){
		System.out.println("getBYtes()");
		//while(!hasBytes){}
		System.out.println("Not here");
		byte[] ret = bytes;
		bytes = new byte[0];
		hasBytes = false;
		return ret;
	}
}
