package cdn.communications;

import cdn.wireformats.*;
import cdn.node.Router;

public class RouterReceiveThread extends Thread{

	private Router router;
	private Link link;
	
	public RouterReceiveThread(Router r, Link l){
		router = r;
		link = l;
	}

	public void run(){
		while(true){
			if(link.hasMessage()){
				link.receiveData();
				while(!link.hasBytes){}
				byte[] msg = link.getBytesReceived();
				byte[] type = Message.getBytes(0, 4, msg);
				if(Message.bytesToInt(type) == Message.ROUTER_INFO){
					router.gotRouterInfo(new RouterInfo(msg));
				} else if (Message.bytesToInt(type) == Message.CHAT){
					router.gotChatMessage(new ChatMessage(msg));
				} else {
					System.out.print(Message.bytesToInt(type));
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
