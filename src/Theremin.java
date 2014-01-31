
import leapControl.ThereminListener;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.leapmotion.leap.Controller;


class Theremin {
	public static void main(String[] args) throws LWJGLException {
        // Create a sample listener and controller
        ThereminListener listener = new ThereminListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);
        
        //LWJGL
        Display.setDisplayMode(new DisplayMode(400, 400));
        Display.create();
        while(!Display.isCloseRequested()) {
        	Display.update();
        }
        controller.removeListener(listener);
        Display.destroy();
    }

}
