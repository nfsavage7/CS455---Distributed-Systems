package cdn.wireformats;

import java.net.*;

public class LinkInfo extends Message {

	private final int type = LINK_INFO;
	private RouterInfo routerOne;
	private RouterInfo routerTwo;
	private int weight;

	public LinkInfo(RouterInfo rOne, RouterInfo rTwo, int w){
		routerOne = rOne;
		routerTwo = rTwo;
		weight = w;
	}

	public LinkInfo(byte[] data){
		unmarshall(data);
	}
	
	public RouterInfo hasRouter(RouterInfo r){
		if(r.getID().equals(routerOne.getID())){
			return routerTwo;
		}
		if(r.getID().equals(routerTwo.getID())){
			return routerOne;
		}
		return null;
	}

	public RouterInfo getRouterOne(){
		return routerOne;
	}
	
	public RouterInfo getRouterTwo(){
		return routerTwo;
	}	

	public int getWeight(){
		return weight;
	}

	public int getType(){
		return type;
	}

	public int sizeOf(){
		return Message.INT + routerOne.sizeOf() + Message.INT + routerTwo.sizeOf() + Message.INT;
	}
	
	public String toString(){
		return routerOne.getID() + " " + routerTwo.getID() + " " + weight + " " + routerOne.getHostName() + ":" + routerOne.getPort() + " " + routerTwo.getHostName() + ":" + routerTwo.getPort();
}

	//DO NOT MARSHALL TYPE!!!!!!
	public byte[] marshall(){
		byte[] ret = new byte[Message.INT + sizeOf()];

		//Marshall the size of the LinkInfo
		byte[] bytes = Message.intToBytes(sizeOf());
		int index = Message.addBytes(0, ret, bytes);
		
		//Marshall the size of the first router info
		bytes = Message.intToBytes(routerOne.sizeOf());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the first router info
		bytes = routerOne.marshall();
		index = Message.addBytes(index, ret, bytes);

		//Marshall the size of the seccond router info
		bytes = Message.intToBytes(routerTwo.sizeOf());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the seccond router info
		bytes = routerTwo.marshall();
		index = Message.addBytes(index, ret, bytes);
	
		//Marshall the weight
		bytes = Message.intToBytes(weight);
		index = Message.addBytes(index, ret, bytes);
		return ret;
		
	}
	
	//NO UNMARSHALLING TYPE BECAUSE I'M NOT SENDING IT!!!!!!!!!
	public void unmarshall(byte[] data){
		//This skips my size
		int index = 0;

		//Unmarshall the size of the the first router info
		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int size = Message.bytesToInt(bytes);
		index += Message.INT;
	
		//Unmarshall the first router info
		bytes = Message.getBytes(index, size, data);
		routerOne = new RouterInfo(bytes);
		index += size;

		//Unmarshall the size of the the second router info
		bytes = Message.getBytes(index, Message.INT, data);
		size = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall the second router info
		bytes = Message.getBytes(index, size, data);
		routerTwo = new RouterInfo(bytes);
		index += size;

		//Unmarshall the weight
		bytes = Message.getBytes(index, Message.INT, data);
		weight = Message.bytesToInt(bytes);
		
	}
}
