package testframework;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import setoflines.Pair;
import setoflines.Point;
import setoflines.SetOfLines;

public class TestSuiteManager {

	static String grid_log = new String();

	public static void main(String[] args) {

		TestLog testlog = new TestLog();
		int num_test_iterations = 50;
		SetOfLines setoflines = null;

		for (int i = 0; i < num_test_iterations; i++) {

			TestSuite testSuite = new TestSuite();

			for (Test t : testSuite.getAllTests()) {

				// Generate a random point set
				ArrayList<Point> pointset = generate_random_pointset(
						t.getSparsity(), t.getNoise(), t.getGrid_size());

				// Select epsilon error based on point set
				// double epsilon = 0.01;
				double epsilon = dynamically_select_epsilon(pointset);

				System.out.println("Starting compression for test suite "
						+ (i + 1) + " of " + num_test_iterations);

				int dimension = t.getGrid_size().length;

				long startTime = System.currentTimeMillis();
				setoflines = new SetOfLines(pointset, epsilon, dimension);
				long endTime = System.currentTimeMillis();

				// Check time to compress
				long time_to_compress = endTime - startTime;

				// Check compression ratio for the set of lines
				double compression_ratio = 2 * ((double) setoflines
						.get_set_of_lines().size() / (double) pointset.size());

				// Log the compression statistics
				testlog.log("Test " + (i + 1) + " of " + num_test_iterations);
				testlog.log("E: " + epsilon);
				testlog.log(grid_log);
				testlog.log("-------------------------");
				testlog.log("Time to Compress: " + time_to_compress + " ms");
				testlog.log("Compression Ratio: " + compression_ratio);

				testlog.log("Lines: " + setoflines.get_set_of_lines());

				testlog.log("\n");
				
				
				// Will this build up in memory too much? Test objects are small, right?
				testlog.log(t, time_to_compress, compression_ratio); 
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

	// TODO: implement this method
	private static Pair closest_pair(ArrayList<Point> pointset) {
		return null;
	}

	private static double chebyshev_distance(Point p1, Point p2) {

		if (p1.getDimension() != p2.getDimension()) {
			return -1.0;
		}

		double distance = -1.0;

		for (int i = 0; i < p1.getDimension(); i++) {
			double check_distance = Math.abs(p1.getCoordinates().get(i)
					- p2.getCoordinates().get(i));
			if (check_distance > distance) {
				distance = check_distance;
			}
		}
		return distance;
	}

	private static double dynamically_select_epsilon(ArrayList<Point> pointset) {

		// Perform nearest pair on every point in the point set
		Pair closestPair = closest_pair(pointset);

		// Calculate Chebyshev distance between nearest pair
		double distance = chebyshev_distance(closestPair.getFirst(),
				closestPair.getSecond());

		// Divide by 8 to fit 8e box constraint set in Gabe's paper
		return distance / 8.0;
	}

	private static ArrayList<Point> generate_random_pointset(
			double pointset_sparsity, double noise_factor, double[] grid_size) {

		// Check that the grid dimensions are all equal
		for (int i = 0; i < grid_size.length - 1; i++) {
			if (grid_size[i] != grid_size[i + 1]) {
				return null;
			}
		}

		// Initialize the grid
		ArrayList<Point> pointset = new ArrayList<Point>();
		Random rand = new Random();
		int dimension = grid_size.length;

		ArrayList<ArrayList<double[]>> grid = new ArrayList<ArrayList<double[]>>();
		for (int i = 0; i < dimension; i++) {

			ArrayList<double[]> current_axis = new ArrayList<double[]>();

			for (double c = 0.0; c < grid_size[i]; c++) {

				double[] coordinate_and_exponent = new double[2];
				coordinate_and_exponent[0] = c;
				coordinate_and_exponent[1] = rand.nextInt(2);

				current_axis.add(coordinate_and_exponent);
			}

			grid.add(current_axis);
		}

		for (ArrayList<double[]> axis : grid) {
			if (Math.random() < pointset_sparsity) {
				ArrayList<Double> coordinates = new ArrayList<Double>();

				for (double[] points : axis) {
					double x = points[0];
					double x_exponent = points[1];
					coordinates.add(x
							+ (Math.random() * noise_factor * Math.pow(-1.0,
									x_exponent)));
				}

				pointset.add(new Point(dimension, coordinates));
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
