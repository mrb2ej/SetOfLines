#include <vector>

using namespace std;

class Pair 
{

public: 
	Point(int dimension, vector<double> position);
	int getDimension()
	void setDimension(int dimension);
    vector<double> getCoordinates();
    void setCoordinates(vector<double> position);
    Point* add(Point* p1);
    Point* scalarMult(double scalar);
    Point* subtract(Point* p1);
	
	// equals, hashCode, and toString 
	

private:
	int dimension;
	vector<double> coordinates;
	
};
