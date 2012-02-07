package cdn.communications;

/* Java imports */
import java.util.*;

/* Local imports */
import cdn.wireformats.*;
import cdn.node.Discovery;

public class DiscoveryReceiverThread extends Thread{
	
	/* Member vaiables */
	private Discovery discovery;
	private ArrayList<Link> links;
	private boolean stop = false;
	private boolean finished = false;
	
	/* Constructors */
	public DiscoveryReceiverThread(Discovery d, ArrayList<Link> l){
		discovery = d;
		links = l;
	}
	
	public void halt(){
		stop = true;
		while(!finished){}
		System.out.println("Done");
	}

	/* Receiver method */
	public void run(){
		while(!stop){
			for(int i = 0; i < links.size(); i++){
				Link l = links.get(i);
				if(l == null){
					//TODO take me out
					System.out.println("Null thing");
					break;
				}
				if(l.hasMessage()){
					l.receiveData();
					byte[] msg = l.getBytesReceived();
					System.out.println("Got " + msg.length);
					int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
					switch(type){
						case Message.REGISTER_REQUEST:
							discovery.registerRouter( new RegisterRequest(msg), l);
							break;
						case Message.DEREGISTER_REQUEST:
							discovery.deregisterRouter( new DeregisterRequest(msg), l);
							links.remove(links.indexOf(l));
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
		finished = true;
	}
}
