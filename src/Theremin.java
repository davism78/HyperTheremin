
import graphics.GraphicsModel;
import leapControl.ThereminListener;

import org.lwjgl.LWJGLException;

import com.leapmotion.leap.Controller;


class Theremin {
	public static void main(String[] args) throws LWJGLException {
		// Create a sample listener and controller
		ThereminListener listener = new ThereminListener();
		Controller controller = new Controller();
		GraphicsModel graphics = new GraphicsModel();

		// Have the sample listener receive events from the controller
		controller.addListener(listener);

		// run the graphics
		graphics.runDisplay();

		// cleanup
		controller.removeListener(listener);
		graphics.cleanup();
	}

}
