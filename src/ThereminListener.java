
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPortOut;
import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.illposed.osc.*;

import java.net.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;

public class ThereminListener extends Listener {
	private static final double SCALE = 30.0;
	private static final double OFFSET = 25.0;
	private static final double CZERO = 16.25;
    
	//net stuff
	private InetAddress remoteIP;
    private int remotePort = 8000;
    String addressPitch = "/note";
    OSCPortOut sender;

	
    public void onInit(Controller controller) {
    	 // The IP Address of the server (or listener) we would like to send to
	    // For testing we will use the `getLocalHost` function to send to our
	    // machine, but for using with different physical devices, use something
	    // like the following commented out line
	    //InetAddress remoteIP  = InetAddress.getByName("192.168.1.1");
    	try {
			remoteIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    // The port that the server we are sending to is listening on
	    
		try {
			sender = new OSCPortOut(remoteIP, remotePort);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
            	
            	if(!sendPitch(tone, 75)){
            		System.out.println("ERROR: message did not send");
            	}
            }
    	}
    }
    
    private boolean sendPitch(double tone, int level){
    	return sendOSCMessage(addressPitch, new Float(tone), new Integer(level));
    }
    
    private boolean sendOSCMessage(String address, Object value1, Object value2){
    	if(value1 == null || value2 == null)
    		return false;
    	Collection<Object> values = new ArrayList<Object>();
    	values.add(value1);
	    values.add(value2);
	    OSCMessage message = new OSCMessage(address, values);
	    try {
			sender.send(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
    	return true;
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
