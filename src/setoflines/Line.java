package setoflines;

import java.util.ArrayList;

public class Line implements Comparable<Line> {

	private final boolean LEFT = false;
	private final boolean RIGHT = true;
	
	private Point initial_point;
	private Point second_point;

	private ArrayList<Point> all_points;

	private int num_points = 0;

	public Line(Point initial_point, Point second_point) {
		this.initial_point = initial_point;
		this.second_point = second_point;

		all_points = new ArrayList<Point>();		
	}

	public Line(Line line) {
		this.initial_point = line.getInitial_point();
		this.second_point = line.getSecond_point();
		this.all_points = new ArrayList<Point>(line.getAllPoints());
		this.num_points = line.getNum_points();
	}

	public void add_point(Point point, boolean direction) {
		
		if(direction == RIGHT){
			all_points.add(point);
		}else{
			all_points.add(0, point);
		}
		
		this.num_points++;
	}

	public ArrayList<Point> getAllPoints() {
		return this.all_points;
	}

	public Point getInitial_point() {
		return initial_point;
	}

	public void setInitial_point(Point initial_point) {
		this.initial_point = initial_point;
	}

	public Point getSecond_point() {
		return second_point;
	}

	public void setSecond_point(Point second_point) {
		this.second_point = second_point;
	}

	public int getNum_points() {
		return num_points;
	}

	public void setNum_points(int num_points) {
		this.num_points = num_points;
	}	

	@Override
	public int compareTo(Line o) {
		// TODO Auto-generated method stub
		return this.num_points - o.num_points;
	}

	public String toString(){
		return "Initial Point: " + initial_point + " | Second Point: " + second_point + " | Num points: " + num_points + "\n";
	}
}
