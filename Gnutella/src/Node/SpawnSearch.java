package Node;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Messages.Query;
import Utils.Conversion;
import Utils.Neighbor;

public class SpawnSearch extends Thread{

	public static Node myNode;
	private BufferedReader reader;
	public static HashMap<String, Neighbor> KNOWN_RESULTS = new HashMap<>();
	
	public void run () {
		
		System.out.println("---------------GNUTELLA-"+ myNode.getID() + "---------------------");
		
		while (true) {
			
			reader = new BufferedReader(new InputStreamReader(System.in));

			System.out.println("key in '1' for Search, '2' for Download and hit return");
			System.out.println(">>");
			
			try {
				String op = "0";
				
				try {
					 op = reader.readLine();
					 Integer.parseInt(op);
				}
				catch (NumberFormatException e) {
					System.out.println("please enter either '1' or '2' and hit return!");
					continue;
				}
				
			if (1 == (int) Integer.parseInt(op)) {
				System.out.println("enter search string : ");
				String searchStr = reader.readLine();
			
				// create Query for node myNode, and forward to neighbors
				System.out.println("SPAWNED SEARCH with string \"" + searchStr + "\" ");
		
				Query queryNeigh;
			
				queryNeigh = new Query(null, myNode.getMy_ip(), myNode.getMy_port(), (short) 3, (short) 5, (short) 0, (int) 0, (short) myNode.getMySpeed() , searchStr.getBytes("UTF-8"));
				DatagramSocket ds = new DatagramSocket(0);
				//System.out.println("queryNeigh size" +  queryNeigh.message().length);
				
				for(Neighbor n : myNode.getMy_neighbor_list()) {
					DatagramPacket dp = new DatagramPacket(queryNeigh.message(), queryNeigh.message().length, InetAddress.getByAddress(n.getIp()), (int) n.getPort());
					ds.send(dp);
			
				}
				ds.close();
			}
			
			if (2 == Integer.parseInt(op)) {
				ArrayList<String> filenameList = new ArrayList<String>();
				ArrayList<Neighbor> neighborList = new ArrayList<Neighbor>();
				
				int i=0;
				for (Map.Entry<String, Neighbor> known : KNOWN_RESULTS.entrySet()) {
					i++;
					String splitThis = new String(known.getKey()).toString();
					String [] temp = splitThis.split("\\t");
					filenameList.add(temp[1]);
					neighborList.add(known.getValue());
					
					System.out.println(i + " > " + known.getKey());
				}
				if (filenameList.size() == 0) {
					System.out.println("Files available from peers not known. Spawn a new Search!");
					continue;
				} 
				System.out.println("Above files are known, pick the integer index and hit enter");
				
				int downIndex = (int) Integer.parseInt(reader.readLine());
				
				String downFile = filenameList.get(downIndex-1);
				Neighbor n = neighborList.get(downIndex-1); 
				
				System.out.println("attempting download of " + filenameList.get(downIndex-1) + " from " + Conversion.getStringIP(neighborList.get(downIndex-1).getIp()) + "." +neighborList.get(downIndex-1).getPort() +"\n");
				//Thread.sleep(1000);
				
				// MAKE A GET CALL				
				Socket sock = new Socket(InetAddress.getByAddress(n.getIp()), n.getPort());
			
				DataInputStream sockIn = new DataInputStream(sock.getInputStream());
				DataOutputStream sockOut = new DataOutputStream(sock.getOutputStream());
				
				String speedStr = new Integer(myNode.getMySpeed()).toString();
				
				for (i=0; i < (3-speedStr.length()); i++) {
					speedStr = "0" + speedStr;
				}
				
				System.out.println("speedlimit" + speedStr);
				String getQry = "GET " + speedStr + " " + downFile;
				char get[] = getQry.toCharArray();
				
				byte [] sendData = new byte[get.length];
				int j=0;
				for(char c : get){
					sendData[j] = (byte) (int)  c ;
					j++;
				}
				sockOut.write(sendData);
				sockOut.flush();
				
				
				byte[] recvData = new byte[5];				
				sockIn.read(recvData);
				String push = new String(recvData, Charset.forName("UTF-8"));
				if (push.equals("PUSH ")) {
					recvData = new byte[sockIn.available()];
					sockIn.read(recvData);
					System.out.println("PUT " + downFile  );
					 	
						
					    InputStream is = sock.getInputStream();
					    int speed = 500;
					    if (n.getSpeed() > 0) speed = n.getSpeed();
					    System.out.println("download speed : " + speed + "B per cycle");
					    byte[] recvbuff = new byte[speed];
					    FileOutputStream fout = new FileOutputStream(myNode.getMyGnutellaDir() + myNode.getID()+ ".share.dir/" + downFile);
					    
					    InputStream inStream = sock.getInputStream();
					    
					    while((inStream.read(recvbuff)) > 0) {
					    	fout.write(recvbuff);
					    }
					    fout.close();is.close();
				}
				sock.close();
			}
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}


}
