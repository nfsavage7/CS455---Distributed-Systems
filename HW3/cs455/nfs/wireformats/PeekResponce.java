/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */
import java.util.ArrayList;

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                      PeekResponce                                                        */
/*                                                   ------------------                                                     */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Peek Responce wireformat. It is sent form the directory service to the client module with the directory */
/* structure of the direcotry service.                                                                                      */
/* ************************************************************************************************************************ */

public class PeekResponce extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */

	private int type = Message.PEEK_RESPONCE;
	ArrayList<String> contents;

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public PeekResponce(ArrayList<String> c){
		contents = c;
	}

	public PeekResponce(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int size(){
		/* Account for type and number of enteries */
		int ret = Message.INT + Message.INT;
		for(int i = 0; i < contents.size(); i++){
			ret += Message.INT;
			ret += contents.get(i).length();
		}
		return ret;
	}


	public int getType(){
		return type;
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];

		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the number of enteries
		bytes = Message.intToBytes(contents.size());
		index = Message.addBytes(index, ret, bytes);
		

		//Marshall each of the enteries
		for(int i = 0; i < contents.size(); i++){
			//Marshall the size of the entry
			bytes = Message.intToBytes(contents.get(i).length());
			index = Message.addBytes(index, ret, bytes);
			
			//Marshall the entry
			bytes = contents.get(i).getBytes();
			index = Message.addBytes(index, ret, bytes);
		}

		return ret;

	}
	
	public void unmarshall(byte[] data){
		//Unmarshall the type
		int index = Message.INT;
		
		//Unmarshall the number of contents
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int numEntries = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarhsall the entries
		contents = new ArrayList<String>();
		for(int i = 0; i < numEntries; i++){
			//Unmarshall the size of the entry
			bytes = Message.getBytes(index, Message.INT, data);
			int size = Message.bytesToInt(bytes);
			index += Message.INT;

			//Unmarshall the entry
			bytes = Message.getBytes(index, size, data);
			contents.add(new String(bytes));
			index += size;

		}

	}

	public String toString(){
		String ret = "";
		for(int i = 0; i < contents.size(); i++){
			ret += contents.get(i);
			ret += "\n";
		}
		return ret;
	}
}
