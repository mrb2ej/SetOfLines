package testframework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class TestLog {

	private ArrayList<String> test_log; 
	
	public TestLog(){
		test_log = new ArrayList<String>();
	}
	
	public void log(String logMessage){
		test_log.add(logMessage);
	}
	
	public void serializeToFile() throws IOException{
		
		File file = new File("SetOfLinesTestLog.txt");
		 
		// If file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);

		for(String log_message : test_log){
			bw.append(log_message + "\n");
		}
		
		bw.close();
	}
	

}
