package testframework;

import java.io.IOException;
import java.util.ArrayList;

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
		return null;
	}

}
