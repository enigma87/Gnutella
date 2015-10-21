package Utils;
import java.io.Serializable;


public class Neighbor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	byte [] ip;
	short port ;
	int fileCount;
	int shareSize;
	int speed;
	
	public Neighbor (byte [] ip, short port) {
		this.setIp(ip);
		this.setPort(port);
		this.setFileCount(0);
		this.setShareSize(0);
	}

	public byte[] getIp() {
		return ip;
	}

	public void setIp(byte[] ip) {
		this.ip = ip;
	}

	public short getPort() {
		return port;
	}

	public void setPort(short port) {
		this.port = port;
	}

	public int getFileCount() {
		return fileCount;
	}

	public void setFileCount(int fileCount) {
		this.fileCount = fileCount;
	}

	public int getShareSize() {
		return shareSize;
	}

	public void setShareSize(int shareSize) {
		this.shareSize = shareSize;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	
}
