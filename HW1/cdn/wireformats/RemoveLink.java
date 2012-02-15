package cdn.wireformats;

public class RemoveLink extends Message{

	private final int type = Message.REMOVE_LINK;
	private String ID;

	public RemoveLink(String id){
		ID = id;
	}

	public RemoveLink(byte[] data){
		unmarshall(data);
	}
	
	public String getID(){
		return ID;
	}

	public int getType(){
		return type;
	}

	public byte[] marshall(){
		byte[] ret = new byte[Message.INT + Message.INT + ID.length()];

		byte[] bytes = Message.intToBytes(type);
		int index = Message.addBytes(0, ret, bytes);

		bytes = Message.intToBytes(ID.length());
		index = Message.addBytes(index, ret, bytes);

		bytes = ID.getBytes();
		index = Message.addBytes(index, ret, bytes);
		
		return ret;
	}

	public void unmarshall(byte[] data){
		int index = Message.INT;

		byte[] bytes = Message.getBytes(index, Message.INT, data);
		int len = Message.bytesToInt(bytes);
		index += Message.INT;

		ID = new String(Message.getBytes(index, len, data));
	}
}
