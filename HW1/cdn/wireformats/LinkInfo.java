package cdn.wireformats;

import java.net.*;

public class LinkInfo{
	private RouterInfo routerOne;
	private RouterInfo routerTwo;
	private int weight;

	public LinkInfo(InetSocketAddress local, InetSocketAddress remote){
	//	routerOne = new RouterInfo(local.getHostName(), local.getPort());
	//	routerTwo = new RouterInfo(remote.getHostName(), remote.getPort());
		weight = 0;
	}

}
