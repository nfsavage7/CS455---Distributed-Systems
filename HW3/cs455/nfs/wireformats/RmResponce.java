/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                         RmResponce                                                       */
/*                                                      ----------------                                                    */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Rm Responce. It is sent from the Directoy Service to the Client Module to let it know if the directory  */
/* was sucessfully deleted or not.                                                                                          */
/* ************************************************************************************************************************ */

public class RmResponce extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.RM_RESPONCE;
	private byte status;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public RmResponce(byte s){
		status = s;
	}
	
	public RmResponce(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public byte getStatus(){
		return status;
	}
		
	public int size(){
		return Message.INT + 1;
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];

		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		bytes = new byte[1];
		bytes[0] = status;
		index = Message.addBytes(index, ret, bytes);
	
		return ret;
	}

	public void unmarshall(byte[] data){
		int index = Message.INT;
		
		status = (Message.getBytes(index, 1, data))[0];	
		index++;
	}


}
