package cdn.wireformats;

/* ************************************************************************************************************************ */
/*                                                            RouterInfo                                                    */
/*                                                           ------------                                                   */
/* 	This is the Router Info class. This is send from one router to another when the first router makes a connection     */
/* with another router.                                                                                                     */
/* ************************************************************************************************************************ */

public class RouterInfo extends Message{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private final int type = ROUTER_INFO;
	private String ID;
	private String hostname;
	private int port;

	/* **************************************************************************************************************** */
	/*                                          Constructors and initial methods                                        */
	/* **************************************************************************************************************** */

	public RouterInfo(String id, String hn, int p){
		ID = id;
		hostname = hn;
		port = p;
	}
	
	public RouterInfo(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                             Getter and setter methods                                            */
	/* **************************************************************************************************************** */

	public String getID(){
		return ID;
	}

	public String getHostName(){
		return hostname;
	}

	public int getPort(){
		return port;
	}
	
	public int getType(){
		return type;
	}
	
	public int sizeOf(){
		return Message.INT+Message.INT+ID.length()+Message.INT+hostname.length()+Message.INT;
	}
	
	public String toString(){
		return "Router " + ID + " on port " + port + " on host " + hostname;
	}


	/* **************************************************************************************************************** */
	/*                                           Packing and unpacking methods                                          */
	/* **************************************************************************************************************** */

	public byte[] marshall(){
		//4 for the type, 4 for the length of the ID, 4 for the length of the hostname, and 4 for the port number. I swear I'm not crazy
		byte[] ret = new byte[Message.INT+Message.INT+ID.length()+Message.INT+hostname.length()+Message.INT];
		//Marshall the message type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the length of the ID
		bytes = Message.intToBytes(ID.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the ID
		bytes = ID.getBytes();
		index = Message.addBytes(index, ret, bytes);
		
		//Marshal the length of the hostname
		bytes = Message.intToBytes(hostname.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the hostname
		bytes = hostname.getBytes();
		index = Message.addBytes(index, ret, bytes);

		//Marshall the port
		bytes = Message.intToBytes(port);
		index = Message.addBytes(index, ret, bytes);
		
		return ret;
	}
	
	
	public void unmarshall(byte[] data){
		//Unmarshall length of the ID
		int index = Message.INT; //This is to skip the type since we know it already
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int IDlen = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall ID
		bytes = Message.getBytes(index, IDlen, data);
		ID = new String(bytes);
		index += IDlen;

		//Unmarshall length of the hostname
		bytes = Message.getBytes(index, Message.INT, data);
		int hostnameLen = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall hostname
		bytes = Message.getBytes(index, hostnameLen, data);
		hostname = new String(bytes);
		index += hostnameLen;

		//Unmarshall port
		bytes = Message.getBytes(index, Message.INT, data);
		port = Message.bytesToInt(bytes);
	}

}
