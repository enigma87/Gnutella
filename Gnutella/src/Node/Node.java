package Node;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Messages.Descriptor;
import Messages.Ping;
import Messages.Pong;
import Messages.Query;
import Messages.QueryHit;
import Utils.Conversion;
import Utils.DeSerializeData;
import Utils.Neighbor;
import Utils.SerializeData;

public class Node {
	public String ID;
	public byte [] my_ip;
	public short my_port;
	public ArrayList<Neighbor> my_neighbor_list = new ArrayList<>();
	public ArrayList<File> my_shared_list = new ArrayList<>();
	public Logger logStdout;
	public int mySpeed;
	public String myGnutellaDir;
			
	public HashMap<String,Neighbor> incomingPing = new HashMap<String,Neighbor>();
	public HashMap<String,Neighbor> incomingQueries = new HashMap<String, Neighbor>();
	
	public Node (String string, byte[] ip, short port, int speed) throws ClassNotFoundException, IOException {
		this.setID(string);
		this.setMy_ip(ip);
		this.setMy_port(port);
		this.setMySpeed(speed);
		this.setMyGnutellaDir(Constant.GNUTELLA_HOME + this.getID()+ "/");
		
		File myDir = new File(this.getMyGnutellaDir());
		if (!myDir.exists()) {
			
			try {
				myDir.mkdir();
				myDir.setExecutable(true, false);
				myDir.setReadable(true, false);
				myDir.setWritable(true, false);
			} catch(SecurityException e){
				System.out.println("contact your admin, you do not have sufficient privileges to write to " + Constant.GNUTELLA_HOME);
			}
		}
		
		this.setMy_shared_list(LoadSharedList());
		this.setMy_neighbor_list(LoadNeighborList());
		
		logStdout = Logger.getLogger("NodeLog");
		
		FileHandler fh = new FileHandler(this.getMyGnutellaDir() + this.getID() + ".stdout.log");
		fh.setFormatter(new SimpleFormatter()); 
		logStdout.addHandler(fh);
		logStdout.setUseParentHandlers(false);
		logStdout.info("log created.");
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Neighbor> LoadNeighborList() throws ClassNotFoundException, IOException{
		ArrayList<Neighbor> nborList = new ArrayList<>();
		
		String filepath = this.getMyGnutellaDir() + this.getID() + ".neighbor.ser"; 
		File f = new File(filepath);
		
		if (f.exists() && !f.isDirectory())
		nborList = (ArrayList<Neighbor>) new DeSerializeData(filepath).deSerializeData();
		
		return nborList;
	}
	
	public ArrayList<File> LoadSharedList() throws ClassNotFoundException, IOException{
		
		// list path of files in shared directory, filelist.ser
		String shareDir = this.getMyGnutellaDir() + this.getID()+ ".share.dir";
		File dir = new File(shareDir);
		if (!dir.exists()) {
			try{
				dir.mkdir();
				dir.setExecutable(true, false);
				dir.setReadable(true, false);
				dir.setWritable(true, false);
			}
			catch(SecurityException e ){
				System.out.println("couldnt create a shareDirectory at " + this.getMyGnutellaDir());
			}
		}
		File [] dirFiles = dir.listFiles();
		
		if (null == dirFiles) return this.getMy_shared_list();
		
		for (File dirFile : dirFiles) {
			if (!this.hasFile(dirFile)) {
				this.getMy_shared_list().add(dirFile);
			}
		}
		return this.getMy_shared_list();
	}
	
	public byte[] getMy_ip() {
		return my_ip;
	}
	public void setMy_ip(byte[] my_ip) {
		this.my_ip = my_ip;
	}
	public short getMy_port() {
		return my_port;
	}
	public void setMy_port(short my_port) {
		this.my_port = my_port;
	}
	
	public String getID() {
		return ID;
	}
	public void setID(String string) {
		ID = string;
	}
	public ArrayList<Neighbor> getMy_neighbor_list() {
		return my_neighbor_list;
	}
	public void setMy_neighbor_list(ArrayList<Neighbor> my_neighbor_list) {
		this.my_neighbor_list = my_neighbor_list;
	}
	public ArrayList<File> getMy_shared_list() {
		return my_shared_list;
	}
	public void setMy_shared_list(ArrayList<File> my_shared_list) {
		this.my_shared_list = my_shared_list;
	}
	
	public void pingKnownHosts() throws IOException {
		
		if (null == this.getMy_neighbor_list() || this.getMy_neighbor_list().isEmpty()) {
			
			DatagramSocket clientSocket = new DatagramSocket();
			
			for (int i =0; i < Constant.KNOWN_HOSTS.length; i++) {
				
				Neighbor n = Constant.KNOWN_HOSTS[i];
				
				
				if (this.getID().equals(Conversion.getStringIP(n.getIp()) + "." + n.getPort())) continue; 
				
				this.getMy_neighbor_list().add(n);
				
				Ping pingData = new Ping(null ,this.getMy_ip(), this.getMy_port(), (short) 1, (short) 5, (short) 0 , (int) 0);
				
				byte [] sendData = pingData.message();
				
				DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length, InetAddress.getByAddress(n.getIp()), n.getPort());
				
				clientSocket.send(sendPacket);
			}
			
			if (this.getMy_neighbor_list().size() > 0) new SerializeData(this.getMyGnutellaDir() + this.getID() + ".neighbor.ser").serializeAndReplace(this.getMy_neighbor_list());
			
			clientSocket.close();
		
		}
		return; 
	} 
	
public void processPing(Ping ping) throws IOException {

		if (this.getIncomingPing().containsKey(new String(ping.pingDesc.getID(), Charset.forName("UTF-8")))) { 
				return;
		}
	
		byte [] pingID = new byte[16];
		System.arraycopy(ping.pingDesc.getID(), 0, pingID, 0, 16);

		if (ping.getPingDesc().getTTL() > (short) 0 ) {
			
			Descriptor piDesc = ping.getPingDesc();
			DatagramSocket clientSocket = new DatagramSocket();
			
			piDesc.setHops( (short) (piDesc.getHops() + (short) 1));
			piDesc.setTTL((short) (piDesc.getTTL() - (short) 1));
			
			
			int no_of_files = 0;
			if (null != this.getMy_shared_list()) {
				no_of_files = this.getMy_shared_list().size();
			}
			
			Pong pongBack = new Pong(pingID, this.getMy_ip(), this.getMy_port(), (short) 2, ping.getPingDesc().getHops(), (short) 0 ,(int) 0 , no_of_files, this.sharedSize());
			DatagramPacket pongPacket = new DatagramPacket(pongBack.message(), pongBack.message().length, InetAddress.getByAddress(ping.getSender_node_ip()), ping.getSender_node_port());
			clientSocket.send(pongPacket);
			
			for (Neighbor n: this.getMy_neighbor_list()) {
			
			byte [] noFwdIP = ping.getSender_node_ip();
			short noFwdPort = ping.getSender_node_port();
			
			ping.setSender_node_ip(this.getMy_ip());
			ping.setSender_node_port(this.getMy_port());
			
				if (this.cmpIP(noFwdIP, n.getIp()) && (n.getPort() == noFwdPort)) continue;
			
				DatagramPacket sendPacket = new DatagramPacket(ping.message(),ping.message().length, InetAddress.getByAddress(n.getIp()), n.getPort());
				
				clientSocket.send(sendPacket);
			}
		
			clientSocket.close();
		}
		
		
		return;
}

	public void processPong(Pong pong) throws IOException {
		Neighbor n = new Neighbor(pong.getSender_node_ip(), pong.getSender_node_port());
		logStdout.info(" neighbor " + n.getPort() + " has " + pong.getFile_count() + " files.");
		
		n = this.getNeighborByAddress(n);
		n.setFileCount(pong.getFile_count());
		n.setShareSize(pong.getTotal_size());
		
		if (!this.hasNeighbor(n) && !(this.cmpIP(n.getIp(), this.getMy_ip()) && this.getMy_port() == n.getPort())) {
			this.getMy_neighbor_list().add(n);
		}
		
		String pingKey = new String(pong.getPongDesc().getID(), Charset.forName("UTF-8"));
		Neighbor traceBackNbr = this.incomingPing.get(pingKey);
		
		logStdout.info("====NBOR CATALOG - "+Conversion.getStringIP(this.getMy_ip()) + ":" + this.getMy_port() +" ====");
		for (Neighbor x : this.getMy_neighbor_list()) {
			
			logStdout.info(x.getPort() + "\t count " +  x.getFileCount() + " \t size "+  x.getShareSize());
		}
		logStdout.info("====END====");
		
		if (pong.getPongDesc().getTTL() <= 0) {
			return;
			
		} else {
			
		}
		
		// trace back
		if (null != traceBackNbr) {
			// forward to traceback neighbor
			byte [] descID = pong.pongDesc.getID(); 
			
			Pong pongBack = new Pong(descID, pong.getSender_node_ip(), pong.getSender_node_port(), (short) 2, (short) (pong.getPongDesc().getTTL() - (short) 1), (short) 0 ,(int) 0 , pong.getFile_count(), pong.getTotal_size());
			//System.out.println("\n\t\t\t traced back pong from" +  this.getMy_port() +  " to " + traceBackNbr.getPort());
			DatagramPacket pongPacket = new DatagramPacket(pongBack.message(), pongBack.message().length, InetAddress.getByAddress(traceBackNbr.getIp()), traceBackNbr.getPort());
			DatagramSocket fwdSocket = new DatagramSocket(0);
			fwdSocket.send(pongPacket);
			fwdSocket.close();
		}
		
		return;
}
	
	public void processQuery(Query query) throws IOException {
		
		//on receiving a query response, process and forward 
		if (this.getIncomingQueries().containsKey(new String(query.getQueryDesc().getID(), Charset.forName("UTF-8"))))
			return;
		byte [] queryID = new byte[16];
		System.arraycopy(query.getQueryDesc().getID(), 0, queryID, 0, 16);
		
		if (query.getQueryDesc().getTTL() <= (short) 0)
			return;
		
		Descriptor queryDesc = query.getQueryDesc();
		queryDesc.setTTL((short) (queryDesc.getTTL() - (short) 1));
		queryDesc.setHops((short) (queryDesc.getHops() + (short) 1));
		
		
		// gets tab separated list of filenames, that are close match
		String resultSet = GetQueryResults(new String(query.getSearchStr(), Charset.forName("UTF-8")));
		
		DatagramSocket clientSocket = new DatagramSocket(0);
		
		if (null != resultSet) {
			System.out.println("Match found : " + resultSet);
			System.out.println("Sending QHit");
			QueryHit qHit = new QueryHit(queryID, this.getMy_ip(), this.getMy_port(), (short) 4, query.getQueryDesc().getHops(), (short) 0, (int) 0, (short) 0, (short) query.getSpeed(), resultSet); 
			DatagramPacket qhitPacket = new DatagramPacket(qHit.message(), qHit.message().length, InetAddress.getByAddress(query.getSender_node_ip()), query.getSender_node_port());
			clientSocket.send(qhitPacket);
		}
		
		byte [] noFwdIP = query.getSender_node_ip();
		short noFwdPort = query.getSender_node_port();
		
		query.setSender_node_ip(this.getMy_ip());
		query.setSender_node_port(this.getMy_port());
		
		for (Neighbor n : this.getMy_neighbor_list()) {
			
			if (this.cmpIP(noFwdIP, n.getIp()) && (n.getPort() == noFwdPort)) continue;
			DatagramPacket sendPacket = new DatagramPacket(query.message(), query.message().length, InetAddress.getByAddress(n.getIp()), n.getPort());
			clientSocket.send(sendPacket);
			
		}
		
		clientSocket.close();
	}
	
	
	/*
	 *  do not modify the searchstring, just read into a new string, normalize, search and return string resultset
	 */
	private String GetQueryResults(String searchStr) {
		

		// TODO Auto-generated method stub
		StringBuilder resultBuilder = new StringBuilder();
		String normalStr = new String(searchStr);
		normalStr.replaceAll("\\s+"," " );
		normalStr.toLowerCase();
		
		for (File f : this.getMy_shared_list()) {
			
			String fileName = new String(f.getName());
			fileName.replaceAll("\\s+", " ");
			fileName = fileName.toLowerCase();
			
			if (fileName.indexOf(normalStr) > -1){
				resultBuilder.append(f.getName() + "\t");
			}
		}
		
		if (resultBuilder.length() < 2) return null;
		
		return resultBuilder.substring(0, resultBuilder.lastIndexOf("\t"));
	}

	public void processsQueryHit(QueryHit  qhit) throws IOException {
		
		if (qhit.getQhitDesc().getTTL() <= 0) {
			return;
		}
		
		Neighbor n = new Neighbor(qhit.getSender_ip(), qhit.getSender_port());
		n.setSpeed((int)qhit.getSPEED());
		logStdout.info(" neighbor " + n.getPort() + " has " + qhit.getResultCount() + " files.");
		
		String qhitKey = new String(qhit.getQhitDesc().getID(), Charset.forName("UTF-8"));
		Neighbor traceBackNbr = this.incomingQueries.get(qhitKey);
		
		
		System.out.println("got back files" + qhit.getResultSet() + "<<");
		
		Neighbor fileNeighbor = new Neighbor(qhit.getSender_ip(), qhit.getSender_port());
		this.ProcessResultSetAndStore(qhit.getResultSet(), fileNeighbor); 
			
		
		
		// trace back
				if (null != traceBackNbr) {
					// forward to traceback neighbor
					byte [] descID = qhit.getQhitDesc().getID(); 
					
					QueryHit qhitBack = new QueryHit(descID, qhit.getSender_ip(), qhit.getSender_port(), (short) 4, (short) (qhit.getQhitDesc().getTTL() - (short) 1), (short) 0 ,(int) 0 ,qhit.getResultCount(),qhit.getSPEED(), qhit.getResultSet());
				
					DatagramPacket pongPacket = new DatagramPacket(qhitBack.message(), qhitBack.message().length, InetAddress.getByAddress(traceBackNbr.getIp()), traceBackNbr.getPort());
					DatagramSocket fwdSocket = new DatagramSocket(0);
					fwdSocket.send(pongPacket);
					fwdSocket.close();
				}

		
		return;
	}
	
	private void ProcessResultSetAndStore(String ResultSet, Neighbor n) {
		// TODO Auto-generated method stub
		String storeStr = new String(ResultSet).toString();
		String [] files = storeStr.split("\\t");
		String key_prefix = Conversion.getStringIP(n.getIp()) + "." + n.getPort() ;
	
		for (String filename: files) {
			SpawnSearch.KNOWN_RESULTS.put(key_prefix + "\t" +  filename, n);
		}
		
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public static void main (String args[]) throws IOException, ClassNotFoundException {
		
		if (null ==args[0]) {
			System.out.println("enter valid port number");
		}
		
		if (args.length < 1 || args.length > 2) {
			System.out.println("provide proper arguments!");
		}
			
		int portIO = Integer.parseInt(args[0]);
		int speed = 500;
		if (args.length == 2 ) speed = Integer.parseInt(args[1]);
		Node myNode = new Node(( Conversion.getStringIP(Constant.LOCALHOST_BYTE_ADDRESS) + "." + portIO ), Constant.LOCALHOST_BYTE_ADDRESS, (short) portIO, speed);; 
		ServerSocket tcpSocket = new ServerSocket(portIO);
		DatagramSocket servSock = new DatagramSocket(portIO);
		
		PingNeighbor.myNode = myNode;
		new PingNeighbor().start();
		
		SpawnSearch.myNode = myNode;
		new SpawnSearch().start();
		
		NodeFileServer.myNode = myNode;
		NodeFileServer.myTCPSocket = tcpSocket;
		new NodeFileServer().start();
	
		DeSerializeData nborlist_deserializor = new DeSerializeData(myNode.myGnutellaDir + myNode.getID() + ".neighbor.ser");
			
		File nbor_list_file = new File(nborlist_deserializor.getFilepath());
		
		if (nbor_list_file.exists() && !nbor_list_file.isDirectory())
		myNode.setMy_neighbor_list(((ArrayList<Neighbor>) nborlist_deserializor.deSerializeData()));
		
		if (null == myNode.getMy_neighbor_list() || myNode.getMy_neighbor_list().isEmpty()) {
			myNode.pingKnownHosts();
			
		} else {
			myNode.logStdout.info(myNode.getMy_neighbor_list().size() + "pre-known neighbors !");
		} 
		
		while (true) {
			int buffSize =servSock.getReceiveBufferSize();
			
			byte[] recvbuff = new byte[buffSize];
			DatagramPacket receivePacket = new DatagramPacket(recvbuff, recvbuff.length);
			servSock.receive(receivePacket);
			
			
			byte [] recvData = new byte[receivePacket.getLength()];
			System.arraycopy(receivePacket.getData(), 0, recvData, 0, receivePacket.getLength());
			/*
			StringBuilder sb = new StringBuilder(recvData.length * Byte.SIZE);
	
			for( int i = 0; i < Byte.SIZE * recvData.length; i++ )
	               sb.append((recvData[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
	        System.out.println(sb.toString());
			*/
	           
			byte[] payload_type = new byte[2];
			System.arraycopy(receivePacket.getData(), 16, payload_type, 0, 2);
			Neighbor recvNbor;
			
			switch((int) Conversion.bytesToShort(payload_type)) {
				case 1: {
					Ping recvPing = Ping.PingObjeFromMsg(recvData);
					
					if (myNode.incomingPing.containsKey(recvPing.getPingDesc().getID()))
						break;
						recvNbor = new Neighbor(recvPing.getSender_node_ip(), recvPing.getSender_node_port());
						// forward to neighbors if ttl valid
						myNode.processPing(recvPing);
					
						if (myNode.getMy_neighbor_list().size() < Constant.MAX_NBOR_COUNT && !myNode.hasNeighbor(recvNbor)) { 
							myNode.getMy_neighbor_list().add(recvNbor);
							new SerializeData(myNode.getMyGnutellaDir() + myNode.getID() + ".neighbor.ser").serializeAndReplace(myNode.getMy_neighbor_list());
						}
						myNode.getIncomingPing().put(new String(recvPing.getPingDesc().getID(), Charset.forName("UTF-8")), recvNbor);
					
						break;
					}
				case 2: { 
					Pong recvPong = Pong.PongObjeFromMsg(recvData);
					myNode.processPong(recvPong);
					break;
				}
				case 3: {
					
					Query recvQuery = Query.QueryObjeFromMsg(recvData);
					
					if (myNode.incomingQueries.containsKey(recvQuery.getQueryDesc().getID())) 
						break;
					recvNbor = new Neighbor(recvQuery.getSender_node_ip(), recvQuery.getSender_node_port());
					
					// forward to neighbors if ttl valid
					myNode.processQuery(recvQuery);
					myNode.getIncomingQueries().put(new String(recvQuery.getQueryDesc().getID(), Charset.forName("UTF-8")), recvNbor);
					//myNode.getIncomingQueries().put(new BigInteger(recvQuery.getQueryDesc().getID()).toString(), recvNbor);
					break;
				}
				case 4: { 
					QueryHit qHit = QueryHit.QHitObjeFromMsg(recvData);
					myNode.processsQueryHit(qHit);
					break;
				}
			}
		
		}
		
	
	}

	public HashMap<String, Neighbor> getIncomingPing() {
		return incomingPing;
	}

	public void setIncomingPing(HashMap<String, Neighbor> incomingPing) {
		this.incomingPing = incomingPing;
	}

	public HashMap<String, Neighbor> getIncomingQueries() {
		return incomingQueries;
	}

	public void setIncomingQueries(HashMap<String, Neighbor> incomingQueries) {
		this.incomingQueries = incomingQueries;
	}
		
	public int sharedSize () {
		if (null == this.getMy_shared_list()) return 0;
		int sum = 0;
		for (File f : this.getMy_shared_list()) {
			sum += f.length();
		}
		return sum;
	}

	public boolean hasNeighbor(Neighbor n) {
		
		for (Neighbor x : this.getMy_neighbor_list()) {
			
			String hashIP1 = new String(n.getIp(), Charset.forName("UTF-8"));
			String hashIP2 = new String(x.getIp(), Charset.forName("UTF-8"));
			
			if (hashIP1.equals(hashIP2) && x.getPort() == n.getPort())
				return true;
		}
		return false;
	}
	
	public Neighbor getNeighborByAddress(Neighbor n) {
		
		for (int i =0; i < this.getMy_neighbor_list().size(); i++) {
			
			
			String hashIP1 = new String(n.getIp(), Charset.forName("UTF-8"));
			String hashIP2 = new String(this.getMy_neighbor_list().get(i).getIp(), Charset.forName("UTF-8"));
			
			if (hashIP1.equals(hashIP2) && this.getMy_neighbor_list().get(i).getPort() == n.getPort())
				return this.getMy_neighbor_list().get(i);
		}
		
		return n;
	}

	public boolean hasFile(File f) {
		for (File shareFile : this.getMy_shared_list()) {
			
			if (shareFile.getAbsolutePath().equals(f.getAbsolutePath())) return true; 
		}
		return false;
	}
	
	public boolean cmpIP(byte [] ip1, byte[] ip2) {
		
		String hashIP1 = new String(ip1, Charset.forName("UTF-8"));
		String hashIP2 = new String(ip2, Charset.forName("UTF-8"));
		if (hashIP1.equals(hashIP2)) return true;
		
		return false;
	}

	public int getMySpeed() {
		return mySpeed;
	}

	public void setMySpeed(int mySpeed) {
		this.mySpeed = mySpeed;
	}

	public String getMyGnutellaDir() {
		return myGnutellaDir;
	}

	public void setMyGnutellaDir(String myGnutellaDir) {
		this.myGnutellaDir = myGnutellaDir;
	}	
}