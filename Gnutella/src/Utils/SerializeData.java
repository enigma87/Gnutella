package Utils;
import java.io.*;

public class SerializeData {

	public String filepath;
	
	public SerializeData(String fname) {
		// TODO Auto-generated constructor stub
		this.setFilepath(fname);
	
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void serializeAndReplace(Object obj) throws IOException{
		FileOutputStream fOut = new FileOutputStream(this.getFilepath());
		ObjectOutputStream objOut = new ObjectOutputStream(fOut);
		objOut.writeObject(obj);
		objOut.close();
		fOut.close();
	}
	

}
