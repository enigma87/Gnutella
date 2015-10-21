package Messages;

import Utils.Conversion;

public class Query {
	
	Descriptor queryDesc;
	short sender_node_port;
	byte [] sender_node_ip;
	
	short  speed;
	byte[] searchStr;
	
	
	public Query(byte[] fwdMsgID, byte[] ip, short port, short type, short ttl, short hops, int length, short speed, byte[] searchStr) {
		queryDesc = new Descriptor(fwdMsgID, ip, port, type, ttl, hops, length);
		this.setSearchStr(searchStr);
		this.setSpeed(speed);
		this.setSender_node_ip(ip);
		this.setSender_node_port(port);
	}
	
	public byte[] message () {
		int msglen = 26 + 2 + 4 + 2+ this.getSearchStr().length;
	
		byte [] msg = new byte[msglen];
		System.arraycopy(this.queryDesc.message(), 0, msg,0, 26);
		System.arraycopy(Conversion.shortToBytes(this.getSender_node_port()), 0, msg, 26, 2);
		System.arraycopy(this.getSender_node_ip(), 0, msg, 28, 4);
		System.arraycopy(Conversion.shortToBytes(this.getSpeed()), 0, msg, 32, 2);
		System.arraycopy(this.getSearchStr(), 0, msg, 34, this.getSearchStr().length);
	
		return msg;
	}
	
	
	public short getSpeed() {
		return speed;
	}
	public void setSpeed(short speed) {
		this.speed = speed;
	}

	
	public static Query QueryObjeFromMsg(byte []  msg) {
		
	//	System.out.println(msg.length + "is the culprit");
		
		//if (true) return null;
		
		byte [] ip = new byte[4];
		byte [] port = new byte[2];
		byte [] descID = new byte[16];
		byte [] type = new byte[2];
		byte [] ttl = new byte[2];
		byte [] hops = new byte[2];
		byte []length = new byte[4];
		byte [] sender_port = new byte[2];
		byte [] sender_ip = new byte[4];
		byte [] speed = new byte[2];
		byte [] searchStr = new byte[msg.length - 34];
		
		System.arraycopy(msg, 0,  ip, 0, 4);
		System.arraycopy(msg, 6, port, 0, 2);
		System.arraycopy(msg, 0, descID, 0, 16);
		System.arraycopy(msg, 16, type, 0, 2);
		System.arraycopy(msg, 18, ttl, 0, 2);
		System.arraycopy(msg, 20, hops, 0, 2);
		System.arraycopy(msg, 22,length, 0, 4);
		System.arraycopy(msg, 26, sender_port, 0, 2);
		System.arraycopy(msg, 28, sender_ip, 0, 4);
		System.arraycopy(msg, 32, speed, 0, 2);
		
		
		System.arraycopy(msg, 34, searchStr, 0, msg.length - 34);
	
		return new Query(descID, sender_ip, Conversion.bytesToShort(sender_port), Conversion.bytesToShort(type), Conversion.bytesToShort(ttl), Conversion.bytesToShort(hops), Conversion.bytesToInt(length), Conversion.bytesToShort(speed), searchStr);
	}

	public Descriptor getQueryDesc() {
		return queryDesc;
	}

	public void setQueryDesc(Descriptor queryDesc) {
		this.queryDesc = queryDesc;
	}

	public short getSender_node_port() {
		return sender_node_port;
	}

	public void setSender_node_port(short port) {
		this.sender_node_port = port;
	}

	public byte[] getSender_node_ip() {
		return sender_node_ip;
	}

	public void setSender_node_ip(byte[] sender_node_ip) {
		this.sender_node_ip = sender_node_ip;
	}

	public byte[] getSearchStr() {
		return searchStr;
	}

	public void setSearchStr(byte[] searchStr) {
		this.searchStr = searchStr;
	}
}
