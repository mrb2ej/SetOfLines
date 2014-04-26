package setoflines;

public class Pair {

	Point first, second;
	
	public Pair (Point first, Point second) throws Exception{
		
		if(first.getDimension() != second.getDimension()){
			throw new Exception ("Point dimensions do not match!");
		}
		
		this.first = first;
		this.second = second;
	}
	 
	
	public int hashCode(){
		//int returnCode = 0;
		
		/*
		for (double posX : first.getCoordinates()){
			for (double posY : second.getCoordinates()){
				returnCode += Double.valueOf(posX).hashCode() * Double.valueOf(posY).hashCode();
			}
		}
		*/
		
		return first.hashCode() ^ second.hashCode();
	}
	
	
	public Point getFirst() {
		return first;
	}


	public void setFirst(Point first) throws Exception {
		
		if(first.getDimension() != this.second.getDimension()){
			throw new Exception ("Point dimensions do not match!");
		}
		
		this.first = first;
	}


	public Point getSecond() {
		return second;
	}


	public void setSecond(Point second) throws Exception {
		
		if(this.first.getDimension() != second.getDimension()){
			throw new Exception ("Point dimensions do not match!");
		}
		
		this.second = second;
	}


	public boolean equals (Object o){
		
		if (!(o instanceof Pair)){
			return false;
		}
		
		Pair otherpair = (Pair) o;
		
		return (first.equals(otherpair.first) && second.equals(otherpair.second)) 
				|| (first.equals(otherpair.second) && (second.equals(otherpair.first)));	
		
	}
}
