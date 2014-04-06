package setoflines;

import java.util.ArrayList;
import java.util.HashSet;

public class SetOfLines {

	private final boolean LEFT = false;
	private final boolean RIGHT = true;
	
	
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
		
		for(int i = 0; i < pointSet.size(); i++)
	    {
	        for(int j = i + 1; j < pointSet.size(); j++)
	        {
	            Pair new_pair;
				try {
					new_pair = new Pair(pointSet.get(i), pointSet.get(j));					
					unmarked_pairs.add(new_pair); // money in the bank
					
				} catch (Exception e) {
					// Dimensions don't match exception
					e.printStackTrace();
				}	            
	        }
	    }
		
	}
	
	
	private void initialize(ArrayList<Point> workingSet){
		
		// Mark the working set
		mark_pair(workingSet.get(0), workingSet.get(1));
		
		while(extend(workingSet, LEFT)){
			// Keep extending
		}
		
		while(extend(workingSet, RIGHT)){
			// Keep extending
		}
	}
	
	private void march(ArrayList<Point> workingSet, boolean direction){
		
	}
	
	private boolean extend(ArrayList<Point> workingSet, boolean direction){
		
		// Check the kd tree for points within the 8e box
		// if point exists, add the point to the working set					
		
		Point next_point = get_next_point(get_next_point_guess(workingSet, direction));
		
		if(next_point != null){
			// Check if candidate point fits the line
			// TODO: check that
			
			workingSet.add(next_point);
			return true;
		}
		
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
		// TODO: Use kd-tree library that we don't have yet
		return null;
		
	}
	
	private Point get_next_point_guess(ArrayList<Point> workingSet, boolean direction){
		
		Point A = null;
		Point B = null;
		
		if(direction == LEFT){
			A = workingSet.get(0);
			B = workingSet.get(1);			
		}
		
		if(direction == RIGHT){
			A = workingSet.get(workingSet.size() - 1);
			B = workingSet.get(workingSet.size() - 2);			
		}
		
		return A.add( A.subtract(B) );
	}
	
}
