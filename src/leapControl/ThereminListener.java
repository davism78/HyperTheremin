package leapControl;

import graphics.GraphicsModel;

import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

public class ThereminListener extends Listener {
	private static final double SCALE = 30.0;
	private static final double OFFSET = 25.0;
	private static final double CZERO = 16.25;
	
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
    
    /*
     * Evaluate fingers position into a double value representing a pitch
     */
    private double getTone(FingerList fingers) {
    	float max = fingers.get(0).tipPosition().getX();
       	for (Finger finger : fingers){
       		float v = finger.tipPosition().getX();
       		if (v > max){
       			max = v;
       		}
       	}
    	
       	double tone = CZERO * Math.pow(2, ((max - OFFSET) / SCALE));
       	return tone;
    }
    
    /*
     * Evaluate a fingers position into an int value representing volume level
     * 
     * TODO this method need much more thought out implementation
     * TODO filter out crackling when changing level
     */
    private double getLevel(Finger finger) {
       	Vector v = finger.tipPosition();
       	float Yval = v.getY();
       	System.out.println("Y VAL: " + Yval);
       	double level = 17 * Math.log(Yval);
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
    	double level = 0;
    	double tone = 0;
    	
    	System.out.println("#Hands: " + frame.hands().count());
    	// Process hands

    	if (frame.hands().count() >= 1){
    		// get pitch data
    		FingerList tones = frame.hands().get(0).fingers();
    		if (!tones.isEmpty()){
    			tone = getTone(tones);
    		}
    	}
    	
    	if (frame.hands().count() >= 2){
    		// get volume data
    		FingerList levels = frame.hands().get(frame.hands().count() - 1).fingers();
    		if (!levels.isEmpty()){
    			level = getLevel(levels.get(0));
    		}
    	}

    	System.out.println(tone);
    	
    	// update the graphics model
    	graphicsModel.setPitch(tone);
    	graphicsModel.setVolume(level);
    	
    	if(!pitchConnection.sendPitch(tone, level)){
    		System.out.println("ERROR: message did not send");
    	}  
    } 
}


