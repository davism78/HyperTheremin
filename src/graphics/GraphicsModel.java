package graphics;

import leapControl.HandData;

import org.newdawn.slick.TrueTypeFont;

import static graphics.ThereminMode.*;

public class GraphicsModel {
	
	private TrueTypeFont font;
	
	// These fields contain data about each hand.
	// each hand should have 
	private HandData pitchHand;
	private HandData levelHand;

	// These will be deprecated
	private double pitchValue;
	private double volumeLevel;
	private TuningData tuningData;
	private ThereminMode currentMode;

	public GraphicsModel() {
		pitchValue = GraphicsUtils.DEFAULT_VAL;
		volumeLevel = GraphicsUtils.DEFAULT_VAL;
		tuningData = new TuningData();
		
		// default to play mode
		currentMode = PLAYMODE;
	}
	
	public ThereminMode setMode(ThereminMode mode){
		ThereminMode old = mode;
	    currentMode = mode;
		return old;
	}
	
	public ThereminMode getMode(){
		return currentMode;
	}

	public void setupFont() {
		font = GraphicsUtils.getDefaultFont();
	}
	
	public TrueTypeFont getFont() {
		return font;
	}
	
	public void setFont(TrueTypeFont newFont) {
		this.font = newFont;
	}
	
	public double getPitch() {
		return pitchValue;
	}
	
	public void setPitch(double v){
		pitchValue = v;
	}

	public double getVolume() {
		return volumeLevel;
	}

	public void setVolume(double volumeLevel) {
		this.volumeLevel = volumeLevel;
	}
	
	// Methods involved with tuning the theremin
	
	public void setLeftAndRightTuningFingers(double left, double right) {
		this.tuningData.setLeftFingerPos(left);
		this.tuningData.setRightFingerPos(right);
	}
	
	public double getLeftTuningFinger() {
		return this.tuningData.getLeftFingerPos();
	}
	
	public double getRightTuningFinger() {
		return this.tuningData.getRightFingerPos();
	}
	
	public void setScale(double scale) {
		this.tuningData.setScale(scale);
	}
	
	public double getScale() {
		return this.tuningData.getScale();
	}
}
