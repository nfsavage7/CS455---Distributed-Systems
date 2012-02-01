package cdn.wireformat;

public class RegisterRequest extends Message{
	
	private final int type = Message.REGISTER_REQUEST;
	private String IP;
	private int port;
	private String ID;

	public RegisterRequest(String ip, int p, String id){
		IP = ip;
		port = p;
		ID = ID;
	}

	public byte[] marshall(){

	}

	public void unmarshall(byte[] data){
		
	}
}
