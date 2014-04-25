package testframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import setoflines.Point;
import setoflines.SetOfLines;

public class SoLTestManager {

	public static void main(String[] args) {

		TestLog testlog = new TestLog();
		int num_tests = 1000;

		for (int i = 0; i < num_tests; i++) {
			// Generate a random point set
			ArrayList<Point> pointset = generate_random_pointset();

			// Select epsilon error based on point set
			double epsilon = 0.001;

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
			testlog.log("Test " + (i + 1) + " of " + num_tests);
			testlog.log("-------------------------");
			testlog.log("Time to Compress: " + time_to_compress);
			testlog.log("Compression Ratio: " + compression_ratio);
			testlog.log("\n");
		}

		try {
			testlog.serializeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static ArrayList<Point> generate_random_pointset() {
		
		// Start with 1024 points and work up
		
		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();
		double pointset_sparsity = 0.7;
		
		// This generates 2D point sets
		for (double x = 0.0; x < 32.0; x++){
			for (double y = 0.0; y < 32.0; y++){				
				
				if (Math.random() < pointset_sparsity){
					ArrayList<Double> coordinates = new ArrayList<Double>();
					
					int x_exponent = rand.nextInt(2);
					int y_exponent = rand.nextInt(2);
					
					coordinates.add(x + (Math.random() * Math.pow(-1.0, x_exponent)));
					coordinates.add(y + (Math.random() * Math.pow(-1, y_exponent)));
					
					pointset.add(new Point(2, coordinates));
				}				
				
			}
		}
		
		return pointset;
	}

}
