package leapControl;

public class HandData {
	private double data;
	private double position;
	
	public HandData(double data, double position){
		// sanity checks
		assert(data >= 0);
		//assert(position >= 0);
		
		this.data = data;
		this.position = position;
	}
	
	public double getData(){
		return data;
	}
	
	public void setData(double data){
		this.data = data;
	}

	public double getPos(){
		return position;
	}
}
