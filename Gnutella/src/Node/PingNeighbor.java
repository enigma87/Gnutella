package Node;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import Messages.Ping;
import Utils.Neighbor;

public class PingNeighbor extends Thread {
	
	public static Node myNode;
	
	public void run() {
	
		while (true) {
			myNode.logStdout.info("pinging neighbor every 120 seconds!");
			try {
				Thread.sleep(15000);
				Ping pingNeigh = new Ping(null, myNode.getMy_ip(), myNode.getMy_port(), (short) 1, (short) 5, (short) 0, (int) 0);
				DatagramSocket ds = new DatagramSocket(0);
			
				for(Neighbor n : myNode.getMy_neighbor_list()) {
					DatagramPacket dp = new DatagramPacket(pingNeigh.message(), pingNeigh.message().length, InetAddress.getByAddress(n.getIp()), (int) n.getPort());
					ds.send(dp);
				
				}
				ds.close();
			
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
