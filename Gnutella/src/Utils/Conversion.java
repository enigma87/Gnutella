package Utils;

import java.nio.ByteBuffer;

public class Conversion {

	public static byte[] longToBytes(long x) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
	    buffer.putLong(x);
	    return buffer.array();
	}
	
	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE/8);
		buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
	
	public static byte[] intToBytes(int x) {
	    ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE/8);
	    buffer.putInt(x);
	    return buffer.array();
	}
	
	public static int bytesToInt(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Integer.SIZE/8);
		buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getInt();
    }
	
	public static byte[] shortToBytes(short x) {
		ByteBuffer buffer = ByteBuffer.allocate(Short.SIZE/8);
	    buffer.putShort(x);
	    return buffer.array();
	}
	
	public static short bytesToShort(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Short.SIZE/8);
		buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getShort();   
    }	
	
	public static String getStringIP(byte [] ip) {
		Integer b1, b2, b3, b4;
		b1 = (int) ip[0];
		b2 = (int) ip[1];
		b3 = (int) ip[2];
		b4 = (int) ip[3];
		
		String ip_str = b1.toString() + "." + b2.toString() + "." + b3.toString() + "." + b4.toString();
		return ip_str;
	}
	
/*
 * Leaving these here, for the convenience of checking these methods
 * These methods are extensively thrown around and therefore need as much testing as possible
 * Very intricate in that they convert bytes to meaningful data for a programmer
 * 
public static void main (String args[]) {
	
	byte [] recvData;
	
	int integer = 127;
	short  shortint = 63;
	long longint = 255;
	
	recvData = intToBytes(integer);
	integer = bytesToInt(recvData);
	recvData = intToBytes(integer);
	
	StringBuilder sb = new StringBuilder(recvData.length * Byte.SIZE);
    for( int i = 0; i < Byte.SIZE * recvData.length; i++ )
        sb.append((recvData[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
    System.out.println(sb.toString());
    
	recvData = longToBytes(longint);
	longint= bytesToLong(recvData);
	recvData = longToBytes(longint);
	
	sb = new StringBuilder(recvData.length * Byte.SIZE);
    for( int i = 0; i < Byte.SIZE * recvData.length; i++ )
        sb.append((recvData[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
    System.out.println(sb.toString());
    
	
	recvData = shortToBytes(shortint);
	shortint = bytesToShort(recvData);
	recvData = shortToBytes(shortint);
	System.arraycopy(shortToBytes(shortint), 0, recvData, 0, 2);
	
	sb = new StringBuilder(recvData.length * Byte.SIZE);
    for( int i = 0; i < Byte.SIZE * recvData.length; i++ )
        sb.append((recvData[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
    System.out.println(sb.toString());
	
System.arraycopy(shortToBytes(shortint), 0, recvData, 0, 2);
System.out.println(recvData[1]);
    
}
*/	
}