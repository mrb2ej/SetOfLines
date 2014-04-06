package setoflines;

import java.util.ArrayList;
import java.util.HashSet;

public class SetOfLines {

	private HashSet<Pair> marked_pairs = new HashSet<Pair>();
	private HashSet<Pair> unmarked_pairs = new HashSet<Pair>();
	
	private double epsilon;
	private int dimension;
	// Add kd-tree
	
	
	public SetOfLines(ArrayList<Point> pointSet, double epsilon, int dimension){
		this.epsilon = epsilon;
		this.dimension = dimension;
		
		
	}
	
	private void generate_tree(ArrayList<Point> pointSet){
		// TODO: Add kd-tree library for Java
	}
	
	private void generate_pairs(ArrayList<Point> pointSet){
		
	}
	
	
	private void initialize(ArrayList<Point> workingSet){
		
	}
	
	private void march(ArrayList<Point> workingSet, boolean direction){
		
	}
	
	private boolean extend(ArrayList<Point> workingSet, boolean direction){
		return false;
		
	}
	
	private void mark_pair(Point first, Point second){
		
		Pair new_pair;
		
		try {
			new_pair = new Pair(first, second);
			
			// remove from unmarked
			unmarked_pairs.remove(new_pair);
			
			// add to marked
			marked_pairs.add(new_pair);
		} catch (Exception e) {
			// Dimensions don't match
			e.printStackTrace();
		}		
	}
	
	private Point get_next_point(Point next_point_guess){
		return null;
		
	}
	
}
