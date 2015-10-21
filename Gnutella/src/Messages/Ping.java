package Messages;

import Utils.Conversion;



public class Ping {
	public Descriptor pingDesc;
	public short sender_node_port;
	public byte[] sender_node_ip;

	
	public Ping(byte [] fwdMsgID, byte[] ip, short port, short type, short ttl, short hops, int length) {
		pingDesc = new Descriptor(fwdMsgID,ip, port, type, ttl, hops, length);
		this.setSender_node_port(port);
		this.setSender_node_ip(ip);

	}

	public byte[] message () {
		byte [] msg = new byte[26 + 2 + 4 ];
		System.arraycopy(this.pingDesc.message(), 0, msg, 0, 26);
		System.arraycopy(Conversion.shortToBytes(this.getSender_node_port()), 0, msg, 26, 2);
		System.arraycopy(this.getSender_node_ip(), 0, msg, 28, 4);
	
		return msg;
	}
	
	public static Ping PingObjeFromMsg(byte []  msg) {
		byte [] ip = new byte[4];
		byte [] port =  new byte[2];
		byte [] descID = new byte[16];
		byte [] type = new byte[2];
		byte [] ttl = new byte[2];
		byte [] hops = new byte[2];
		byte []length = new byte[4];
		byte [] node_port = new byte[2];
		byte [] node_ip = new byte[4];
		
		System.arraycopy(msg, 0,  ip, 0, 4);
		System.arraycopy(msg, 6, port, 0, 2);
		System.arraycopy(msg, 0, descID, 0, 16);
		System.arraycopy(msg, 16, type, 0, 2);
		System.arraycopy(msg, 18, ttl, 0, 2);
		System.arraycopy(msg, 20, hops, 0, 2);
		System.arraycopy(msg, 22,length, 0, 4);
		System.arraycopy(msg, 26, node_port, 0, 2);
		System.arraycopy(msg, 28, node_ip, 0, 4);
		
		return new Ping(descID, node_ip, Conversion.bytesToShort(node_port), Conversion.bytesToShort(type), Conversion.bytesToShort(ttl), Conversion.bytesToShort(hops), Conversion.bytesToInt(length));
	}

	public Descriptor getPingDesc() {
		return pingDesc;
	}

	public void setPingDesc(Descriptor pingDesc) {
		this.pingDesc = pingDesc;
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
