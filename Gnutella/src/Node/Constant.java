package Node;

import java.util.HashMap;

import Utils.Neighbor;

public class Constant {
	
	final static byte [] LOCALHOST_BYTE_ADDRESS = {(byte) 127, (byte) 0, (byte) 0, (byte) 1};
	
	final static int MAX_NBOR_COUNT = 5; 
	
	final static HashMap<String, Integer> MESSAGE_TYPE  = new HashMap<>();
	static {
		MESSAGE_TYPE.put("PING", 1);
		MESSAGE_TYPE.put("PONG", 2);
		MESSAGE_TYPE.put("QUERY", 3);
		MESSAGE_TYPE.put("QHIT", 4);
	}
	
	
	final static Neighbor[] KNOWN_HOSTS = {
		new Neighbor(LOCALHOST_BYTE_ADDRESS, (short) 9899),
		new Neighbor(LOCALHOST_BYTE_ADDRESS, (short) 9900),
	}; 
	
	final static String GNUTELLA_HOME = "/home/vikramaditya/Gnutella/";
}
