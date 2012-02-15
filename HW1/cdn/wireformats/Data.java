package cdn.wireformats;


/* java imports */
import java.util.HashMap;
import java.util.ArrayList;
/* ************************************************************************************************************************ */
/*	                                                        Data                                                        */
/*                                                             ------                                                       */
/*       	                     This is the data packet that will be sent throughout the CDN                           */
/* ************************************************************************************************************************ */

public class Data extends Message{

	/* **************************************************************************************************************** */
	/*                                                 Member variables                                                 */
	/* **************************************************************************************************************** */

	private final int type = Message.DATA;	
	private RoutingPlan plan;
	private int tracker;
	/* **************************************************************************************************************** */
	/*                                       Constructors and other inital methods                                      */
	/* **************************************************************************************************************** */

	public Data(HashMap<String, ArrayList<String>> p, int t){
		plan = new RoutingPlan(p);
		tracker = t;
	}

	public Data(byte[] data){
		unmarshall(data);
	}

	/* **************************************************************************************************************** */
	/*                                             Getter and setter methods                                            */
	/* **************************************************************************************************************** */

	public ArrayList<String> getRoutes(String id){
		return plan.getRoutes(id);
	}
	
	public int getTracker(){
		return tracker;
	}

	public int getType(){
		return type;
	}

	/* **************************************************************************************************************** */
	/*                                          Packing and unpacking methods                                           */
	/* **************************************************************************************************************** */
	
	public byte[] marshall(){
		byte[] ret = new byte[Message.INT +Message.INT + plan.sizeOf() + Message.INT];

		/* Marshall the type */
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		/* Marshall the size of the routing plan */
		bytes = Message.intToBytes(plan.sizeOf());
		index = Message.addBytes(index, ret, bytes);
		
		/* Marshall the routing plan */
		bytes = plan.marshall();
		index = Message.addBytes(index, ret, bytes);
	
		/* Marshall the tracker */
		bytes = Message.intToBytes(tracker);
		index = Message.addBytes(index, ret, bytes);
	
		return ret;
	}

	public void unmarshall(byte[] data){
		
		/* Unmarshall the type */
		int index = Message.INT;
		
		/* Unmarshall the size of the Routing Plan */
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int size = Message.bytesToInt(bytes);
		index += Message.INT;

		/* Unmarshall the Routing Plan */
		bytes = Message.getBytes(index, size, data);
		plan = new RoutingPlan(bytes);
		index += size;

		/* Unmarshall the tracker */
		bytes = Message.getBytes(index, Message.INT, data);
		tracker = Message.bytesToInt(bytes);	
	}
}
