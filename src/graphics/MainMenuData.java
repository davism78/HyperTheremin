package graphics;

import org.lwjgl.opengl.Display;

public class MainMenuData {
	private ThereminMode selectedState;
	private double handXPos;
	private double handYPos;
	
	// window data
	public int windowLineX;
	public int windowLineY;
	
	public MainMenuData() {
		selectedState = ThereminMode.MENU;
		handXPos = 0.0;
		handYPos = 0.0;
		windowLineX = Display.getWidth() / 2;
		windowLineY = Display.getHeight() / 2;
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
		if(x < windowLineX) {
			if(y < windowLineY) {
				selectedState = ThereminMode.PLAY;
			} else {
				selectedState = ThereminMode.PLAY; // SETTINGS
			}
		} else {
			if(y < windowLineY) {
				selectedState = ThereminMode.TUNE;
			} else {
				selectedState = ThereminMode.TUNE; // EXIT
			}
		}
		handXPos = x;
		handYPos = y;
		
		System.out.println("MenuMode = " + selectedState);
	}
	
}
