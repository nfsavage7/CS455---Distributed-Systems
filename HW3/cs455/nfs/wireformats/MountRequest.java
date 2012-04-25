/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                      MountRequest                                                        */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Mount Request class. It is sent form the client to the directory service to let it know, it wants to    */
/* mount its files                                                                                                          */
/* ************************************************************************************************************************ */

public class MountRequest extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.MOUNT_REQUEST;
	private String path;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public MountRequest(String p){
		path = p;
	}

	public MountRequest(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public String getPath(){
		return path;
	}

	public int size(){
		return Message.INT + Message.INT + path.length();
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];
		
		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the length of the path
		bytes = Message.intToBytes(path.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the path
		index = Message.addBytes(index, ret, path.getBytes());

		return ret;
	}
		
	public void unmarshall(byte[] data){
		int index = Message.INT;

		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int len = Message.bytesToInt(bytes);
		index += Message.INT;

		bytes = Message.getBytes(index, len, data);
		path = new String(bytes);
		index += len;

		
	}
}
