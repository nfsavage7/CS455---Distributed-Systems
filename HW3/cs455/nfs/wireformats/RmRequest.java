/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                         RmRequest                                                        */
/*                                                      ---------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Rm Request message. It is sent from the Client Module to the Directory Service. The Directory Service   */
/* will then delete the requested directory                                                                                 */
/* ************************************************************************************************************************ */

public class RmRequest extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.RM_REQUEST;
	private String path;

	public RmRequest(String p){
		path = p;
	}

	public RmRequest(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

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
