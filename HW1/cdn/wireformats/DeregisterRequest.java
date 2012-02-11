package cdn.wireformats;

/* ************************************************************************************************************************ */
/* 		                                           DeregisterRequest                                                */
/*                                                        -------------------                                               */
/* 	This is the Deregister Request message. It is sent from a router to the discovery node.		                    */
/* ************************************************************************************************************************ */

public class DeregisterRequest extends Message{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private final int type = DEREGISTER_REQUEST;
	private String IP;
	private int port;
	private String ID;

	/* **************************************************************************************************************** */
	/*                                         Constructors and other inital methods                                    */
	/* **************************************************************************************************************** */
	
	public DeregisterRequest(String ip, int p, String id){
		IP = ip;
		port = p;
		ID = id;
	}
	
	public DeregisterRequest(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                               Getter and setter methods                                          */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public String getID(){
		return ID;
	}

	public int getPort(){
		return port;
	}

	/* **************************************************************************************************************** */
	/*                                             acking and unpacking methods                                         */
	/* **************************************************************************************************************** */

	public byte[] marshall(){
		byte[] ret = new byte[Message.INT + Message.INT + IP.length() + Message.INT + Message.INT + ID.length()];
	
		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the len of the IP
		bytes = Message.intToBytes(IP.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the IP
		bytes = IP.getBytes();
		index = Message.addBytes(index, ret, bytes);
	
		//Marshall the port
		bytes = Message.intToBytes(port);
		index = Message.addBytes(index, ret, bytes);

		//Marshall the len of the ID
		bytes = Message.intToBytes(ID.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the ID
		bytes = ID.getBytes();
		index = Message.addBytes(index, ret, bytes);

		return ret;
	}

	public void unmarshall(byte[] data){
		//skip the type
		int index = Message.INT;

		//Unmarhsall the IP
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		index += Message.INT;
		int len = Message.bytesToInt(bytes);
		bytes = Message.getBytes(index, len, data);
		IP = new String(bytes);
		index += len;

		//Unmarshall the port
		bytes = Message.getBytes(index, Message.INT, data);
		port = Message.bytesToInt(bytes);
		index += Message.INT;
		
		//Unmarshall the ID
		bytes = Message.getBytes(index, Message.INT, data);
		index += Message.INT;
		len = Message.bytesToInt(bytes);
		bytes = Message.getBytes(index, len, data);
		ID = new String(bytes);

	}

}
