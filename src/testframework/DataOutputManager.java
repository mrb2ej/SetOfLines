package testframework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DataOutputManager {

	public static void main(String[] args) {
		System.out.println("Starting");
		
		for(int i = 1; i < 31; i++){
			readFile("SetOfLinesTestLog" + i + ".txt");
		}
		
		System.out.println("Done");
	}
	
	private static void readFile(String filepath){
		//String filepath = "SetOfLinesTestLog + 1398906754783.txt";
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(filepath));
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}

		String testNum = "";
		String epsilon = "";
		String sparsity = "";
		String noise = "";
		String timeToCompress = "";
		String compressionRatio = "";
		String gridsize = "";

				
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			
			Scanner lineScanner = new Scanner(line);
			if (!lineScanner.hasNext()) {
				continue;
			}			
			
			String nextLine = lineScanner.next();			
			
			switch (nextLine) {
			case "Test":
				testNum = lineScanner.next();
				break;
			case "E:":
				epsilon = lineScanner.next();
				break;
			case "Grid:":
				gridsize = lineScanner.next();
				break;
			case "S:":
				sparsity = lineScanner.next();
				break;
			case "N:":
				noise = lineScanner.next();
				break;
			case "Time":
				lineScanner.next();
				lineScanner.next();
				timeToCompress = lineScanner.next();
				break;
			case "Compression":
				lineScanner.next();
				compressionRatio = lineScanner.next();

				String filepathCSV = "DataOutput";
				generateCsvFile(filepathCSV, testNum, epsilon, sparsity, noise,
						timeToCompress, compressionRatio, gridsize);
				break;
			default:				

			}			

			lineScanner.close();

		}

		scanner.close();
	}

	private static void generateCsvFile(String filepath, String testNum,
			String epsilon, String sparsity, String noise, String timeToCompress,
			String compressionRatio, String gridsize) {

				
		try {
			FileWriter writer = new FileWriter(filepath + gridsize + ".csv", true);

			writer.append("" + testNum);
			writer.append(',');
			writer.append("" + epsilon);
			writer.append(',');
			writer.append("" + sparsity);
			writer.append(',');
			writer.append("" + noise);
			writer.append(',');
			writer.append("" + timeToCompress);
			writer.append(',');
			writer.append("" + compressionRatio);
			writer.append('\n');

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
