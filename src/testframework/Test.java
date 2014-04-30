package testframework;

public class Test {	
	
	private double sparsity;
	private double noise; 
	private double[] grid_size;
	
	public Test(double sparsity, double noise, double[] grid_size){		
		this.sparsity = sparsity;
		this.noise = noise;		
		this.grid_size = grid_size;
	}
	
	public double[] getGrid_size() {
		return grid_size;
	}	

	public double getSparsity() {
		return sparsity;
	}

	public void setSparsity(double sparsity) {
		this.sparsity = sparsity;
	}

	public double getNoise() {
		return noise;
	}

	public void setNoise(double noise) {
		this.noise = noise;
	}	
}
