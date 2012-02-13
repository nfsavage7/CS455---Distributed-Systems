package cdn.wireformats;

import java.util.ArrayList;

/* ************************************************************************************************************************ */
/*	                                                 LinkWeightUpdate                                                   */
/*                                                      ------------------                                                  */
/* 	This is the Link Weight Update that is sent from the Discovery to the routers to tell them the wieghts on all of    */
/* their links.
/* ************************************************************************************************************************ */

public class LinkWeightUpdate extends Message{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private int type = LINK_WEIGHT_UPDATE;
	private ArrayList<LinkInfo> links;

	/* **************************************************************************************************************** */
	/*                                       Constructors and other inital methods                                      */
	/* **************************************************************************************************************** */

	public LinkWeightUpdate(ArrayList<LinkInfo> l){
		links = l;
	}

	public LinkWeightUpdate(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                             Getter and setter methods                                            */
	/* **************************************************************************************************************** */

	public int getType(){
		return type;
	}

	public int sizeOf(){
		int ret = Message.INT + Message.INT;
		for(int i = 0; i < links.size(); i++){
			ret += links.get(i).sizeOf();
			ret += Message.INT;
		}
		return ret;
	}
	/* **************************************************************************************************************** */
	/*                                          Packing and unpacking methods                                           */
	/* **************************************************************************************************************** */

	public byte[] marshall(){
		byte[] ret = new byte[sizeOf()];

		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);
		
		//Marshall the number of links
		bytes = Message.intToBytes(links.size());
		index = Message.addBytes(index, ret, bytes);
		

		//Marshall each of the Link Infos
		for(int i = 0; i < links.size(); i++){
			bytes = links.get(i).marshall();
			index = Message.addBytes(index, ret, bytes);
		}
		return ret;
	}

	public void unmarshall(byte[] data){
		//Unmarshall the type
		int index = Message.INT;
		
		//Unmarshall the number of links
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int numLinks = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall link infos
		links = new ArrayList<LinkInfo>();
		for(int i = 0; i < numLinks; i++){
			bytes = Message.getBytes(index, Message.INT, data);
			int size = Message.bytesToInt(bytes);
			index += Message.INT;

			bytes = Message.getBytes(index, size, data);
			links.add(new LinkInfo(bytes));
			index += size;
		}
	}

}
