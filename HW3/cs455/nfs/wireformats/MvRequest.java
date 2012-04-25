/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                         MvRequest                                                        */
/*                                                      ---------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Mv Request message. It is sent from the Client Module, to the Directory Service were the desired file   */
/* to move resides.                                                                                                         */
/* ************************************************************************************************************************ */

public class MvRequest extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.MV_REQUEST;
	private String file;
	private String remoteIP;
	private int remotePort;
	private String targetPath;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public MvRequest(String f, String rip, int rp, String tp){
		file = f;
		remoteIP = rip;
		remotePort = rp;
		targetPath = tp;
	}

	public MvRequest(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}
	
	public String getFile(){
		return file;
	}

	public String getRemoteIP(){
		return remoteIP;
	}
	
	public int getRemotePort(){
		return remotePort;
	}

	public String getTargetPath(){
		return targetPath;
	}

	public int size(){
		return Message.INT + Message.INT + file.length() + Message.INT + remoteIP.length() + Message.INT + Message.INT + targetPath.length();
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];
		
		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the length of the file name
		bytes = Message.intToBytes(file.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the file name
		index = Message.addBytes(index, ret, file.getBytes());

		//Marshall the length of the remoteIP
		bytes = Message.intToBytes(remoteIP.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the remoteIP
		index = Message.addBytes(index, ret, remoteIP.getBytes());

		//Marshall the remotePort
		bytes = Message.intToBytes(remotePort);
		index = Message.addBytes(index, ret, bytes);

		//Marshall the size of the targetPath
		bytes = Message.intToBytes(targetPath.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the targetPath
		bytes = targetPath.getBytes();
		index = Message.addBytes(index, ret, bytes);

		return ret;
	}

	public void unmarshall(byte[] data){
		int index = Message.INT;

		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int len = Message.bytesToInt(bytes);
		index += Message.INT;

		bytes = Message.getBytes(index, len, data);
		file = new String(bytes);
		index += len;

		bytes = Message.getBytes(index, Message.INT, data);
		len = Message.bytesToInt(bytes);
		index += Message.INT;

		bytes = Message.getBytes(index, len, data);
		remoteIP = new String(bytes);
		index += len;

		bytes = Message.getBytes(index, Message.INT, data);
		remotePort = Message.bytesToInt(bytes);
		index += Message.INT;
		
		bytes = Message.getBytes(index, Message.INT, data);
		len = Message.bytesToInt(bytes);
		index += Message.INT;

		bytes = Message.getBytes(index, len, data);
		targetPath = new String(bytes);
		index += len;
	}

}
