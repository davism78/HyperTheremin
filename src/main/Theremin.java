package main;

import graphics.GraphicsModel;
import graphics.GraphicsView;

import java.io.IOException;

import leapControl.ThereminListener;

import org.lwjgl.LWJGLException;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Gesture;


public class Theremin {
	
	private static GraphicsView gui;
	private static Controller controller;
	private static ThereminListener listener;
	private static boolean clean = false;
	
	private static Process PDSubprocess;
	
	public static void main(String[] args) throws LWJGLException {
		// Setup the display
		GraphicsModel graphics = new GraphicsModel();
		gui = new GraphicsView(graphics);
		
		//launchPuredata();
		
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

	private static void launchPuredata() {
		// auto start PD with our pd file
		// we can use java.io.runtime:
		Runtime rt = Runtime.getRuntime();
		
		String url = Theremin.class.getResource("").getPath();

		System.out.println(url);
		
		String pdsrcurl = url.substring(1) + "..\\..\\pd_src\\theremin.pd";
		String pdurl = url + "../../../../WindowsFolders/Pd-0.43.4-extended-windowsxp-i386/Pd-0.43.4-extended/bin/pd.exe";
		
		try {
			PDSubprocess = rt.exec(pdurl + " " + pdsrcurl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void exit() {
		listener.cleanup();
		controller.removeListener(listener);
		gui.cleanup();
		
		if (PDSubprocess != null){
			try {
				PDSubprocess.exitValue();
			} catch (IllegalThreadStateException e) {
				PDSubprocess.destroy();
				System.out.println("PD destroyed");
			} 
			// if exception not thrown, process has already terminated
		}
		clean = true;
	}
}
