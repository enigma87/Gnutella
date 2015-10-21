package Utils;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;


public class DeSerializeData {
	static int deSerialized_count = 0;

	public String filepath;
	
	public DeSerializeData(String fname) {
		// TODO Auto-generated constructor stub
		this.setFilepath(fname);
	
	}
	
	public  Object deSerializeData() throws IOException, ClassNotFoundException {
		Object obj = null;
		
		FileInputStream fileInp = new FileInputStream(this.getFilepath());
		ObjectInputStream objInp = new ObjectInputStream(fileInp);
		
		obj = objInp.readObject();
		objInp.close();
		fileInp.close();
		
		deSerialized_count += 1;
		
		return obj;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
}
