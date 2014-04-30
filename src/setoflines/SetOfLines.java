package setoflines;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import lpsolve.*;

import net.sf.javaml.core.kdtree.KDTree;

public class SetOfLines {

	private final boolean LEFT = false;
	private final boolean RIGHT = true;

	private HashSet<Pair> unmarked_pairs = new HashSet<Pair>();

	private ArrayList<Line> maximal_lines = new ArrayList<Line>();

	private ArrayList<Line> set_of_lines = new ArrayList<Line>();

	private double epsilon;
	private int dimension;

	private KDTree kdtree;

	public SetOfLines(ArrayList<Point> pointSet, double epsilon, int dimension) {

		this.dimension = dimension;
		this.epsilon = epsilon;		

		// Create the kd-tree
		kdtree = new KDTree(dimension);
		populate_tree(kdtree, pointSet);

		// Generate all pairs
		generate_pairs(pointSet);

		// Populates maximal_lines with all maximal epsilon regular subsequences
		generate_maximal_lines();

		// Select best set of lines from maximal_lines
		generate_set_of_lines(pointSet);

		// Clear all unnecessary data structures
		maximal_lines = null;
		unmarked_pairs = null;
		kdtree = null;
	}
	
	public SetOfLines(ArrayList<Point> pointSet, int dimension) {

		this.dimension = dimension;
		this.epsilon = dynamically_select_epsilon(pointSet);		

		// Create the kd-tree
		kdtree = new KDTree(dimension);
		populate_tree(kdtree, pointSet);

		// Generate all pairs
		generate_pairs(pointSet);

		// Populates maximal_lines with all maximal epsilon regular subsequences
		generate_maximal_lines();

		// Select best set of lines from maximal_lines
		generate_set_of_lines(pointSet);

		// Clear all unnecessary data structures
		maximal_lines = null;
		unmarked_pairs = null;
		kdtree = null;
	}

	public ArrayList<Line> get_set_of_lines() {
		return set_of_lines;
	}
	
	public double getEpsilon(){
		return epsilon;
	}

	private void generate_set_of_lines(ArrayList<Point> pointSet) {

		ArrayList<PotentialLine> potentialLines = new ArrayList<PotentialLine>();

		// Sort the epsilon-regular subsequences according to number of points
		Collections.sort(maximal_lines);

		// Populate sorted linked-list of buckets of lines
		Bucket front_bucket = generate_list_of_buckets(potentialLines);

		// Use greedy algorithm to select set of lines
		greedy_select_lines(pointSet, potentialLines, front_bucket);

	}

	private void greedy_select_lines(ArrayList<Point> pointSet,
			ArrayList<PotentialLine> potentialLines, Bucket front_bucket) {

		// Create a map of points to all the potential lines that point is on
		HashMap<Point, ArrayList<PotentialLine>> unused_points = populate_unused_points(
				pointSet, potentialLines);

		// Repeatedly select a line from the head bucket and add it to the set
		// of lines
		while (unused_points.size() > 0) {

			// Get longest available line and add it to set of lines
			Line selected_line = select_line(front_bucket);
			set_of_lines.add(selected_line);

			for (Point current_point : selected_line.getAllPoints()) {

				ArrayList<PotentialLine> lines_containing_point = unused_points
						.get(current_point);

				if (lines_containing_point != null) {
					for (PotentialLine current_line : lines_containing_point) {

						// Decrement number unused points
						current_line.num_unused_points--;

						// Move PotentialLine to new bucket
						current_line.bucket.removeLine(current_line);
						current_line.bucket = current_line.bucket
								.getNextBucket();

						if (current_line.bucket.getPreviousBucket() != null
								&& current_line.bucket.getPreviousBucket()
										.isEmpty()) {
							current_line.bucket
									.setPreviousBucket(current_line.bucket
											.getPreviousBucket()
											.getPreviousBucket());
						}

						// If we end up between two buckets at a bucket that
						// doesn't
						// exist, we need to create the "in-between" bucket for
						// the line
						if (current_line.bucket.getValue() != current_line.num_unused_points) {
							Bucket new_bucket = new Bucket(
									current_line.num_unused_points);
							new_bucket.setPreviousBucket(current_line.bucket
									.getPreviousBucket());
							if (current_line.bucket.getPreviousBucket() != null) {
								current_line.bucket.getPreviousBucket()
										.setNextBucket(new_bucket);
							}
							current_line.bucket.setPreviousBucket(new_bucket);
							new_bucket.setNextBucket(current_line.bucket);
							current_line.bucket = new_bucket;
							new_bucket.addLine(current_line);
						} else {
							current_line.bucket.addLine(current_line);
						}
					}

					unused_points.remove(current_point);
				}
			}
		}
	}

	private Bucket generate_list_of_buckets(
			ArrayList<PotentialLine> potentialLines) {
		// Create the first bucket in the unused points bucket list
		Bucket front_bucket = new Bucket(0);

		// Populate sorted linked-list of buckets of lines
		for (Line current_line : maximal_lines) {
			PotentialLine potential_line = new PotentialLine();
			potential_line.line = current_line;
			potential_line.num_unused_points = current_line.getNum_points();

			if (potential_line.num_unused_points == front_bucket.getValue()) {

				// Add potential_line to the proper bucket
				front_bucket.addLine(potential_line);
				potential_line.bucket = front_bucket;
			} else {

				// Because maximal_lines is sorted, if potential_line does not
				// belong in the first bucket, we need to add a new bucket
				Bucket new_bucket = new Bucket(potential_line.num_unused_points);
				new_bucket.setNextBucket(front_bucket);
				front_bucket.setPreviousBucket(new_bucket);
				front_bucket = new_bucket;

				// Add potential_line to the proper bucket
				front_bucket.addLine(potential_line);
				potential_line.bucket = front_bucket;
			}

			// Add potential_line to the list of potential lines
			potentialLines.add(potential_line);
		}

		return front_bucket;
	}

	private Line select_line(Bucket front_bucket) {
		Bucket current_bucket = front_bucket;
		while (current_bucket.isEmpty()) {
			current_bucket = current_bucket.getNextBucket();
		}
		return current_bucket.getPotentialLine().line;
	}

	private HashMap<Point, ArrayList<PotentialLine>> populate_unused_points(
			ArrayList<Point> pointSet, ArrayList<PotentialLine> potentialLines) {

		HashMap<Point, ArrayList<PotentialLine>> unused_points = new HashMap<Point, ArrayList<PotentialLine>>();

		// Iterate through all lines and match every point in every line with
		// all the lines that point is a part of

		for (Point p : pointSet) {
			unused_points.put(p, new ArrayList<PotentialLine>());
		}

		for (PotentialLine l : potentialLines) {
			for (Point p : l.line.getAllPoints()) {
				unused_points.get(p).add(l);
			}
		}

		return unused_points;

	}

	private void generate_maximal_lines() {

		while (!unmarked_pairs.isEmpty()) {

			// Get the current pair
			Pair current_pair = unmarked_pairs.iterator().next();

			// Create new working set and new auxlist
			Line workingSet = new Line(current_pair.getFirst(),
					current_pair.getSecond());

			ArrayList<Line> auxlist = new ArrayList<Line>();

			// Add current pair to working set
			workingSet.add_point(current_pair.getFirst(), RIGHT);
			workingSet.add_point(current_pair.getSecond(), RIGHT);

			// Initialize the working set for compression
			initialize(workingSet);

			// Copy the un-marched working set
			Line workingSetCopy = new Line(workingSet);

			// Add the current working set to auxlist
			update_auxlist(auxlist, workingSet);

			// March the working set and the copy
			march(workingSet, RIGHT);
			march(workingSetCopy, LEFT);

			// Mark all pairs in the current auxlist
			mark_auxlist(auxlist);

			// Add auxlist to maximal_lines
			maximal_lines.addAll(auxlist); // Adding same line twice?
		}
	}

	private void populate_tree(KDTree kdtree, ArrayList<Point> pointSet) {

		// Insert all points into the kd-tree
		for (Point p : pointSet) {
			
			ArrayList<Double> coordinates = p.getCoordinates();

			double[] key = new double[coordinates.size()];

			for (int i = 0; i < coordinates.size(); i++) {
				key[i] = coordinates.get(i);
			}

			kdtree.insert(key, p);
		}

	}

	private void generate_pairs(ArrayList<Point> pointSet) {

		// Generate all pairs of points in the point set and insert into
		// unmarked_pairs
		for (int i = 0; i < pointSet.size(); i++) {
			for (int j = i + 1; j < pointSet.size(); j++) {

				Pair new_pair;
				try {
					new_pair = new Pair(pointSet.get(i), pointSet.get(j));
					unmarked_pairs.add(new_pair);

				} catch (Exception e) {
					// Dimensions don't match exception
					e.printStackTrace();
				}
			}
		}

	}

	private void initialize(Line workingSet) {

		// Mark the working set
		// mark_pair(workingSet.get(0), workingSet.get(1));

		while (extend(workingSet, LEFT)) {
			// Keep extending
		}

		while (extend(workingSet, RIGHT)) {
			// Keep extending
		}
	}

	private void remove_opposite_end(Line workingSet, boolean direction) {

		// Remove the <oposite-direction>-most point from the working set
		if (direction == LEFT) {
			// Erase the end of the working set
			workingSet.getAllPoints().remove(
					workingSet.getAllPoints().size() - 1);
		} else {
			// Erase the beginning of the working set
			workingSet.getAllPoints().remove(0);
		}
		workingSet.setNum_points(workingSet.getNum_points() - 1);
	}

	private void march(Line workingSet, boolean direction) {

		// Try removing the end of the epsilon sequence to see if the
		// sequence can be extended any further

		remove_opposite_end(workingSet, direction);

		while (workingSet.getAllPoints().size() > 1) {

			boolean did_extend = false;

			while (extend(workingSet, direction)) {
				did_extend = true;
			}

			if (did_extend) {
				// Add new maximum to the working set
				update_auxlist(maximal_lines, workingSet);
			}

			remove_opposite_end(workingSet, direction);

		}

	}

	private void update_auxlist(ArrayList<Line> auxlist, Line workingSet) {

		// Copy the working set
		Line new_working_set = new Line(workingSet);

		// Add the working set copy to the set of maximal lines
		auxlist.add(new_working_set);
	}

	private boolean extend(Line workingSet, boolean direction) {

		// Check the kd tree for points within the 8e box
		// if point exists, add the point to the working set

		Point next_point = get_next_point(get_next_point_guess(workingSet,
				direction));

		if (next_point != null) {
			// Check if candidate point fits the line
			if (fits_the_line(next_point, workingSet, direction)) {
				workingSet.add_point(next_point, direction);
				return true;
			}
		}

		return false;

	}

	private boolean fits_the_line(Point next_point, Line workingSet,
			boolean direction) {

		boolean pointFits = true;
		ArrayList<Double> initial_coordinates = new ArrayList<Double>();
		ArrayList<Double> second_coordinates = new ArrayList<Double>();

		// Create a problem with 3 variable and 2N + 2 constraints
		// where N is the number of points on the current line
		try {
			for (int dim = 0; dim < dimension && pointFits; dim++) {
				LpSolve solver = LpSolve.makeLp(0, 3);
				solver.setVerbose(1); // Only critical output
				// 2 * workingSet.getNum_points() + 2

				solver.setSense(false); // Minimize

				// Set objective function
				solver.strSetObjFn("1 0 0");

				// add constraints
				for (int i = 0; i < workingSet.getNum_points(); i++) {

					Point current_point = workingSet.getAllPoints().get(i);
					double x;

					if (direction == LEFT) {
						x = i + 1;
					} else {
						x = i;
					}

					double y = current_point.getCoordinates().get(dim);

					double[] row_firstconstraint = new double[4];
					row_firstconstraint[0] = 3.0;
					row_firstconstraint[1] = -1.0;
					row_firstconstraint[2] = -1.0 * x;
					row_firstconstraint[3] = -1.0;

					double[] row_secondconstraint = new double[4];
					row_secondconstraint[0] = 3.0;
					row_secondconstraint[1] = -1.0;
					row_secondconstraint[2] = x;
					row_secondconstraint[3] = 1.0;

					solver.addConstraint(row_firstconstraint, LpSolve.LE, -1.0
							* y);
					solver.addConstraint(row_secondconstraint, LpSolve.LE, y);

				}

				double x;
				if (direction == LEFT) {
					x = 0;
				} else {
					x = workingSet.getNum_points();
				}

				double y = next_point.getCoordinates().get(dim);

				double[] row_firstconstraint = new double[4];
				row_firstconstraint[0] = 3.0;
				row_firstconstraint[1] = -1.0;
				row_firstconstraint[2] = -1.0 * x;
				row_firstconstraint[3] = -1.0;

				double[] row_secondconstraint = new double[4];
				row_secondconstraint[0] = 3.0;
				row_secondconstraint[1] = -1.0;
				row_secondconstraint[2] = x;
				row_secondconstraint[3] = 1.0;

				solver.addConstraint(row_firstconstraint, LpSolve.LE, -1.0 * y);
				solver.addConstraint(row_secondconstraint, LpSolve.LE, y);

				// solve the problem
				int check = solver.solve();

				// This should give us vector <r c d>, which is equivalent to x
				// in the Ax <= b constraint model

				double[] var = solver.getPtrVariables();

				if (var[0] < epsilon) {
					initial_coordinates.add(var[2]);
					second_coordinates.add(var[1] + var[2]);
				} else {
					pointFits = false;
				}

				// delete the problem and free memory
				solver.deleteLp();
			}

			if (pointFits) {
				workingSet.setInitial_point(new Point(dimension,
						initial_coordinates));
				workingSet.setSecond_point(new Point(dimension,
						second_coordinates));
			}

		} catch (LpSolveException e) {
			e.printStackTrace();
		}

		return pointFits;
	}

	private void mark_pair(Point first, Point second) {

		Pair new_pair;

		try {
			new_pair = new Pair(first, second);
			// remove from unmarked
			boolean check = unmarked_pairs.remove(new_pair);

		} catch (Exception e) {
			// Dimensions don't match
			e.printStackTrace();
		}
	}

	private void mark_auxlist(ArrayList<Line> auxlist) {
		for (Line line : auxlist) {
			mark_line(line);
		}
	}

	private void mark_line(Line line) {
		for (int i = 0; i < line.getAllPoints().size() - 1; i++) {
			mark_pair(line.getAllPoints().get(i), line.getAllPoints()
					.get(i + 1));
		}
	}

	private Point get_next_point(Point next_point_guess) {

		double[] lower_bound = new double[dimension];
		double[] upper_bound = new double[dimension];

		for (int i = 0; i < next_point_guess.getDimension(); i++) {
			upper_bound[i] = next_point_guess.getCoordinates().get(i) + 8
					* epsilon;
			lower_bound[i] = next_point_guess.getCoordinates().get(i) - 8
					* epsilon;
		}

		Object[] returned_value = kdtree.range(lower_bound, upper_bound);

		if (returned_value.length == 0)
			return null;

		return (Point) returned_value[0];

	}

	private Point get_next_point_guess(Line workingSet, boolean direction) {

		Point A = null;
		Point B = null;

		if (direction == LEFT) {
			A = workingSet.getAllPoints().get(0);
			B = workingSet.getAllPoints().get(1);
		}

		if (direction == RIGHT) {
			A = workingSet.getAllPoints().get(
					workingSet.getAllPoints().size() - 1);
			B = workingSet.getAllPoints().get(
					workingSet.getAllPoints().size() - 2);
		}

		return A.add(A.subtract(B));
	}

	private Pair closest_pair(ArrayList<Point> pointset) {
		
		// Pre-sort points
		ArrayList<ArrayList<Point>> sorted_points = new ArrayList<ArrayList<Point>>();
		for (int i = 0; i < this.dimension; i++) {
			sorted_points.add(sort_points_by_dimension(pointset, i));
			
		}
		
		return closest_pair_helper(sorted_points);
	}

	private Pair closest_pair_helper(ArrayList<ArrayList<Point>> sorted_points) {
		
		ArrayList<Point> first_dim_points = sorted_points.get(0);
		int points_left = first_dim_points.size();
		
		// Base Case
		if (points_left <= 3) {
			return brute_force_closest_pair(first_dim_points);
		}
		
		// Divide into 2 subproblems
		int median = points_left / 2;
		ArrayList<ArrayList<Point>> left_half = new ArrayList<ArrayList<Point>>();
		ArrayList<ArrayList<Point>> right_half = new ArrayList<ArrayList<Point>>();
		ArrayList<Point> left_points = new ArrayList<Point>();
		ArrayList<Point> right_points = new ArrayList<Point>();
		HashSet<Point> left_pointset = new HashSet<Point>();
		for (int i = 0; i < median; i++) {
			left_points.add(first_dim_points.get(i));
			left_pointset.add(first_dim_points.get(i));
		}
		for (int i = median; i < points_left; i++) {
			right_points.add(first_dim_points.get(i));
		}
		left_half.add(left_points);
		right_half.add(right_points);
		for (int i = 1; i < this.dimension; i++) {
			ArrayList<Point> current_left = new ArrayList<Point>();
			ArrayList<Point> current_right = new ArrayList<Point>();
			ArrayList<Point> current_sorted = sorted_points.get(i);
			for (int j = 0; j < current_sorted.size(); j++) {
				Point current_point = current_sorted.get(j);
				if (left_pointset.contains(current_point)) {
					current_left.add(current_point);
				} else {
					current_right.add(current_point);
				}
			}
			left_half.add(current_left);
			right_half.add(current_right);
		}
		// Recursively solve the subproblems
		Pair left_closest = closest_pair_helper(left_half);
		Pair right_closest = closest_pair_helper(right_half);
		double left_distance = -1.0;
		if(left_closest != null) left_distance = chebyshev_distance(left_closest);
		
		double right_distance = -1.0;
		if(right_closest != null) right_distance = chebyshev_distance(right_closest);
		
		Pair closest_pair = left_closest;
		double closest_distance = left_distance;
		if ((right_distance < left_distance && right_closest != null) || left_closest == null) {
			closest_pair = right_closest;
			closest_distance = right_distance;
		}
		// Find closest pair that crosses the hyperplane
		double best_crossover_distance = -1.0;
		Pair best_crossover_pair = null;
		// // Find points within closest_distance of the hyperplane
		ArrayList<Point> left_close = new ArrayList<Point>();
		ArrayList<Point> right_close = new ArrayList<Point>();
		double hyperplane_coordinate = first_dim_points.get(median)
				.getCoordinates().get(0);
		double min_coordinate = hyperplane_coordinate - closest_distance;
		double max_coordinate = hyperplane_coordinate + closest_distance;
		for (int i = left_points.size() - 1; i >= 0; i--) {
			if (left_points.get(i).getCoordinates().get(0) >= min_coordinate) {
				left_close.add(left_points.get(i));
			} else {
				break;
			}
		}
		for (int i = 0; i < right_points.size() - 1; i++) {
			if (right_points.get(i).getCoordinates().get(0) <= max_coordinate) {
				right_close.add(right_points.get(i));
			} else {
				break;
			}
		}
		// // For each point in the left half that is close to the
		// // hyperplane, check potential points in the right half
		// // TODO: This isn't optimal
		for (Point left_p : left_close) {
			for (Point right_p : right_close) {
				double current_distance = chebyshev_distance(left_p, right_p);
				if (current_distance < best_crossover_distance
						|| best_crossover_distance < 0) {
					best_crossover_distance = current_distance;
					try {
						best_crossover_pair = new Pair(left_p, right_p);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (best_crossover_distance < closest_distance && best_crossover_pair != null) {
			closest_pair = best_crossover_pair;
		}
		return closest_pair;
	}

	private Pair brute_force_closest_pair(ArrayList<Point> points) {
		double distance = -1.0;
		Pair closest_pair = null;
		for (int i = 0; i < points.size(); i++) {
			Point current_first = points.get(i);
			for (int j = i + 1; j < points.size(); j++) {
				Point current_second = points.get(j);
				double current_distance = chebyshev_distance(current_first,
						current_second);
				if (distance < 0 || distance > current_distance) {
					try {
						closest_pair = new Pair(current_first, current_second);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return closest_pair;
	}

	private ArrayList<Point> sort_points_by_dimension(
			ArrayList<Point> pointset, final int dim) {
		
		ArrayList<Point> sorted_points = new ArrayList<Point>(pointset);
		Collections.sort(sorted_points, new Comparator<Point>() {
			@Override
			public int compare(Point a, Point b) {
				double a_val = a.getCoordinates().get(dim);
				double b_val = b.getCoordinates().get(dim);
				if(a_val > b_val)
				{
					return 1;
				}
				if(b_val > a_val)
				{
					return -1;
				}
				return 0;
			}
		});
		return sorted_points;
	}

	private double chebyshev_distance(Pair p) {
		return chebyshev_distance(p.first, p.second);
	}

	private double chebyshev_distance(Point p1, Point p2) {
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
	
	private double dynamically_select_epsilon(ArrayList<Point> pointset) {

		// Perform nearest pair on every point in the point set
		Pair closestPair = closest_pair(pointset);

		// Calculate Chebyshev distance between nearest pair
		double distance = chebyshev_distance(closestPair);

		// Divide by 16 to fit 8e box constraint set in Gabe's paper
		return distance / 16.0;
	}

}
