package setoflines;

import java.util.ArrayList;
import java.util.Collections;
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

		this.epsilon = epsilon;
		this.dimension = dimension;

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

	private void generate_set_of_lines(ArrayList<Point> pointSet) {

		// Create the first bucket in the unused points bucket list
		Bucket front_bucket = new Bucket(0);
		ArrayList<PotentialLine> potentialLines = new ArrayList<PotentialLine>();

		// Sort the epsilon-regular subsequences according to number of points
		Collections.sort(maximal_lines);

		// Populate sorted linked-list of buckets of lines
		generate_list_of_buckets(front_bucket, potentialLines);

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
						if (current_line.bucket.getPreviousBucket().isEmpty()) {
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
				}
			}
		}
	}

	private void generate_list_of_buckets(Bucket front_bucket,
			ArrayList<PotentialLine> potentialLines) {

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
	}

	private Line select_line(Bucket front_bucket) {
		return front_bucket.getPotentialLine().line;
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
			workingSet.add_point(current_pair.getFirst());
			workingSet.add_point(current_pair.getSecond());

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
			if (fits_the_line(next_point, workingSet)) {
				workingSet.add_point(next_point);

				return true;
			}
		}

		return false;

	}

	private boolean fits_the_line(Point next_point, Line workingSet) {
		// TODO: Implement this LP solver properly
		// (What we have below is an example linear program)

		// TODO: Add the equation of the line to workingSet

		boolean pointFits = false;

		// Create a problem with 1 variable and 2N constraints
		// where N is the number of points on the current line
		try {
			LpSolve solver = LpSolve.makeLp(workingSet.getNum_points(), 1);

			// add constraints
			for(int i = 0; i < workingSet.getNum_points(); i++){
				
				// TODO: Add error handling for this
				Point current_point = workingSet.getAllPoints().get(i);
				double x = current_point.getCoordinates().get(0);
				double y = current_point.getCoordinates().get(1);
				
				double[] row_firstconstraint = new double[2];
				row_firstconstraint[0] = -1.0;
				row_firstconstraint[1] = -1.0 * x;
				
				double[] row_secondconstraint = new double[2];
				row_secondconstraint[0] = -1.0;
				row_secondconstraint[1] = x;
				
				solver.addConstraint(row_firstconstraint, LpSolve.LE, -1.0 * y);
				solver.addConstraint(row_secondconstraint, LpSolve.LE, y);
				
			}			

			// Set objective function
			solver.strSetObjFn("1 1");

			// solve the problem
			solver.solve();

			// print solution
			
			// This should give us vector <r c>, which is equivalent to x
			// in the Ax <= b constraint model
			
			
			// System.out.println("Value of objective function: " + solver.getObjective());
			
			double[] var = solver.getPtrVariables();
			// If LP is solved, we have a point that fits
			pointFits = (var.length == 2);  // This is messy and should be fixed
			
			/*
			for (int i = 0; i < var.length; i++) {
				System.out.println("Value of var[" + i + "] = " + var[i]);
			}
			*/		

			// delete the problem and free memory
			solver.deleteLp();

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
			unmarked_pairs.remove(new_pair);

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
		for (int i = 0; i < line.getAllPoints().size() - 2; i++) {
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

}
