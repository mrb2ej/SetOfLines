package setoflines;

import java.util.ArrayList;

public class Point {
	
	private int dimension;
	private ArrayList<Double> coordinates;
	
	public Point (int dimension, ArrayList<Double> position){
		this.dimension = dimension;
		this.coordinates = new ArrayList<Double>(position);
	}
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public ArrayList<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(ArrayList<Double> position) {
		this.coordinates = position;
	}
	
	public Point add (Point p1){
		
		ArrayList<Double> coordinates = new ArrayList<Double>();
		
		for(int i = 0; i < this.getDimension(); i++){
			coordinates.add(this.getCoordinates().get(i) + p1.getCoordinates().get(i));
		}
		
		return new Point(this.getDimension(), coordinates);
	}
	
	public Point subtract (Point p1){
		
		ArrayList<Double> coordinates = new ArrayList<Double>();
		
		for(int i = 0; i < this.getDimension(); i++){
			coordinates.add(this.getCoordinates().get(i) - p1.getCoordinates().get(i));
		}
		
		return new Point(this.getDimension(), coordinates);
	}
	
	
	public boolean equals (Object o){
		
		if (!(o instanceof Point)){
			return false;
		}
		
		Point otherpoint = (Point) o;
		
		for (int i = 0; i < this.getDimension(); i++ ){
			if (this.getCoordinates().get(i) != otherpoint.getCoordinates().get(i)){
				return false;
			}
		}
		
		return true;
		
	}
	
	public String toString(){
		return coordinates.toString();
	}
	

}
