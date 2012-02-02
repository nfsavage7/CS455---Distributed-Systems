package cdn.communications;

import cdn.wireformats.*;
import cdn.node.Router;

public class RouterReceiveThread extends Thread{

	private Router router;
	private Link link;
	
	public RouterReceiveThread(Router r, Link l){
		router = r;
		link = l;
		System.out.println("At least we spawned one xP");
	}

	public void run(){
		System.out.println("Made it here Sir");
		while(true){
			if(link.hasMessage()){
				link.receiveData();
				while(!link.hasBytes){}
				byte[] msg = link.getBytesReceived();
				int type = Message.bytesToInt(Message.getBytes(0, 4, msg));
				System.out.println("Type: " + type);
				switch(type){
					case Message.ROUTER_INFO:
						router.gotRouterInfo(new RouterInfo(msg));
						break;
					case Message.CHAT:
						router.gotChatMessage(new ChatMessage(msg));
						break;
					case Message.REGISTER_RESPONSE:
						router.gotRegisterResponse(new RegisterResponse(msg));
						break;
					default:
						System.out.println("Router::recvMessage: Type unsupported.");
				}

			} else {
				try{
					Thread.sleep(50);
				} catch (InterruptedException e){
					continue;
				}
			}
		}
	}
}
