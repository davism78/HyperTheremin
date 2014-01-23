import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;

import java.io.IOException;

public class ThereminListener extends Listener {
	private static final double SCALE = 30.0;
	private static final double OFFSET = 25.0;
	private static final double CZERO = 16.25;
	
    public void onInit(Controller controller) {
        System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }
    
    public void onDisconnect(Controller controller) {
        //Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }
    
    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    /*
     * This method is intended to transform a finger point position into an
     * audio frequency that can be used as input to an oscillator.
     * (non-Javadoc)
     * @see com.leapmotion.leap.Listener#onFrame(com.leapmotion.leap.Controller)
     */
    public void onFrame(Controller controller) {
    	Frame frame = controller.frame();
    	if (!frame.hands().isEmpty()) {
    		Hand hand = frame.hands().get(0);
    		FingerList fingers = hand.fingers();
            if (!fingers.isEmpty()) {
            	Vector v = fingers.get(0).tipPosition();
            	float Yval = v.getY();
            	double tone = CZERO * Math.pow(2, ((Yval - OFFSET) / SCALE));
            	System.out.println(tone);
            }
    	}
    }
}

class Theremin {
	public static void main(String[] args) {
        // Create a sample listener and controller
        ThereminListener listener = new ThereminListener();
        Controller controller = new Controller();

        // Have the sample listener receive events from the controller
        controller.addListener(listener);

        // Keep this process running until Enter is pressed
        System.out.println("Press Enter to quit...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Remove the sample listener when done
        controller.removeListener(listener);
    }

}
