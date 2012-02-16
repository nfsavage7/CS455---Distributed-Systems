package cdn.communications;

/* java imports */
import java.net.*;
import java.io.*;

/* local imports */
import cdn.node.*;
import cdn.wireformats.*;

/* ************************************************************************************************************************ */
/*                                                    	    Link class                                                      */
/*                                                         ------------                                                     */
/* 	This is the link class. It manages the sending and receiving of messages between nodes in the system.               */
/* ************************************************************************************************************************ */

public class Link{
	
	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private Socket sock;
	private Server server;
	private String ID;
	
	/* **************************************************************************************************************** */
	/*                                        Constructors and other inital methods                                     */
	/* **************************************************************************************************************** */

	public Link(Socket s, Server serv){
		sock = s;
		server = serv;
		LinkReaderThread reader = new LinkReaderThread(sock, this);
	}

	/* **************************************************************************************************************** */
	/*                                           Getter and setter methods                                              */
	/* **************************************************************************************************************** */

	public void setID(String id){
		ID = id;
	}

	public String getID(){
		return ID;
	}

	public String getHostname(){
		return sock.getInetAddress().getHostName();
	}

	public String getIP(){
		return sock.getInetAddress().toString();
	}
	/* **************************************************************************************************************** */
	/*                                               Send method                                                        */
	/* **************************************************************************************************************** */

	public void sendData(Message msg){
		try{
			if(!sock.isClosed() && sock.isConnected()){
				LinkSenderThread sender = new LinkSenderThread(msg, sock, this);
				sender.start();
			} else {
				System.out.println("Unable to send message. Connection is already closed");
			}
		} catch (Exception e){}
	}
	
	/* **************************************************************************************************************** */
	/*                                          Receive callback method                                                 */
	/* **************************************************************************************************************** */
	
	public void deliverMessage(byte[] msg){
		server.acceptMsg(msg, this);
	}
	
	/* **************************************************************************************************************** */
	/*                                             Maintnence methods                                                   */
	/* **************************************************************************************************************** */

	public void close(){
		try{
			sock.close();
			server.removeLink(this);
		} catch(Exception e) {}
	}
}
