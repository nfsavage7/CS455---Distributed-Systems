package cdn.wireformats;

/* java imports */
import java.nio.*;

/* ************************************************************************************************************************ */
/*                                                            Message                                                       */
/*                                                           ---------                                                      */
/* 	This is the Message class. It cannot be instanciated. It holds all of the constants for all wireformats. It also is */
/* responsible for managing the bytes for marshalling and unmarshalling of all wireformats.                                 */
/* ************************************************************************************************************************ */

public abstract class Message{
	
	/* **************************************************************************************************************** */
	/*                                                 Constant type values                                             */
	/* **************************************************************************************************************** */
	public static final int CHAT = 0; //TODO take me out
	public static final int REGISTER_REQUEST = 1;
	public static final int REGISTER_RESPONSE = 2;
	public static final int DEREGISTER_REQUEST = 3;
	public static final int ROUTER_INFO  = 4;
	public static final int PEER_ROUTER_LIST = 5;
	public static final int LINK_INFO = 6;
	public static final int LINK_WEIGHT_UPDATE = 7;

	/* **************************************************************************************************************** */
	/*                                                 Marshalling constants                                            */
	/* **************************************************************************************************************** */
	protected static final int INT = 4;

	/* **************************************************************************************************************** */
	/*                                              Marshalling helper methods                                          */
	/* **************************************************************************************************************** */
	public static int addBytes(int index, byte[] into, byte[] from){
		for(int i = 0; i < from.length; i++){
			into[index+i] = from[i];
		}
		return index + from.length;
	}
	
	public static byte[] intToBytes(int i){
		ByteBuffer buffer = ByteBuffer.allocate(Message.INT);
		buffer.putInt(i);
		return buffer.array();	
	}

	public static int bytesToInt(byte[] bytes){
		ByteBuffer buffer = ByteBuffer.allocateDirect(Message.INT);
		for(int i = 0; i < bytes.length; i++){
			buffer.put(bytes[i]);
		}
		return buffer.getInt(0);
	}

	public static byte[] getBytes(int index, int count, byte[] bytes){
		byte[] ret = new byte[count];
		for(int i = 0; i < count; i++){
			ret[i] = bytes[index + i];
		}
		return ret;
	}



	/* Abstract methods */
	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */
	public abstract int getType();
	public abstract byte[] marshall();
	public abstract void unmarshall(byte[] data);

}
