package studiplayer.audio;

import static org.junit.Assert.assertTrue;

public class NotPlayableException extends Exception{
	private String message;
	
	public NotPlayableException(String pathname, String msg){
		super(pathname + " " + msg);
	}
	//TODO assertTrue("toString von NotPlayableException ungenuegend", ts.contains("abc-path")

	public NotPlayableException(String pathname, Throwable t){
		super("File with path: " + pathname + " could not be opened. Throwable says: " + t.getMessage());
	}
	public NotPlayableException(String pathname, String msg, Throwable t){
		super("File with path: " + pathname + " could not be opened. Message: " + msg,t);
	}
}
