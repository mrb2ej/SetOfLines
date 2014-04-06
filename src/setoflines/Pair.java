package setoflines;

public class Pair {

	Point first, second;
	
	public Pair (Point first, Point second){
		this.first = first;
		this.second = second;
	}
	 
	
	//Add Hash function 
	
	
	public Point getFirst() {
		return first;
	}


	public void setFirst(Point first) {
		this.first = first;
	}


	public Point getSecond() {
		return second;
	}


	public void setSecond(Point second) {
		this.second = second;
	}


	public boolean equals (Object o){
		return false;
		
	}
}
