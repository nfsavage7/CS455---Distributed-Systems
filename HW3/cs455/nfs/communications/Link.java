/* pacakge statement */
package cs455.nfs.communications;

/* java imports */
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;


/* local imports */ 
import cs455.nfs.wireformats.Message;
import cs455.nfs.wireformats.PeekRequest;
import cs455.nfs.wireformats.PeekResponce;
import cs455.nfs.wireformats.MountRequest;
import cs455.nfs.wireformats.MountResponce;
import cs455.nfs.wireformats.MkdirRequest;
import cs455.nfs.wireformats.RmRequest;
import cs455.nfs.wireformats.RmResponce;
import cs455.nfs.wireformats.MvRequest;
import cs455.nfs.wireformats.MvMessage;

/* ************************************************************************************************************************ */
/*                                                           Link                                                           */
/*                                                        ----------                                                        */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Link class. It is incharge of sending and recieving the wireformats between the ClientModule and the    */
/* remote Direcotry Service                                                                                                 */
/* ************************************************************************************************************************ */

public class Link{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	private String IP;
	private int port;
	private SocketChannel server;
	private Selector selector;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public Link(String ip, int p){
		try{
			IP = ip;
			port = p;
			InetSocketAddress servIP = new InetSocketAddress(IP, port);
			server = SocketChannel.open(servIP);
			server.configureBlocking(false);
			selector = selector.open();
			server.register(selector, SelectionKey.OP_READ);

		} catch (Exception e){
			System.out.println("Client:: Failed to connect to remote direcotry service");
			return;
		}
		
	}

	public Link(SocketChannel s){
		server = s;
		try{
			selector = selector.open();
			server.register(selector, SelectionKey.OP_READ);
		} catch (Exception e){}
	}
	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public String getIP(){
		return IP;
	}
		
	public int getPort(){
		return port;
	}

	public void send(Message msg){
	//	System.out.println(msg.size());
		ByteBuffer len = ByteBuffer.wrap(Message.intToBytes(msg.size()));
		len.rewind();
		ByteBuffer payload = ByteBuffer.wrap(msg.marshall());
		payload.rewind();
		int bytes = 0;
		try{
			server.write(len);
			while(payload.hasRemaining()){
				bytes += server.write(payload);
			}
		} catch (Exception e){}


	}

	public boolean hasBytes(){
		try{
			selector.selectNow();
		} catch (Exception e){}
		Set keys = selector.selectedKeys();
		Iterator iter = keys.iterator();
		while(iter.hasNext()){
			SelectionKey key = (SelectionKey)(iter.next());
			iter.remove();
			if(key.isReadable()){
				return true;
			}
		}
		return false;
	}



	public Message read(){
		try{
			selector.select();
		} catch (Exception e){}
		Set keys = selector.selectedKeys();
		Iterator iter = keys.iterator();
		while(iter.hasNext()){
			SelectionKey key = (SelectionKey)(iter.next());
			iter.remove();
			if(key.isReadable()){
				ByteBuffer buffer = ByteBuffer.allocate(Message.INT);
				try{
					int bytes = server.read(buffer);
					if(bytes == -1){
						close();
						return null;
					}
					if(bytes != Message.INT){
						System.out.println(bytes);
						System.out.println("Something is not right Boss.");
					}
					int size = Message.bytesToInt(buffer.array());
					//System.out.println(size);
					buffer = ByteBuffer.allocate(size);
					bytes = 0;
					while(buffer.hasRemaining() && key.isReadable()){
						bytes += server.read(buffer);
					//	System.out.println(bytes);
					}
					byte[] data = buffer.array();
					int type =  Message.bytesToInt(Message.getBytes(0, 4, data));
				//	System.out.println("TYPE: " + type); 
					if(type == Message.PEEK_REQUEST){
						return new PeekRequest();
					} else if (type == Message.PEEK_RESPONCE){
						return new PeekResponce(data);
					} else if (type == Message.MOUNT_REQUEST){
						return new MountRequest(data);
					} else if(type == Message.MOUNT_RESPONCE){
						return new MountResponce(data);
					} else if (type == Message.MKDIR_REQUEST){
						return new MkdirRequest(data);
					} else if (type == Message.RM_REQUEST){
						return new RmRequest(data);
					} else if (type == Message.RM_RESPONCE){
						return new RmResponce(data);
					} else if (type == Message.MV_REQUEST){
						System.out.println("Request");
						return new MvRequest(data);
					} else if (type == Message.MV_MESSAGE){
						System.out.println("Responce");
						return new MvMessage(data);
					} else {
						System.out.println("Type undefined: " + type);
					}
				} catch (Exception e){}
			}
		}
		return null;

	}

	public void close(){
		try{
			selector.close();
			server.close();
		} catch (Exception e){}
	}

}
