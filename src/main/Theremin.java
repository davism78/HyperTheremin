package main;

import graphics.GraphicsModel;
import graphics.GraphicsView;
import leapControl.ThereminListener;

import org.lwjgl.LWJGLException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;


public class Theremin {
	
	private static GraphicsView gui;
	private static Controller controller;
	private static ThereminListener listener;
	private static boolean clean = false;
	
	public static void main(String[] args) throws LWJGLException {
		// Setup the display
		GraphicsModel graphics = new GraphicsModel();
		gui = new GraphicsView(graphics);
		
		// TODO: auto start PD with our pd file
		// we can use java.io.runtime:
		// Runtime rt = Runtime.getRuntime();
		// Process ps = rt.exec("path/to/pd.exe path/to/src.pd");
		
		// Create a theremin listener and controller
		listener = new ThereminListener(graphics);
		controller = new Controller();
		// Have the sample theremin receive events from the controller
		
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP); // for tuning
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);     // for tuning
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);    // for recording
		controller.addListener(listener);

		// run the graphics
		graphics.setPitch(1.3e10);
		gui.runDisplay();

		// cleanup
		if(!clean) {
			exit();
		}
	}

	public static void exit() {
		listener.cleanup();
		controller.removeListener(listener);
		gui.cleanup();
		clean = true;
	}
}
