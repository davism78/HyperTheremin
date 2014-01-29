package leapControl;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class ThereminListener extends Listener {
	private static final double SCALE = 30.0;
	private static final double OFFSET = 25.0;
	private static final double CZERO = 16.25;
	private OSCConnection pitchConnection;
	
    public void onInit(Controller controller) {
    	pitchConnection = new OSCConnection(8000, "/note");
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
            	
            	if(!pitchConnection.sendPitch(tone, 75)){
            		System.out.println("ERROR: message did not send");
            	}
            }
    	}
    }
    
}
