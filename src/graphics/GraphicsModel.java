package graphics;

import org.newdawn.slick.TrueTypeFont;
import static graphics.ThereminMode.*;

public class GraphicsModel {
	
	private TrueTypeFont font;
	
	// TODO create HandData object containing pitch/volume and position
	// these fields are those objects
	private double pitchValue;
	private double volumeLevel;
	
	private int scaleValue; // tuning value
	private ThereminMode currentMode;

	public GraphicsModel() {
		pitchValue = GraphicsUtils.DEFAULT_VAL;
		volumeLevel = GraphicsUtils.DEFAULT_VAL;
		currentMode = PLAYMODE;
	}
	
	public void setMode(ThereminMode mode){
		currentMode = mode;
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
	
}
