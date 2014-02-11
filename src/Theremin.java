
import graphics.GraphicsModel;
import graphics.GraphicsView;
import leapControl.ThereminListener;

import org.lwjgl.LWJGLException;

import com.leapmotion.leap.Controller;


class Theremin {
	public static void main(String[] args) throws LWJGLException {
		// Setup the display
		GraphicsModel graphics = new GraphicsModel();
		GraphicsView gui = new GraphicsView(graphics);
		
		// Create a sample listener and controller
		ThereminListener listener = new ThereminListener(graphics);
		Controller controller = new Controller();
		// Have the sample listener receive events from the controller
		controller.addListener(listener);

		// run the graphics
		graphics.setPitch(1.3e10);
		gui.runDisplay();

		// cleanup
		controller.removeListener(listener);
		gui.cleanup();
	}

}
