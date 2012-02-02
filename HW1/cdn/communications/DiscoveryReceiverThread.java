package cdn.communications;

import java.util.*;

import cdn.wireformats.*;
import cdn.node.Discovery;

public class DiscoveryReceiverThread extends Thread{
	
	private ArrayList<Link> links;
	private Discovery discovery;
	
	public DiscoveryReceiverThread(ArrayList<Link> l, Discovery d){
		links = l;
		discovery = d;
	}

	public void run(){
		while(true){
			for(int i = 0; i < links.size(); i++){
				Link l = links.get(i);
				if(l.hasMessage()){
					l.receiveData();
					byte[] msg = l.getBytesReceived();
					int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
					switch(type){
						case Message.REGISTER_REQUEST:
							discovery.registerRouter( new RegisterRequest(msg), l);
							break;
						default:
							System.out.println("Message type unsupported");
							break;
					}
				}
			links = discovery.getLinks();
			}
			if(links.size() == 0){
				links = discovery.getLinks();
			}
		}
	}
}
