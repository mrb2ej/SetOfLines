package testframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import setoflines.Point;
import setoflines.SetOfLines;

public class SoLTestManager {

	public static void main(String[] args) {

		TestLog testlog = new TestLog();
		int num_tests = 1;

		for (int i = 0; i < num_tests; i++) {
			// Generate a random point set
			ArrayList<Point> pointset = generate_random_pointset();
			//ArrayList<Point> pointset = generate_tommy_points();
			
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
			testlog.log("Time to Compress: " + time_to_compress + " ms");
			testlog.log("Compression Ratio: " + compression_ratio);
			
			
			testlog.log("Lines: " + setoflines.get_set_of_lines());
			
			
			testlog.log("\n");
		}

		try {
			testlog.serializeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		System.out.println("Done");
		
	}

	
	private static ArrayList<Point> generate_tommy_points() {
		ArrayList<Point> points = new ArrayList<Point>();
		points.add(generate2dPoint(0.05, 0));
		points.add(generate2dPoint(1, 1.02));
		points.add(generate2dPoint(1.998, 2));
		points.add(generate2dPoint(1, -0.003));
		return points;
	}
	
	private static Point generate2dPoint(double x, double y) {
		ArrayList<Double> coordinates = new ArrayList<Double>();
		coordinates.add(x);
		coordinates.add(y);
		return new Point(2, coordinates);
	}
	
	
	
	
	private static ArrayList<Point> generate_random_pointset() {
		
		// Start with 1024 points and work up
		
		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();
		double pointset_sparsity = 0.7;
		
		// This generates 2D point sets
		for (double x = 0.0; x < 10.0; x++){
			for (double y = 0.0; y < 10.0; y++){				
				
				if (Math.random() < pointset_sparsity){
					ArrayList<Double> coordinates = new ArrayList<Double>();
					
					int x_exponent = rand.nextInt(2);
					int y_exponent = rand.nextInt(2);
					
					// coordinates.add(x + (Math.random() * Math.pow(-1.0, x_exponent)));
					// coordinates.add(y + (Math.random() * Math.pow(-1, y_exponent)));
					
					coordinates.add(x);
					coordinates.add(y);
					
					pointset.add(new Point(2, coordinates));
				}				
				
			}
		}
		
		return pointset;
	}

}
