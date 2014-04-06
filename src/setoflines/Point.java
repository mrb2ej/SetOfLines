package setoflines;

import java.util.ArrayList;

public class Point {
	
	private int dimension;
	private ArrayList<Double> position;
	
	public Point (int dimension, ArrayList<Double> position){
		this.dimension = dimension;
		this.position = new ArrayList<Double>(position);
	}
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public ArrayList<Double> getPosition() {
		return position;
	}

	public void setPosition(ArrayList<Double> position) {
		this.position = position;
	}
	
	
	public boolean equals (Object o){
		
		if (!(o instanceof Point)){
			return false;
		}
		
		Point otherpoint = (Point) o;
		
		for (int i = 0; i < this.getDimension(); i++ ){
			if (this.getPosition().get(i) != otherpoint.getPosition().get(i)){
				return false;
			}
		}
		
		return true;
		
	}
	

}
