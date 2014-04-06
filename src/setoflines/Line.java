package setoflines;

public class Line {
	
	private Point initial_point;
	private Point second_point;
	private int num_points;
	
	public Line (Point initial_point, Point second_point, int num_points){
		this.initial_point = initial_point;
		this.second_point = second_point;
		this.num_points = num_points;
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
	
}
