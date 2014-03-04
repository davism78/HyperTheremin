package graphics;

import org.lwjgl.opengl.Display;

public class MainMenuData {
	private ThereminMode selectedState;
	// Stored as pixels
	private double handXPos;
	private double handYPos;
	
	// window data
	public int windowLineX;
	public int windowLineY;
	
	public MainMenuData() {
		selectedState = ThereminMode.MENU;
		handXPos = 0.0;
		handYPos = 0.0;
	}
	
	public ThereminMode getSelectedState() {
		return selectedState;
	}

	public double getHandXPos() {
		return handXPos;
	}

	public double getHandYPos() {
		return handYPos;
	}

	public void updateMainMenu(double x, double y) {
		windowLineX = Display.getWidth() / 2;
		windowLineY = Display.getHeight() / 2;
		x = normalizeX(x);
		y = normalizeY(y);
		//System.out.println("X = " + x + " Y = " + y);
		//System.out.println("Wx = " + windowLineX  + " Wy = " + windowLineY);
		
		
		if(x < windowLineX) {
			if(y < windowLineY) {
				selectedState = ThereminMode.PLAY;
			} else {
				selectedState = ThereminMode.PLAYBACK; // SETTINGS
			}
		} else {
			if(y < windowLineY) {
				selectedState = ThereminMode.TUNE;
			} else {
				selectedState = ThereminMode.EXIT; // EXIT
			}
		}
		handXPos = x;
		handYPos = y;
		
		//System.out.println("MenuMode = " + selectedState);
	}

	private double normalizeX(double x) {
		double slope = 740.0 / 700.0;
		return slope * x + 370.0;
	}

	private double normalizeY(double y) {
		double slope = -600 / 575;
		return slope * (y - 600);
	}
	
}
