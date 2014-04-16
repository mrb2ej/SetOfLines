package setoflines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import redblacktree.RedBlackTree;

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

		// Initialize and populate unused_points structure
		//HashMap<Point, ArrayList<Line>> unused_points = populate_unused_points(pointSet);
		
		
		LinkedList<Bucket> bucket_list = new LinkedList<Bucket>();
		
		// TODO: Populate bucket list 
		
		//LinkedList<PotentialLine> potential_lines = new LinkedList<PotentialLine>();
		
		HashMap<Point, ArrayList<PotentialLine>> unused_points = populate_unused_points(pointSet);
		
		
		

		// Tree, unused_points, num_unused_points
		while (unused_points.size() > 0){
			Line selected_line = select_line(bucket_list);
			
			for (Point p : selected_line.getAllPoints()){
				ArrayList<PotentialLine> lines_containing_point = unused_points.get(p);
				if(lines_containing_point != null){
					for(PotentialLine l : lines_containing_point){
						
						// Decrement num unused points
						
						// Move PotentialLine to new bucket 
						
						
					}
				}
			}			
		}	

	}
	
	private Line select_line(RedBlackTree line_tree){
		
		return null;
	}
	
	private HashSet<PotentialLine> populate_potential_lines(){
		HashSet<PotentialLine> potential_lines = new HashSet<PotentialLine>();
		
		for(Line l : maximal_lines){
			// potential_lines.put(l, l.getNum_points());
		}
		
		return potential_lines;
	}
	
	private HashMap<Point, ArrayList<Line>> populate_unused_points(ArrayList<Point> pointSet){
		
		
		HashMap<Point, ArrayList<Line>> unused_points = new HashMap<Point, ArrayList<Line>>();
		
		// Iterate through all lines and match every point in every line with 
		// all the lines that point is a part of
		
		for(Point p : pointSet){					
			unused_points.put(p, new ArrayList<Line>());			
		}
		
		for (Line l : maximal_lines){
			for(Point p : l.getAllPoints()){
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

	private void remove_opposite_end(Line workingSet,
			boolean direction) {

		// Remove the <oposite-direction>-most point from the working set
		if (direction == LEFT) {
			// Erase the end of the working set
			workingSet.getAllPoints().remove(workingSet.getAllPoints().size() - 1);
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

	private void update_auxlist(ArrayList<Line> auxlist,
			Line workingSet) {

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
			// TODO: check that ^

			workingSet.add_point(next_point);
			return true;
		}

		return false;

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
			mark_pair(line.getAllPoints().get(i), line.getAllPoints().get(i + 1));
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

	private Point get_next_point_guess(Line workingSet,
			boolean direction) {

		Point A = null;
		Point B = null;

		if (direction == LEFT) {
			A = workingSet.getAllPoints().get(0);
			B = workingSet.getAllPoints().get(1);
		}

		if (direction == RIGHT) {
			A = workingSet.getAllPoints().get(workingSet.getAllPoints().size() - 1);
			B = workingSet.getAllPoints().get(workingSet.getAllPoints().size() - 2);
		}

		return A.add(A.subtract(B));
	}

}
