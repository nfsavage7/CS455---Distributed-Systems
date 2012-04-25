/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                       PeekRequest                                                        */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the PeekRequest class. It is the message sent form the client to the remote directory service, where the    */
/* client wants to see the direcotry structure of the direcotry service without mounting it.                                */
/* ************************************************************************************************************************ */

public class PeekRequest extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	
	private int type = Message.PEEK_REQUEST;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public PeekRequest(){}

	public PeekRequest(byte[] data){}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public int size(){
		return Message.INT;
	}

	public byte[] marshall(){
		return intToBytes(type);
	}

	public void unmarshall(byte[] data){}

}
