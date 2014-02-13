package leapControl;

import graphics.GraphicsModel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import static graphics.ThereminMode.*;

//TODO: fix up indentation
public class ThereminListener extends Listener {
	public static final boolean DEBUG = false;
	
	
	//TODO: this should move to a reference value in graphicsModel
	private  double SCALE = 40.0;   // will be determined by tuning
	
	private static final double OFFSET = 25.0; // Leap motion min sensitivity 
	private static final double MAXFREQ = 20000.0; // freq when touching antennae
	
	private static final double ANTENNAE = 350.0;  //the distance of the virtual antennae from the origin

	private OSCConnection pitchConnection;
	private GraphicsModel graphicsModel;
	
	public ThereminListener(GraphicsModel model) {
		this.graphicsModel = model;
	}
	
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
    
    private static void printDebug(String message){
    	if (DEBUG){
    		System.out.println(message);
    	}
    }
    
    public void setPitchScale(double scale){
    	SCALE = scale;
    }
    
    /*
     * Evaluate hands position into a double value representing a pitch
     * Pitch is determined by distance from virtual antennae which is calculated here
     * The virtual antennae location is specified by ANTENNAE
     * 
     * The closest finger to the antennae will be used
     * 
     * The pitch returned will always be <= 18000
     * 
     * TODO: this method doesn't work well with multiple fingers
     * 		 desired function would be to base tone on closest point on the entire hand
     * TODO: implement tuning functionality, tuning should alter SCALE variable 
     */
    private double getTone(Hand hand/*FingerList fingers*/) {
    	if (hand == null)
    		return 0;
    	
    	float max = hand.fingers().get(0).tipPosition().getX();
       	for (Finger finger : hand.fingers()){
       		float v = finger.tipPosition().getX();
       		if (v > max){
       			max = v;
       		}
       	}
       	
       	// Finds the distance of the finger from the "antennae"
       	double position = Math.abs(ANTENNAE - (double) max);
    	 
       	// Pitch formula = MAX * (1/2)^(pos / SCALE)
       	// This formula pins the maximum frequency to be at the location of ANTENNAE 
       	// SCALE is used to tune the pitch scale
       	double tone = MAXFREQ * Math.pow(.5,  position / SCALE);

       	// 18000 Hz is limit on pitch
    		if (tone > 18000.0) {
    			tone = 18000.0;
    		}
       	
       	return tone;
    }
    
    
    /*
     * Evaluate a hand into an double value representing volume level
     * The first finger on the hand is used
     * 
     * The level returned will be <= 100
     * 
     * TODO filter out crackling when changing level
     * TODO fine tune volume scaling, this will be hardcoded
     */
    private double getLevel(Hand hand) {
    		if (hand == null)
    			return 0;
    		
       	Vector v = hand.fingers().get(0).tipPosition();
       	float Yval = v.getY();
       	
       	printDebug("Y VAL: " + Yval);
       	
       	// Volume formula  = C * log(height - offset)
       	// The formula should pin the height of OFFSET to 0 db
       	// Levels beyond 0 are computed logarithmically and scaled with a constant
       	double level = 17 * Math.log(Yval - OFFSET);
       	
       	// 1000 db will be the limit on volume
    		if (level > 100.0){
    			level = 100.0;
    		}
       	
       	return level;
    }
    
    /*
     * This method is intended to transform a finger point position into an
     * audio frequency that can be used as input to an oscillator.
     * 
     * TODO when removing hand from frame, controller can get confused
     * 		also if you put left hand it first, it will not map it to volume when you put in right hand
     */
    public void onFrame(Controller controller) {
    	// TODO: check for state change gesture
    	// TODO: update state variable
    	
    	Frame frame = controller.frame();
    	double level = 0;
    	double tone = 0;
    	
    	// Evaluate Hands, decide which hands are left and right,
    	// if there is only 1 hand, left will be null
    	// if there are no hands left and right will be null
    	// getTone and getLevel should handle null objects accordingly
    	
    	Hand right = null;
    	Hand left = null;
    	int count = frame.hands().count();
    	System.out.println("#Hands: " + count);

    	if (count > 0){ 
    		// there is a pitch hand
    		right = frame.hands().rightmost();
    	} 
    	if (count > 1){
    		// there is also a level hand, need to find the left one
    		left = frame.hands().leftmost();
    	}
    	
    	// Process hands, we know which is left and right now so lets call
    	// getTone and getLevel. This happens regardless of weather they are
    	// null or not.
    	
    	tone = getTone(right);
    	level = getLevel(left);
    	
    	printDebug(Double.toString(tone));
    	
    	// TODO: update the graphics model
    	// TODO: move these into getLevel and getVolume, they should pass position data now
    	graphicsModel.setPitch(tone);
    	graphicsModel.setVolume(level);
    	
    	
    	// Audio data is sent to PureData here, data is 
    	boolean pitchSent = pitchConnection.sendPitch(tone, level);
    	
    	if(!pitchSent){
    		printDebug("ERROR: message did not send");
    	}  
    } 
}


