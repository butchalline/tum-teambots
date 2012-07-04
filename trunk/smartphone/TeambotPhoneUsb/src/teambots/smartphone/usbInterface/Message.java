package teambots.smartphone.usbInterface;

import java.util.HashMap;

public class Message {

	public enum Type
	{
	    Data ("data", 1, 2),
	    Info ("info", 2, 3);

	    public final String name;
	    public final int id;
	    public final int packageLength;
	    Type(String name, int id, int packageLength) {
	        this.name = name;
	        this.id = id;
	        this.packageLength = packageLength;
	    }
	}
	
	public static HashMap<Integer, Type> IntIdToType = new HashMap<Integer, Type>(30);
    static {
    	IntIdToType.put(1, Type.Data);
    	IntIdToType.put(2, Type.Info);
    }
    
    public Type type;
    public byte[] data;
    
    
    public Message()
    {
    	
    }
    
    public Message(Type type)
    {
    	this.type = type;
    }
    
    public Message(Type type, byte[] data)
    {
    	this.type = type;
    	this.data = data;
    }
}
