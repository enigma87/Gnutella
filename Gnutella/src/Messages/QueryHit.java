package Messages;

import Utils.Conversion;

public class QueryHit {
	Descriptor qhitDesc;
	short resultCount;
	short sender_port;
	byte[] sender_ip;
	short SPEED;
	String resultSet;
	
	
	public QueryHit(byte [] fwdMsgID, byte[] ip, short port, short type, short ttl, short hops, int length, short count, short speed, String resultset) {
		qhitDesc = new Descriptor(fwdMsgID, ip, port, type, ttl, hops, length);
		this.setResultCount(count);
		this.setSender_port(port); 
		this.setSender_ip(ip);
		this.setSPEED(speed);
		this.setResultSet(resultset);
		
	}

	public byte[] message() {
		// fill msg with appropriate content, keep result set as a tab separated content
		int msglen = 26 + 2 + 2 + 4 + 2 + this.getResultSet().getBytes().length;  
		byte [] msg = new byte[msglen];
		System.arraycopy(this.qhitDesc.message(), 0, msg,0, 26);
		System.arraycopy(Conversion.shortToBytes(this.getResultCount()), 0, msg, 26, 2);
		System.arraycopy(Conversion.shortToBytes(this.getSender_port()), 0, msg, 28, 2);
		System.arraycopy(this.getSender_ip(), 0, msg, 30, 4);
		System.arraycopy(Conversion.shortToBytes(this.getSPEED()), 0, msg, 34, 2);
		System.arraycopy(this.getResultSet().getBytes(), 0, msg, 36, this.getResultSet().getBytes().length);
				
		return msg;
	}
	
	public Descriptor getQhitDesc() {
		return qhitDesc;
	}

	public void setQhitDesc(Descriptor qhitDesc) {
		this.qhitDesc = qhitDesc;
	}

	
	public short getSPEED() {
		return SPEED;
	}

	public void setSPEED(short sPEED) {
		SPEED = sPEED;
	}

	public String getResultSet() {
		return resultSet;
	}

	public void setResultSet(String resultSet) {
		this.resultSet = resultSet;
	}



	public static QueryHit QHitObjeFromMsg(byte []  msg) {
		byte [] ip = new byte[4];
		byte [] port = new byte[2];
		byte[] descID = new byte [16];
		byte [] type = new byte[2];
		byte [] ttl = new byte[2];
		byte [] hops = new byte[2];
		byte []length = new byte[4];
		byte [] hitCount = new byte[2];
		byte [] hitPort = new byte[2];
		byte[] hitIP = new byte[4];
		byte[] speed = new byte[2];
		byte [] hitResultSet = new byte [msg.length - 36];
		
		
		System.arraycopy(msg, 0,  ip, 0, 4);
		System.arraycopy(msg, 6, port, 0, 2);
		System.arraycopy(msg, 0, descID, 0, 16);
		System.arraycopy(msg, 16, type, 0, 2);
		System.arraycopy(msg, 18, ttl, 0, 2);
		System.arraycopy(msg, 20, hops, 0, 2);
		System.arraycopy(msg, 24,length, 0, 4);
		System.arraycopy(msg, 26, hitCount, 0, 2);
		System.arraycopy(msg, 28, hitPort, 0, 2);
		System.arraycopy(msg, 30,hitIP, 0, 4);
		System.arraycopy(msg, 34,speed, 0, 2);
		
		System.arraycopy(msg, 36, hitResultSet, 0, msg.length - 36);
		
		return new QueryHit(descID, hitIP, Conversion.bytesToShort(hitPort), Conversion.bytesToShort(type), Conversion.bytesToShort(ttl), Conversion.bytesToShort(hops), Conversion.bytesToInt(length), Conversion.bytesToShort(hitCount), Conversion.bytesToShort(speed), new String(hitResultSet));
	}

	public short getResultCount() {
		return resultCount;
	}

	public void setResultCount(short resultCount) {
		this.resultCount = resultCount;
	}

	public short getSender_port() {
		return sender_port;
	}

	public void setSender_port(short port) {
		this.sender_port = port;
	}

	public byte[] getSender_ip() {
		return sender_ip;
	}

	public void setSender_ip(byte[] sender_ip) {
		this.sender_ip = sender_ip;
	}	
}	
	
	
	