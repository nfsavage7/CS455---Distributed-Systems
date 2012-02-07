package cdn.communications;

import java.net.*;
import java.io.*;

import cdn.wireformats.*;

public class Link{
	
	/* Member vaiables */
	private Socket sock;
	private byte[] bytes;
	private String ID;
	
	/* Constructors */
	public Link(Socket s){
		sock = s;
		bytes = new byte[0];
	}

	/* getter and setter methods */

	public void setID(String id){
		ID = id;
	}

	public String getID(){
		return ID;
	}

	public String getHostname(){
		return sock.getInetAddress().getHostName();
	}

	/* send and helper methods methods */
	public void sendData(Message msg){
		//synchronized(sock){
			try{
				LinkSenderThread sender = new LinkSenderThread(msg, sock);
				sender.start();
				sender.join();
			} catch (Exception e){}
		//}
	}
	
	/* Receive and helper methods*/
	public boolean hasMessage(){
		synchronized(sock){
			try{
				return sock.getInputStream().available() > 0;
			} catch (IOException e){
				return false;
			}
		}
	}


	public void receiveData(){
		//synchronized(sock){
			try{
				LinkReaderThread reader = new LinkReaderThread(sock, this);
				reader.start();
				reader.join();
			} catch (Exception e){}
		//}
	}

	public void setBytes(byte[] b){
		bytes = b;
	}
	
	public byte[] getBytesReceived(){
		byte[] ret = bytes;
		bytes = new byte[0];
		return ret;
	}

	/* terminate the link */
	public void close(){
		try{
			sock.close();
		} catch(Exception e) {}
	}
}
