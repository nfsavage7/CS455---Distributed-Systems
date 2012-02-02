package cdn.wireformats;

public class RegisterResponse extends Message{

	private final int type = Message.REGISTER_RESPONSE;
	private byte status;
	private String info;

	public RegisterResponse(byte s, String i){
		status = s;
		info = i;
	}
	
	public RegisterResponse(byte[] data){
		unmarshall(data);
	}
	
	public int getType(){
		return type;
	}

	public String getInfo(){
		return info;
	}
	
	public byte[] marshall(){
		byte[] ret = new byte[Message.INT + 1 + Message.INT + info.length()];
		//Marshall the type
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		//Marshall the status
		bytes = new byte[1];
		bytes[0] = status;
		index = Message.addBytes(index, ret, bytes);

		//Marshall the len of the info
		bytes = Message.intToBytes(info.length());
		index = Message.addBytes(index, ret, bytes);

		//Marshall the info
		bytes = info.getBytes();
		index = Message.addBytes(index, ret, bytes);

		return ret;
	}

	public void unmarshall(byte[] data){
		//Skip the type
		int index = Message.INT;

		//Unmarshall the status
		byte[] bytes = Message.getBytes(index, 1, data);
		status = bytes[0];
		index++;

		//Unmarshall the len of the info
		bytes = Message.getBytes(index, Message.INT, data);
		int len = Message.bytesToInt(bytes);
		index += Message.INT;

		//Unmarshall the info
		bytes = Message.getBytes(index, len, data);
		info = new String(bytes);
	}
}
