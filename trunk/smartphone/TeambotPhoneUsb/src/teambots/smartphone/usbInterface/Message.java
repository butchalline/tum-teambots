package teambots.smartphone.usbInterface;

import java.util.HashMap;

public class Message {

	public enum Type
	{
	    Data ("data", 1),
	    Info ("info", 2);

	    public final String name;
	    public final int id;
	    Type(String name, int id) {
	        this.name = name;
	        this.id = id;
	    }
	}
	
	public static HashMap<Integer, Type> IntToType = new HashMap<Integer, Type>(30);
    static {
    	IntToType.put(1, Type.Data);
    	IntToType.put(2, Type.Info);
    }
    
    public Type type;
    public byte[] data;
    
    
    public Message()
    {
    	
    }
    
    public Message(Type type, byte[] data)
    {
    	this.type = type;
    	this.data = data;
    }
}
