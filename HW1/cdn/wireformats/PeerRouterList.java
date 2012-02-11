package cdn.wireformats;

/* java imports */
import java.util.*;

/* ************************************************************************************************************************ */
/*	                                                   PeerRouterList                                                   */
/*                                                        ----------------                                                  */
/* 	This is the Peer Router List that is sent from the Discovery to the routers to tell them who to connect to.         */
/* ************************************************************************************************************************ */

public class PeerRouterList extends Message{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private final int type = PEER_ROUTER_LIST;
	private ArrayList<RouterInfo> peers;

	/* **************************************************************************************************************** */
	/*                                       Constructors and other inital methods                                      */
	/* **************************************************************************************************************** */

	public PeerRouterList(ArrayList<RouterInfo> p){
		peers = p;
	}

	public PeerRouterList(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                             Getter and setter methods                                            */
	/* **************************************************************************************************************** */

	public ArrayList<RouterInfo> getPeers(){
		return peers;
	}

	public int getType(){
		return type;
	}

	public int numBytesOfPeers(){
		int ret = 0;
		for(int i = 0; i < peers.size(); i++){
			ret += Message.INT;
			ret += peers.get(i).sizeOf();
		}
		return ret;
	}

	/* **************************************************************************************************************** */
	/*                                          Packing and unpacking methods                                           */
	/* **************************************************************************************************************** */

	public byte[] marshall(){
		//4 for the type, 4 for the num of peers, and the number of peers times the size of a router info
		byte[] ret = new byte[Message.INT + Message.INT + numBytesOfPeers()];

		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the number of peers
		bytes = Message.intToBytes(peers.size());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the RouterInfos
		for(int i = 0; i < peers.size(); i++){
			RouterInfo r = peers.get(i);
			//Marshall the size of the RouterInfo
			bytes = Message.intToBytes(r.sizeOf());
			index = Message.addBytes(index, ret, bytes);

			//Marshall the RouterInfo
			bytes = r.marshall();
			index = Message.addBytes(index, ret, bytes);
		}
		
		return ret;
	}

	public void unmarshall(byte[] data){
		//Skip the type
		int index = Message.INT;

		//Unmarshall the number of peers
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int numPeers = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall the RouterInfos
		peers = new ArrayList<RouterInfo>();
		for(int i = 0; i < numPeers; i++){
			bytes = Message.getBytes(index, Message.INT, data);
			int size = Message.bytesToInt(bytes);
			index += Message.INT;
	
			bytes = Message.getBytes(index, size, data);
			RouterInfo r = new RouterInfo(bytes);
			peers.add(r);
			index += r.sizeOf();
		}
	}
}
