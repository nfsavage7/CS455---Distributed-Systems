package cdn.wireformats;

public class ChatMessage extends Message{

	private String payload;
	private final int type = Message.CHAT;

	public ChatMessage(String p){
		payload = p;
	}

	public ChatMessage(byte[] data){
		unmarshall(data);
	}

	public String getPayload(){
		return payload;
	}

	public int getType(){
		return type;
	}

	public byte[] marshall(){
		byte[] ret = new byte[Message.INT + Message.INT + payload.length()];
		
		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		bytes = Message.intToBytes(payload.length());
		index = Message.addBytes(Message.INT, ret, bytes);

		bytes = payload.getBytes();
		index = Message.addBytes(index, ret, bytes);

		return ret;
	}

	public void unmarshall(byte[] data){
		byte[] bytes = Message.getBytes(Message.INT, Message.INT, data);
		int len = Message.bytesToInt(bytes);

		bytes = Message.getBytes(Message.INT + Message.INT, len, data);
		payload = new String(bytes);
	}

}
