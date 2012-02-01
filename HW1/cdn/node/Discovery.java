package cdn.node;

import java.util.*;

import cdn.commection.*;

public class Discovery{

	private int port;
	HashMap<String, Link> routers = new HashMap<String, Link>();
	
	public Discovery(int p){
		port = p;
	}

}
