package cdn.wireformats;

/* java imports */
import java.util.HashMap;
import java.util.ArrayList;

/* ************************************************************************************************************************ */
/*	                                                   Routring Plan                                                    */
/*                                                        ---------------                                                   */
/* 	This is Routring Plan that is send from router to router. It is how a router knows where to foward packets to       */
/* ************************************************************************************************************************ */

public class RoutingPlan extends Message {

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private final int type = Message.ROUTING_PLAN;
	private HashMap<String, ArrayList<String>> plan;
	
	/* **************************************************************************************************************** */
	/*                                       Constructors and other inital methods                                      */
	/* **************************************************************************************************************** */

	public RoutingPlan(HashMap<String, ArrayList<String>> p){
		plan = p;
	}

	public RoutingPlan(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                             Getter and setter methods                                            */
	/* **************************************************************************************************************** */

	public ArrayList<String> getRoutes(String id){
		return plan.get(id);
	}

	//TODO convention: size to be marshalled by the data packet
	public int sizeOf(){
		int size = 0;
		Object[] keys = plan.keySet().toArray();
		size += Message.INT; 				//this is for the number of keys
		for(int i = 0; i < keys.length; i++){
			String key = ((String)keys[i]);
			size += Message.INT;    		//this is for the size of the key
			size += key.length();    		//this is for the key
			size += Message.INT;     		//this is for the number of peers in the MST
			ArrayList<String> peers = plan.get(key);
			for(int j = 0; j < peers.size(); j++){
				size += Message.INT;		//this is for the size of the peer
				size += peers.get(j).length();	//this is for the peer
			}
		}
		return size;
	}

	public int getType(){
		return type;
	}

	/* **************************************************************************************************************** */
	/*                                          Packing and unpacking methods                                           */
	/* **************************************************************************************************************** */

	//Since this is never sent alone, no need to marshall the type
	public byte[] marshall(){
		byte [] ret = new byte [sizeOf()];

		/* Marshall the number of keys */
		Object[] keys = plan.keySet().toArray();
		byte[] bytes = Message.intToBytes(keys.length);
		int index = Message.addBytes(0, ret, bytes);

		for(int i = 0; i < keys.length; i++){
			String key = (String)(keys[i]);
			
			/* Marshall the size of the key */
			bytes = Message.intToBytes(key.length());
			index = Message.addBytes(index, ret, bytes);

			/* Marshall the key */
			bytes = key.getBytes();
			index = Message.addBytes(index, ret, bytes);

			ArrayList<String> peers = plan.get(key);
			
			/* Marshall the number of peers */
			bytes = Message.intToBytes(peers.size());
			index = Message.addBytes(index, ret, bytes);

			for(int j = 0; j < peers.size(); j++){
				/* Marshall the size of the peer */
				bytes = Message.intToBytes(peers.get(j).length());
				index = Message.addBytes(index, ret, bytes);
			
				/* Marshall the peer */
				bytes = peers.get(j).getBytes();
				index = Message.addBytes(index, ret, bytes);	
			}
			

		}
		return ret;
	}

	public void unmarshall(byte[] data){
		plan = new HashMap<String, ArrayList<String>>();
		int index = 0;	

		/* Unmarshall the number of keys */
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int keys = Message.bytesToInt(bytes);
		index += Message.INT;

		for(int i = 0; i < keys; i++){
			/* Unmarshall the size of the key */
			bytes = Message.getBytes(index, Message.INT, data);
			int len = Message.bytesToInt(bytes);
			index += Message.INT;

			/* Unmarshall the key */
			bytes = Message.getBytes(index, len, data);
			String key = new String(bytes);
			index += len;

			/* Unmarshall the number of peers */
			bytes = Message.getBytes(index, Message.INT, data);
			int peers = Message.bytesToInt(bytes);
			index += Message.INT;

			ArrayList<String> connections = new ArrayList<String>();

			for(int j = 0; j < peers; j++){
				/* Unmarshall the size of the peer */
				bytes = Message.getBytes(index, Message.INT, data);
				len = Message.bytesToInt(bytes);
				index += Message.INT;

				/* Unmarshall the peer */
				bytes = Message.getBytes(index, len, data);
				connections.add(new String(bytes));
				index += len;
			}
			plan.put(key,connections);
		}
	}

}
