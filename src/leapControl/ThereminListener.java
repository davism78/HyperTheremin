package leapControl;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
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
     * Evaluate fingers position into a double value representing a pitch
     */
    private double getTone(Finger finger) {
       	Vector v = finger.tipPosition();
       	float Yval = v.getY();
       	double tone = CZERO * Math.pow(2, ((Yval - OFFSET) / SCALE));
       	return tone;
    }
    
    /*
     * Evaluate a fingers position into an int value representing volume level
     * 
     * TODO this method need much more thought out implementation
     */
    private int getLevel(Finger finger) {
       	Vector v = finger.tipPosition();
       	float Yval = v.getY();
       	int level = (int)Yval;
       	if (Yval > 100.0)
       		return 100;
       	return level;
    }
    
    /*
     * This method is intended to transform a finger point position into an
     * audio frequency that can be used as input to an oscillator.
     * (non-Javadoc)
     * @see com.leapmotion.leap.Listener#onFrame(com.leapmotion.leap.Controller)
     */
    public void onFrame(Controller controller) {
    	Frame frame = controller.frame();
    	int level = 0;
    	double tone = 0;
    	
    	System.out.println("#Hands: " + frame.hands().count());
    	// Process hands
    	switch (frame.hands().count()) {
    	case 0:
    		break;
    	case 1:
    		// Use constant level
    		level = 75;
    		Hand hand = frame.hands().get(0);
    		FingerList fingers = hand.fingers();
    		if (!fingers.isEmpty()){
    			// should be a loop over all fingers
    			tone = getTone(fingers.get(0));
    		}
    		break;
    	default:  // TODO handle more than two hands
    		FingerList levels = frame.hands().get(0).fingers();
    		FingerList tones = frame.hands().get(1).fingers();
    		if (!levels.isEmpty()){
    			//TODO level should be determined by overall hand position, not based on finger
    			level = getLevel(levels.get(0));
    		}
    		if (!tones.isEmpty()){
    			//TODO should be a loop over all fingers
    			tone = getTone(tones.get(0));
    		}
    		break;
    	}

    	System.out.println(tone);
    	if(!pitchConnection.sendPitch(tone, level)){
    		System.out.println("ERROR: message did not send");
    	}  
    } 
}


