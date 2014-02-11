package graphics;

import org.newdawn.slick.TrueTypeFont;

public class GraphicsModel {
	
	private TrueTypeFont font;
	private double pitchValue;
	private double volumeLevel;

	public GraphicsModel() {
		pitchValue = GraphicsUtils.DEFAULT_VAL;
		volumeLevel = GraphicsUtils.DEFAULT_VAL;
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
