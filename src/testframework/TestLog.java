package testframework;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestLog {

	private ArrayList<String> test_log; 
	
	private HashMap<Test, Double> compression_times;
	private HashMap<Test, Double> compression_ratios;
	
	public TestLog(){
		test_log = new ArrayList<String>();
		compression_times = new HashMap<Test, Double>();
		compression_ratios = new HashMap<Test, Double>();
	}
	
	public void log(String logMessage){
		test_log.add(logMessage);
	}
	
	public void log(Test test, double compression_time, double compression_ratio){
		compression_times.put(test, compression_time);
		compression_ratios.put(test, compression_ratio);
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
