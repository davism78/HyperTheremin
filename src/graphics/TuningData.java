package graphics;

/**
 * TuningData contains information needed for tuning the theremin
 * as well as for determining the current scale value. 
 * The scale value of the theremin is a mapping between distance
 * measured by the LeapMotion to changes in pitch. With a larger
 * scale value, the theremin changes pitch slower.
 */
public class TuningData {
	private double leftFingerPos;
	private double rightFingerPos;
	private double scale;
	
	public TuningData() {
		this.setLeftFingerPos(0.0);
		this.setRightFingerPos(0.0);
		this.setScale(0.0);
	}

	public double getLeftFingerPos() {
		return leftFingerPos;
	}

	public void setLeftFingerPos(double leftFingerPos) {
		this.leftFingerPos = leftFingerPos;
	}

	public double getRightFingerPos() {
		return rightFingerPos;
	}

	public void setRightFingerPos(double rightFingerPos) {
		this.rightFingerPos = rightFingerPos;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
	
	
}
