package testframework;

import java.util.ArrayList;

import setoflines.Pair;
import setoflines.Point;

public class TestSuite {
	
	ArrayList<Test> test_suite;

	public TestSuite() {

		test_suite = new ArrayList<Test>();	
		
		double[] a20x20 = {20,20};
		double[] a32x32 = {100,100};
		double[] a100x100 = {100,100};
		
		ArrayList<double[]> grids = new ArrayList<double[]>();
		grids.add(a20x20);
		grids.add(a32x32);
		grids.add(a100x100);	
		
		ArrayList<Double> sparsity_options = new ArrayList<Double>();
		sparsity_options.add(1.0);
		sparsity_options.add(0.7);
		sparsity_options.add(0.5);
		sparsity_options.add(0.2);
		
		ArrayList<Double> noise_options = new ArrayList<Double>();
		noise_options.add(0.0);
		noise_options.add(0.2);
		noise_options.add(0.5);
		noise_options.add(0.7);
		
		
		// Adjust grid size
		for(double[] grid_size : grids){
			
			// Adjust sparsity
			for(Double s : sparsity_options){
				double sparsity = s.doubleValue();
				
				// Adjust noise 
				for (Double n : noise_options){
					double noise = n.doubleValue();
					
					test_suite.add(new Test(sparsity, noise, grid_size));
					
				}
				
			}			
			
		}	

	}
	
	public ArrayList<Test> getAllTests(){
		return this.test_suite;
	}

}
