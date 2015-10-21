package Node;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

public class NodeFileServer extends Thread{
	
	public static Node myNode;
	public static ServerSocket myTCPSocket;
	
	
	public void run() {
		
		while (true) {
			try {
				Socket clientSocket = myTCPSocket.accept();
				DataInputStream streamInp = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream streamOut = new DataOutputStream(clientSocket.getOutputStream());
				
				byte[] getCmd = new byte[4+4];
				int dead = streamInp.read(getCmd);
				if (-1 == dead) continue;
				
				
				
				String  command = new String(getCmd, Charset.forName("UTF-8"));
				
				int transferSpeed = Integer.parseInt(command.substring(4, 7)); 
				String get = command.substring(0, 4);
				
				if (get.equals("GET ")){
					byte[] getFileName = new byte[streamInp.available()];
					streamInp.read(getFileName);
					String fileName = new String(getFileName, Charset.forName("UTF-8"));
					System.out.println("received request: " + command + " " + fileName);
					String responseStr = "PUSH " + fileName;
					
					
					byte [] sendData = new byte[responseStr.length()];
					char push [] = responseStr.toCharArray();
					int j=0;
					for(char c : push){
						sendData[j] = (byte) (int)  c ;
						j++;
					}
					streamOut.write(sendData);
					
					char [] responsechar = responseStr.toCharArray();
					byte[] response = new byte[responsechar.length];
					for (int i=0;i < responsechar.length; i++ ) 
					{
						response[i] = (byte) responsechar[i];
					}
					if (null != fileName) {
						streamOut.write(response);
						String share_dir = myNode.getID() + ".share.dir";
						File myFile = new File(myNode.getMyGnutellaDir() +share_dir+"/"+fileName);
						
						try {
							FileInputStream fileStream = new FileInputStream(myFile.getAbsolutePath());
						
						if (transferSpeed <=0 ) transferSpeed=500;
						byte[] buffer = new byte[transferSpeed];
						
						int bytesRead = 0;
						OutputStream outFile = clientSocket.getOutputStream(); 
						
						while((bytesRead = fileStream.read(buffer))>0)  
		                {  
		                    outFile.write(buffer,0,bytesRead);  
		                }  
						outFile.close();
						fileStream.close();
						}
						catch(FileNotFoundException e) {
							System.out.println("I don't have this fiel, discarding download request!");
							continue;
						}
					}
					
					
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
}
