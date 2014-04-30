package testframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import setoflines.Point;
import setoflines.SetOfLines;

public class TestSuiteManager {

	static String grid_log = new String();

	public static void main(String[] args) {

		TestLog testlog = new TestLog();
		int num_test_iterations = 1;
		SetOfLines setoflines = null;

		for (int i = 0; i < num_test_iterations; i++) {

			TestSuite testSuite = new TestSuite();

			System.out.println("Starting compression for test suite " + (i + 1)
					+ " of " + num_test_iterations);

			int x = 0;

			for (Test t : testSuite.getAllTests()) {
				x++;
				// Generate a random point set
				// ArrayList<Point> pointset =
				// generate_random_pointset(t.getSparsity(), t.getNoise(),
				// t.getGrid_size());
				ArrayList<Point> pointset = generate_random_pointset(
						t.getSparsity(), t.getNoise(), t.getGrid_size());

				// Select epsilon error based on point set
				// double epsilon = 0.01;

				System.out.println("Test " + x + " of "
						+ testSuite.getAllTests().size() + ": " + grid_log);

				int dimension = t.getGrid_size().length;

				long startTime = System.currentTimeMillis();
				setoflines = new SetOfLines(pointset, dimension);
				// setoflines = new SetOfLines(pointset, epsilon, dimension);
				long endTime = System.currentTimeMillis();

				// Check time to compress
				long time_to_compress = endTime - startTime;

				// Check compression ratio for the set of lines
				double compression_ratio = 2 * ((double) setoflines
						.get_set_of_lines().size() / (double) pointset.size());

				// Log the compression statistics
				testlog.log("Iteration " + (i + 1) + " of "
						+ num_test_iterations);
				testlog.log("Test " + x + " of "
						+ testSuite.getAllTests().size());
				testlog.log("E: " + setoflines.getEpsilon());
				testlog.log(grid_log);
				testlog.log("-------------------------");
				testlog.log("Time to Compress: " + time_to_compress + " ms");
				testlog.log("Compression Ratio: " + compression_ratio);

				testlog.log("Lines: " + setoflines.get_set_of_lines());

				testlog.log("\n");

				// Will this build up in memory too much? Test objects are
				// small, right?
				// testlog.log(t, time_to_compress, compression_ratio);

			}

		}

		// Output the test log
		try {
			testlog.serializeToFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Done Testing");

	}

	private static ArrayList<Point> generate_random2D_pointset(
			double pointset_sparsity, double noise_factor, double[] grid_size) {

		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();

		double x_size = grid_size[0];
		double y_size = grid_size[1];

		grid_log = "Grid: " + x_size + "x" + y_size + "\nS: "
				+ pointset_sparsity + "\nN: " + noise_factor + "\n";

		// This generates 2D point sets
		for (double x = 0.0; x < x_size; x++) {
			for (double y = 0.0; y < y_size; y++) {

				if (Math.random() < pointset_sparsity) {
					ArrayList<Double> coordinates = new ArrayList<Double>();

					int x_exponent = rand.nextInt(2);
					int y_exponent = rand.nextInt(2);

					coordinates.add(x
							+ (Math.random() * noise_factor * Math.pow(-1.0,
									x_exponent)));
					coordinates.add(y
							+ (Math.random() * noise_factor * Math.pow(-1,
									y_exponent)));

					pointset.add(new Point(2, coordinates));
				}
			}
		}

		return pointset;
	}

	private static ArrayList<Point> generate_random_pointset(
			double pointset_sparsity, double noise_factor, double[] grid_size) {

		// Initialize the grid
		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();
		int dimension = grid_size.length;		

		int[] current_coordinate = new int[dimension];

		for (int i = 0; i < current_coordinate.length; i++) {
			current_coordinate[i] = 0;
		}

		while (current_coordinate[0] < grid_size[0]) {
			
			// Possibly make a point at current_coordinate
			if (Math.random() < pointset_sparsity) {
				ArrayList<Double> coordinates = new ArrayList<Double>();

				for(int d = 0; d < dimension; d++){
					double coord = current_coordinate[d];
					double coord_exponent = rand.nextInt(2);
					
					coordinates.add(coord
							+ (Math.random() * noise_factor * Math.pow(-1.0,
									coord_exponent)));					
				}
				
				pointset.add(new Point(dimension, coordinates));
				
			}

			current_coordinate[dimension - 1] += 1;
			boolean done = false;
			int current_dim = dimension - 1;

			while (!done && current_dim > 0) {
				done = true;
				if (current_coordinate[current_dim] >= grid_size[current_dim]) {
					done = false;
					current_coordinate[current_dim] = 0;
					current_coordinate[current_dim - 1]++;
				}
				current_dim--;
			}
		}		

		// Log the grid statistics
		grid_log = "Grid: ";
		for (int i = 0; i < dimension; i++) {
			if (i == 0) {
				grid_log += grid_size[i];
			} else {
				grid_log += "x" + grid_size[i];
			}
		}
		grid_log += "\nS: " + pointset_sparsity + "\nN: " + noise_factor + "\n";

		return pointset;
	}

}
