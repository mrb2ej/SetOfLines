package setoflines;

import java.util.ArrayList;
import java.util.HashSet;
import net.sf.javaml.core.kdtree.KDTree;

public class SetOfLines {

	private final boolean LEFT = false;
	private final boolean RIGHT = true;

	private HashSet<Pair> unmarked_pairs = new HashSet<Pair>();

	private ArrayList<ArrayList<Point>> maximal_lines = new ArrayList<ArrayList<Point>>();

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
		generate_set_of_lines();
		
		// Clear all unnecessary data structures
		maximal_lines = null;
		unmarked_pairs = null;
		kdtree = null;		

	}
	
	private void generate_set_of_lines(){
		
		
	}
	
	
	private void generate_maximal_lines() {
		
		while (!unmarked_pairs.isEmpty()) {

			// Create new working set and new auxlist
			ArrayList<Point> workingSet = new ArrayList<Point>();
			ArrayList<ArrayList<Point>> auxlist = new ArrayList<ArrayList<Point>>();

			// Get the current pair
			Pair current_pair = unmarked_pairs.iterator().next();

			// Add current pair to working set
			workingSet.add(current_pair.getFirst());
			workingSet.add(current_pair.getSecond());

			// Initialize the working set for compression
			initialize(workingSet);

			// Copy the un-marched working set
			ArrayList<Point> workingSetCopy = new ArrayList<Point>(workingSet);

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
		for(Point p : pointSet){
			
			ArrayList<Double> coordinates = p.getCoordinates();
			
			double[] key = new double[coordinates.size()];
			
			for(int i = 0; i < coordinates.size(); i++){
				key[i] = coordinates.get(i); 
			}
					
			kdtree.insert(key, p);
		}
		
	}

	private void generate_pairs(ArrayList<Point> pointSet) {

		// Generate all pairs of points in the point set and insert into unmarked_pairs
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

	private void initialize(ArrayList<Point> workingSet) {

		// Mark the working set
		mark_pair(workingSet.get(0), workingSet.get(1));

		while (extend(workingSet, LEFT)) {
			// Keep extending
		}

		while (extend(workingSet, RIGHT)) {
			// Keep extending
		}
	}

	private void remove_opposite_end(ArrayList<Point> workingSet,
			boolean direction) {

		// Remove the <oposite-direction>-most point from the working set
		if (direction == LEFT) {
			// Erase the end of the working set
			workingSet.remove(workingSet.size() - 1);
		} else {
			// Erase the beginning of the working set
			workingSet.remove(0);
		}
	}

	private void march(ArrayList<Point> workingSet, boolean direction) {

		// Try removing the end of the epsilon sequence to see if the 
		// sequence can be extended any further 
		
		remove_opposite_end(workingSet, direction);

		while (workingSet.size() > 1) {

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

	private void update_auxlist(ArrayList<ArrayList<Point>> auxlist,
			ArrayList<Point> workingSet) {

		// Copy the working set
		ArrayList<Point> new_working_set = new ArrayList<Point>(workingSet);

		// Add the working set copy to the set of maximal lines
		auxlist.add(new_working_set);
	}

	private boolean extend(ArrayList<Point> workingSet, boolean direction) {

		// Check the kd tree for points within the 8e box
		// if point exists, add the point to the working set

		Point next_point = get_next_point(get_next_point_guess(workingSet,
				direction));

		if (next_point != null) {
			// Check if candidate point fits the line
			// TODO: check that ^

			workingSet.add(next_point);
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

	private void mark_auxlist(ArrayList<ArrayList<Point>> auxlist) {
		for (ArrayList<Point> line : auxlist) {
			mark_line(line);
		}
	}

	private void mark_line(ArrayList<Point> line) {
		for (int i = 0; i < line.size() - 2; i++) {
			mark_pair(line.get(i), line.get(i + 1));
		}
	}

	private Point get_next_point(Point next_point_guess) {
				
		double[] lower_bound = new double[dimension];
		double[] upper_bound = new double[dimension];
		
		for(int i = 0; i < next_point_guess.getDimension(); i++){
			upper_bound[i] = next_point_guess.getCoordinates().get(i) + 8 * epsilon;
			lower_bound[i] = next_point_guess.getCoordinates().get(i) - 8 * epsilon;
		}			
		
		Object[] returned_value = kdtree.range(lower_bound, upper_bound);
		
		if (returned_value.length == 0) return null;
		
		return (Point) returned_value[0];

	}

	private Point get_next_point_guess(ArrayList<Point> workingSet,
			boolean direction) {

		Point A = null;
		Point B = null;

		if (direction == LEFT) {
			A = workingSet.get(0);
			B = workingSet.get(1);
		}

		if (direction == RIGHT) {
			A = workingSet.get(workingSet.size() - 1);
			B = workingSet.get(workingSet.size() - 2);
		}

		return A.add(A.subtract(B));
	}

}
