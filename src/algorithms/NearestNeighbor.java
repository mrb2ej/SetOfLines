package algorithms;

import setoflines.Line;
import setoflines.Point;
import setoflines.SetOfLines;

public class NearestNeighbor 	{

	public static Point NearestNeighborSearch(SetOfLines setoflines, Point point){
		
		Point current_closest_point = null;
		double current_distance = 0.0;
		
		for(Line line : setoflines.get_set_of_lines()){
			Point check_point = getClosestPoint(line, point);	
			
			double check_distance = getSquaredDistanceBetweenPoint(point, check_point);
			
			if(check_distance < 0){
				return null;
			}
			
			if(current_closest_point == null ||  check_distance < current_distance){
				current_closest_point = check_point;
				current_distance = check_distance;
			}						
		}		
		
		return current_closest_point;
	}	
	
	private static Point getClosestPoint(Line line, Point point){
		
		Point x1 = line.getInitial_point();
		Point x2 = line.getSecond_point();
		
		double numerator = dotProduct( (x1.subtract(point)), (x2.subtract(x1)));
		double denom = dotProduct( (x2.subtract(x1)), (x2.subtract(x1)));
		double t = -1.0 * (numerator / denom);			
		
		if( t < 0 ){
			t = 0;
		}else if ( t > line.getNum_points() - 1 ){
			t = line.getNum_points() - 1; 
		}		
		
		t = Math.round(t);		
		
		// (1 - t)*x1 + t*x2	
		return x1.scalarMult(1 - t).add(x2.scalarMult(t));
	}
	
		
	private static double dotProduct(Point p1, Point p2){
		
		if(p1.getDimension() != p2.getDimension()){
			return -1.0;
		}
		
		double sum = 0.0;
		
		for(int i = 0; i < p1.getDimension(); i++){
			sum += p1.getCoordinates().get(i) * p2.getCoordinates().get(i);
		}
		
		return sum;		
	}
	
	private static double getSquaredDistanceBetweenPoint(Point p1, Point p2){
		
		if(p1.getDimension() != p2.getDimension()){
			return -1.0;
		}
		
		double distance = 0.0;
		
		for(int i = 0; i < p1.getDimension(); i++){
			distance += Math.pow(p1.getCoordinates().get(i) - p2.getCoordinates().get(i), 2);
		}
		
		return distance;
	}

}
