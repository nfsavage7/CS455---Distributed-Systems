package cs455.nfs.wireformats;

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
	public static final int PEEK_REQUEST = 1;
	public static final int PEEK_RESPONCE = 2;
	public static final int MOUNT_REQUEST = 3;
	public static final int MOUNT_RESPONCE = 4;
	public static final int MKDIR_REQUEST = 5;
	public static final int RM_REQUEST = 6;
	public static final int RM_RESPONCE = 7;
	public static final int MV_REQUEST = 8;
	public static final int MV_MESSAGE = 9;

	/* **************************************************************************************************************** */
	/*                                                 Marshalling constants                                            */
	/* **************************************************************************************************************** */
	public static final int INT = 4;

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


	/* **************************************************************************************************************** */
	/*                                                 Abstract Methods                                                 */
	/* **************************************************************************************************************** */
	public abstract int getType();
	public abstract int size();
	public abstract byte[] marshall();
	public abstract void unmarshall(byte[] data);

}
