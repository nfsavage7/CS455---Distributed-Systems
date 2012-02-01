package cdn.wireformats;

import java.nio.*;

public abstract class Message{
	
	/* Message Types */
	public static final int NO_TYPE = 0;
	public static final int ROUTER_INFO  = 1;
	public static final int CHAT = 2;

	/* Constants for marshalling */
	protected static final int INT = 4;

	/* Helper Methods for marshalling */
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
	public abstract int getType();
	public abstract byte[] marshall();
	public abstract void unmarshall(byte[] data);

}
