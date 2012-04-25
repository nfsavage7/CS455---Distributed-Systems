/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                      <Class Name>                                                        */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/*                           			     <Class description>                                                    */
/* ************************************************************************************************************************ */

public class MvMessage extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.MV_MESSAGE;
	private String path;
	private byte[] contents;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public MvMessage(String p, byte[] c){
		path = p;
		contents = c;
	}

	public MvMessage(byte[] data){
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
	
	public byte[] getContents(){
		return contents;
	}
	
	public int size(){
		return Message.INT + Message.INT + path.length() + Message.INT + contents.length;
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];

		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		bytes = Message.intToBytes(path.length());
		index = Message.addBytes(index, ret, bytes);

		index = Message.addBytes(index, ret, path.getBytes());

		bytes = Message.intToBytes(contents.length);
		index = Message.addBytes(index, ret, bytes);

		index = Message.addBytes(index, ret, contents);

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
		
		bytes = Message.getBytes(index, Message.INT, data);
		len = Message.bytesToInt(bytes);
		index += Message.INT;

		contents = Message.getBytes(index, len, data);
		index += len;

	}
}
