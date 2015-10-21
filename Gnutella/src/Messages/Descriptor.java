package Messages;

import Utils.Conversion;

public class Descriptor {
	byte [] ID = new byte[16];
	short payloadType = -1;
	short TTL = -1;
	short hops = -1;
	int payloadLength = -1;
	

	public static long idgen = 0;
	
	public Descriptor(byte[] fwdDescID, byte [] ip, short port, short type, short ttl, short hops, int length) {
		
		byte[] id = new byte[16];
		
		if (null == fwdDescID) {
			System.arraycopy(ip, 0, id, 0, 4);
			System.arraycopy(Conversion.shortToBytes(port), 0, id, 6, 2);
			System.arraycopy(Conversion.longToBytes(idgen), 0, id, 8, 8);
			idgen+=1;
		
		}
		else{
			System.arraycopy(fwdDescID, 0, id, 0, 16);
		}
		
		this.setID(id); // get the latest id from stored list of descIDs + 1
		this.setPayloadType(type);
		this.setTTL(ttl);
		this.setHops(hops);
		this.setPayloadLength(length);
		
	}
	
	
	public byte[] message () {
		byte message[] = new byte[26];
		System.arraycopy(ID, 0, message, 0, 16);
		System.arraycopy(Conversion.shortToBytes(payloadType), 0, message, 16, 2);
		System.arraycopy(Conversion.shortToBytes(TTL), 0, message, 18, 2);
		System.arraycopy(Conversion.shortToBytes(hops), 0, message, 20, 2);
		System.arraycopy(Conversion.intToBytes(payloadLength), 0, message, 22, 4);

		return message;
	}

	public byte[] getID() {
		return ID;
	}


	public void setID(byte[] iD) {
		ID = iD;
	}


	public short getPayloadType() {
		return payloadType;
	}


	public void setPayloadType(short type) {
		this.payloadType = type;
	}


	public short getTTL() {
		return TTL;
	}


	public void setTTL(short ttl2) {
		TTL = ttl2;
	}


	public short getHops() {
		return hops;
	}


	public void setHops(short hops2) {
		this.hops = hops2;
	}


	public int getPayloadLength() {
		return payloadLength;
	}


	public void setPayloadLength(int length) {
		this.payloadLength = length;
	}
	
	
}
