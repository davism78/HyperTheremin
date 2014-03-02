package graphics;

import java.util.Collections;

import leapControl.HandData;

import org.newdawn.slick.TrueTypeFont;
public class GraphicsModel {
	
	private TrueTypeFont font;
	
	// These fields contain data about each hand.
	// each hand should have 
	private HandData pitchHand;
	private HandData levelHand;
	
	private TuningData tuningData;
	private ThereminMode currentMode;
	
	// MainMenu
	private MainMenuData menuData;
	
	// Recording
    private boolean recording;
	
	public GraphicsModel() {
		tuningData = new TuningData();
		menuData = new MainMenuData();
		
		pitchHand = new HandData(0,0);
		levelHand = new HandData(0,0);

		// default to menu mode
		currentMode = ThereminMode.MENU;
		recording = true;
		
	}
	
	public void setRightHand(HandData data){
		pitchHand = data;
	}
	
	public void setLeftHand(HandData data){
		levelHand = data;
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
		return pitchHand.getData();
	}
	
	public void setPitch(double pitch){
		pitchHand.setData(pitch);
	}

	public double getVolume() {
		return levelHand.getData();
	}

	public void setVolume(double volumeLevel) {
		levelHand.setData(volumeLevel);
	}
	
	/***********************************************
	 * Methods to retreive position data in pixels, 
	 * not in millimeters.
	 ***********************************************/
	
	public double getPitchPosition() {
		// convert pitch HandData to pixel value.
		double slope = 290.0 / 350.0;
		return slope * pitchHand.getPos() + 370.0; 
	}
	
	public double getVolumePosition() {
		// convert volume HandData to pixel value.
		double slope = -480.0 / 575.0;
		return slope * (levelHand.getPos() - 600.0);
	}
	
	/********************************************
	 * Methods involved with tuning the theremin
	 ********************************************/
	
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

	/******************************************
	 * Main Menu methods
	 ******************************************/
	
	public MainMenuData getMenuData() {
		return menuData;
	}
	
	/******************************************
	 * Recording methods
	 ******************************************/
	
	public boolean isRecording() {
		return recording;
	}
	
	public void flipRecording() {
		recording = !recording;
	}
}
