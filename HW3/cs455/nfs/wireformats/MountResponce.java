/* pacakge statement */
package cs455.nfs.wireformats;

/* java imports */
import java.util.ArrayList;

/* local imports */ 

/* ************************************************************************************************************************ */
/*                                                      MountResponce                                                       */
/*                                                   -------------------                                                    */
/* 						     Author: Nicholas Franklin Savage                                       */
/*                                                   Class:  CS 455: Into to Distributed Systems                            */
/*						     Department of Comupter Science at Colorado State University            */
/* 	This is the Mount Responce. It is sent form the directory service to the client module. It contains the files of */
/* the requested directory.                                                                                                 */
/* ************************************************************************************************************************ */

public class MountResponce extends Message{

	/* **************************************************************************************************************** */
	/*                                                Member variables                                                  */
	/* **************************************************************************************************************** */
	
	private int type = Message.MOUNT_RESPONCE;
	private ArrayList<String> files;
	private byte[] isDir;	

	/* **************************************************************************************************************** */
	/*                                    Constructors and other inital methods                                         */
	/* **************************************************************************************************************** */

	public MountResponce(ArrayList<String> f, byte[] d){
		files = f;
		isDir = d;
	}

	public MountResponce(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                            Getter and setter methods                                             */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public ArrayList<String> getFiles(){
		return files;
	}
	
	public byte[] isDirectory(){
		return isDir;
	}

	public int size(){
		int size = Message.INT + Message.INT;
		for(int i = 0; i < files.size(); i++){
			size += Message.INT;
			size += files.get(i).length();
			size += 1;
		}
		return size;
	}

	public byte[] marshall(){
		byte[] ret = new byte[size()];

		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the number of enteries
		bytes = Message.intToBytes(files.size());
		index = Message.addBytes(index, ret, bytes);
		

		//Marshall each of the enteries
		for(int i = 0; i < files.size(); i++){
			//Marshall the size of the entry
			bytes = Message.intToBytes(files.get(i).length());
			index = Message.addBytes(index, ret, bytes);
			
			//Marshall the entry
			bytes = files.get(i).getBytes();
			index = Message.addBytes(index, ret, bytes);
			
			//Marshall the isDir byte
			bytes = new byte[1];
			bytes[0] = isDir[i];
			index = Message.addBytes(index, ret, bytes);
		}

		return ret;

	}

	public void unmarshall(byte[] data){
		//Unmarshall the type
		int index = Message.INT;
		
		//Unmarshall the number of files
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int numEntries = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarhsall the entries
		files = new ArrayList<String>();
		isDir = new byte[numEntries];
		for(int i = 0; i < numEntries; i++){
			//Unmarshall the size of the entry
			bytes = Message.getBytes(index, Message.INT, data);
			int size = Message.bytesToInt(bytes);
			index += Message.INT;

			//Unmarshall the entry
			bytes = Message.getBytes(index, size, data);
			files.add(new String(bytes));
			index += size;

			bytes = Message.getBytes(index, 1, data);
			isDir[i] = bytes[0];
			index += 1;		

		}


	}
}
