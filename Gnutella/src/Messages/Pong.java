package Messages;

import Utils.Conversion;

public class Pong {
	public short sender_node_port;
	public byte[] sender_node_ip;
	public int file_count;
	public int total_size;
	
	public Descriptor pongDesc;
	
	public Pong(byte [] fwdMsgID, byte[] ip, short port, short type, short ttl, short hops, int length, int count, int size) {
		pongDesc = new Descriptor(fwdMsgID, ip, port, type, ttl, hops, length);
		this.setSender_node_ip(ip);
		this.setSender_node_port(port);
		this.setFile_count(count);
		this.setTotal_size(size);
	}
	
	public byte[] message () {
		byte [] msg = new byte[26 + 2 + 4 + 4 + 4];
		System.arraycopy(this.pongDesc.message(), 0, msg,0, 26);
		System.arraycopy(Conversion.shortToBytes(this.getSender_node_port()), 0, msg, 26, 2);
		System.arraycopy(this.getSender_node_ip(), 0, msg, 28, 4);
		System.arraycopy(Conversion.intToBytes(this.getFile_count()), 0, msg, 32, 4);
		System.arraycopy(Conversion.intToBytes(this.getTotal_size()), 0, msg, 36, 4);
		return msg;
	}

	

	public int getFile_count() {
		return file_count;
	}

	public void setFile_count(int count) {
		this.file_count = count;
	}

	public int getTotal_size() {
		return total_size;
	}

	public void setTotal_size(int total_size) {
		this.total_size = total_size;
	}

	public Descriptor getPongDesc() {
		return pongDesc;
	}

	public void setPongDesc(Descriptor pongDesc) {
		this.pongDesc = pongDesc;
	}
	
	
	public static Pong PongObjeFromMsg(byte []  msg) {
		byte [] ip = new byte[4];
		byte [] port = new byte[2];
		byte[] descID = new byte[16];
		byte [] type = new byte[2];
		byte [] ttl = new byte[2];
		byte [] hops = new byte[2];
		byte [] length = new byte[4];
		byte[] sender_port = new byte[2];
		byte [] sender_ip = new byte[4];
		byte [] count = new byte[4];
		byte[] size = new byte[4];
		
		System.arraycopy(msg, 0,  ip, 0, 4);
		System.arraycopy(msg, 6, port, 0, 2);
		System.arraycopy(msg, 0, descID, 0, 16);
		System.arraycopy(msg, 16, type, 0, 2);
		System.arraycopy(msg, 18, ttl, 0, 2);
		System.arraycopy(msg, 20, hops, 0, 2);
		System.arraycopy(msg, 22,length, 0, 4);
		System.arraycopy(msg, 26, sender_port, 0, 2);
		System.arraycopy(msg, 28, sender_ip, 0, 4);
		System.arraycopy(msg, 32, count, 0,  4);
		System.arraycopy( msg, 36, size, 0, 4);
				
		return new Pong(descID, sender_ip, Conversion.bytesToShort(sender_port), Conversion.bytesToShort(type), Conversion.bytesToShort(ttl), Conversion.bytesToShort(hops), Conversion.bytesToInt(length), Conversion.bytesToInt(count), Conversion.bytesToInt(size));
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
}
