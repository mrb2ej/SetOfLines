package testframework;

import java.util.ArrayList;

import setoflines.Point;
import setoflines.SetOfLines;

public class SoLTestManager {

	
	public static void main(String[] args) {
		
		TestLog testlog = new TestLog();
		
		// Generate a random point set 
		ArrayList<Point> pointset = null;
		
		// Select epsilon error based on point set 
		double epsilon = 0;
		
		// Define a dimension for the point set
		int dimension = 2;
		
		
		long startTime = System.currentTimeMillis();		
		SetOfLines setoflines = new SetOfLines(pointset, epsilon, dimension);		
		long endTime = System.currentTimeMillis();
		
		// Check time to compress 
		long time_to_compress = endTime - startTime;
		
		// TODO: How to check compression ratio for the set of lines
		double compression_ratio = 0;
		
		// Log the compression statistics 
		testlog.log("Time to Compress: " + time_to_compress);
		testlog.log("Compression Ratio: " + compression_ratio);
		
		
		

	}

}
