package testframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import algorithms.NearestNeighbor;

import setoflines.Point;
import setoflines.SetOfLines;

public class SoLTestManager {

	static String grid_log = new String();
	
	public static void main(String[] args) {

		TestLog testlog = new TestLog();
		int num_tests = 1;
		
		
		SetOfLines setoflines = null;
		

		for (int i = 0; i < num_tests; i++) {
			// Generate a random point set
			ArrayList<Point> pointset = generate_random_pointset();
			//ArrayList<Point> pointset = generate_tommy_points();
			
			// Select epsilon error based on point set
			//double epsilon = 0.01;
			double epsilon = dynamically_select_epsilon(pointset);
			
			// Define a dimension for the point set
			int dimension = 2;

			System.out.println("Starting compression");
			
			long startTime = System.currentTimeMillis();
			setoflines = new SetOfLines(pointset, epsilon, dimension);
			long endTime = System.currentTimeMillis();

			// Check time to compress
			long time_to_compress = endTime - startTime;

			// Check compression ratio for the set of lines
			double compression_ratio = 2 * ((double)setoflines.get_set_of_lines().size() / (double)pointset.size());

			// Log the compression statistics
			testlog.log("Test " + (i + 1) + " of " + num_tests);
			testlog.log("E: " + epsilon);
			testlog.log(grid_log);
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
		
		System.out.println("Trying nearest neighbor");
		
		Point nn = NearestNeighbor.NearestNeighborSearch(setoflines, generate2dPoint(1,0));
		System.out.println("Nearest Neighbor result: " + nn);
		
	}

	// TODO: Finish this method 
	private static double dynamically_select_epsilon(ArrayList<Point> pointset){
		
		// Perform nearest pair on every point in the point set
		
		
		// Calculate Chebyshev distance between nearest pair
		
		
		// Divide by 8 to fit 8e box constraint set in Gabe's paper
		return 0.01;
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
			
		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();
		double pointset_sparsity = 0.9;
		double noise_factor = 0.01;
		double x_size = 20.0;
		double y_size = 20.0;
		
		grid_log = "Grid: " + x_size + "x" + y_size + "\nS: " + pointset_sparsity + "\nN: " + noise_factor + "\n";
		
		// This generates 2D point sets
		for (double x = 0.0; x < x_size; x++){
			for (double y = 0.0; y < y_size; y++){				
				
				if (Math.random() < pointset_sparsity){
					ArrayList<Double> coordinates = new ArrayList<Double>();
					
					int x_exponent = rand.nextInt(2);
					int y_exponent = rand.nextInt(2);					
					
					coordinates.add(x + (Math.random() * noise_factor * Math.pow(-1.0, x_exponent)));
					coordinates.add(y + (Math.random() * noise_factor * Math.pow(-1, y_exponent)));
										
					pointset.add(new Point(2, coordinates));
				}				
			}
		}
		
		return pointset;
	}

}
