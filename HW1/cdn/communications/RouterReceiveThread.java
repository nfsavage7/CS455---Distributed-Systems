package cdn.communications;

/* Java imports */
import java.util.*;

/* Local imports */
import cdn.wireformats.*;
import cdn.node.Router;

public class RouterReceiveThread extends Thread{

	/* Member variables */
	private Router router;
	private ArrayList<Link> links;
	
	/* Constructors */
	public RouterReceiveThread(Router r, ArrayList<Link> l){
		router = r;
		links = l;
	}

	/* Receiver method */
	public void run(){
		while(true){
			for(int i = 0; i < links.size(); i++){
				Link link = links.get(i);
				if(link == null){
					//TODO take me out
					System.out.println("I was right");
					break;
				}
				if(link.hasMessage()){
					link.receiveData();
					byte[] msg = link.getBytesReceived();
					int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
					switch(type){
						case Message.ROUTER_INFO:
							System.out.println("ROUTER_INFO");
							router.gotRouterInfo(new RouterInfo(msg));
							break;
						//TODO take this out once I have real packets to send
						case Message.CHAT:
							router.gotChatMessage(new ChatMessage(msg));
							break;
						case Message.REGISTER_RESPONSE:
							router.gotRegisterResponse(new RegisterResponse(msg));
							break;
						case Message.PEER_ROUTER_LIST:
							System.out.println("HIT HERE");
							router.connectToPeers(new PeerRouterList(msg));
							break;
						default:
							System.out.println("Router::recvMessage: Type unsupported.");
					}

				}
				links = router.getLinks();
			}
			if(links.size() == 0){
				links = router.getLinks();
			}
		}
	}
}
