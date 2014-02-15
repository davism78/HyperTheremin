package leapControl;

import graphics.GraphicsModel;
import graphics.ThereminMode;

import com.leapmotion.leap.*;

import static graphics.ThereminMode.*;

public class ThereminListener extends Listener {
    // make this higher for more info
    public static final int DEBUG = 1;

    // TODO: this should move to a reference value in graphicsModel
    private double SCALE; // will be determined by tuning

    private static final double OFFSET = 25.0; // Leap motion min sensitivity
    private static final double MAXFREQ = 20000.0; // freq when touching
                                                   // antennae

    private static final double ANTENNAE = 350.0; // the distance of the virtual
                                                  // antennae from the origin

    private OSCConnection pitchConnection;
    private GraphicsModel graphicsModel;

    public ThereminListener(GraphicsModel model) {
        this.graphicsModel = model;
        SCALE = 40.0; // default initial value
    }

    public void onInit(Controller controller) {
        pitchConnection = new OSCConnection(8000, "/note");
    }

    public void onConnect(Controller controller) {
        System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
        // Note: not dispatched when running in a debugger.
        System.out.println("Disconnected");
    }

    public void onExit(Controller controller) {
        System.out.println("Exited");
    }

    private static void printDebug(String message){
        printDebug(message, 5);
    }
    
    private static void printDebug(String message, int dbgLevel) {
        if (DEBUG >= dbgLevel) {
            System.out.println(message);
        }
    }

    public void setPitchScale(double scale) {
        SCALE = scale;
    }

    /*
     * Evaluate hands position into a double value representing a pitch Pitch is
     * determined by distance from virtual antennae which is calculated here The
     * virtual antennae location is specified by ANTENNAE
     * 
     * The closest finger to the antennae will be used
     * 
     * The pitch returned will always be <= 18000
     * 
     * TODO: this method doesn't work well with multiple fingers. desired
     * function would be to base tone on closest point on the entire hand
     */
    private double getTone(Hand hand/* FingerList fingers */) {
        if (hand == null){
            // if we get here, there is no pitch hand in the frame
            graphicsModel.setPitch(0);
            return 0;
        }

        float max = hand.fingers().get(0).tipPosition().getX();
        for (Finger finger : hand.fingers()) {
            float v = finger.tipPosition().getX();
            if (v > max) {
                max = v;
            }
        }

        // Finds the distance of the finger from the "antennae"
        double position = Math.abs(ANTENNAE - (double) max);

        // Pitch formula = MAX * (1/2)^(pos / SCALE)
        // This formula pins the maximum frequency to be at the location of
        // ANTENNAE
        // SCALE is used to tune the pitch scale
        double tone = MAXFREQ * Math.pow(.5, position / SCALE);

        // 18000 Hz is limit on pitch
        if (tone > 18000.0) {
            tone = 18000.0;
        }

        // communicate with graphics
        // TODO: needs to pass position
        graphicsModel.setPitch(tone);
        // return value
        return tone;
    }

    /*
     * Evaluate a hand into an double value representing volume level The first
     * finger on the hand is used
     * 
     * The level returned will be <= 100
     * 
     * TODO filter out crackling when changing level TODO fine tune volume
     * scaling, this will be hardcoded
     */
    private double getLevel(Hand hand) {
        if (hand == null){
            // if we get here, there is no level hand in the frame
            graphicsModel.setVolume(0);
            return 0;
        }

        Vector v = hand.fingers().get(0).tipPosition();
        float Yval = v.getY();

        printDebug("Y VAL: " + Yval);

        // Volume formula = C * log(height - offset)
        // The formula should pin the height of OFFSET to 0 db
        // Levels beyond 0 are computed logarithmically and scaled with a
        // constant
        double level = 17 * Math.log(Yval - OFFSET);

        // 100 db will be the limit on volume
        if (!(level <= 100.0)) {
            level = 100.0;
        }

        // communicate with graphics
        // TODO: needs to pass position
        graphicsModel.setVolume(level);
        // return value
        return level;
    }

    /*
     * This method is intended to transform a finger point position into an
     * audio frequency that can be used as input to an oscillator.  
     * 
     * This method deals with the internal state of the instrument.  It evaluates
     * if a state transition should be made and handles the transition.  After 
     * that it calls the correct method to handle the frame data for the current 
     * state.
     * 
     */
    public void onFrame(Controller controller) {
        // Get this frame
        Frame frame = controller.frame();

        /*
         * The first thing we must do is evaluate our state.  We can be in 
         * either PLAY, TUNE, or RECORD mode.  The current state is stored 
         * in the graphics model and state transitions are based on gestures
         */
        
        ThereminMode oldmode = graphicsModel.getMode();
        printDebug("STATE: " + oldmode, 5);

        GestureList gests = frame.gestures();
        assert(gests.count() <= 1);
        
        if (gests.count() > 0){
            // state change triggered
            printDebug("STATE TRANSFER: GESTURE SEEN", 1);
            
            Gesture gest = gests.get(0);
            
            if (gest.type() == Gesture.Type.TYPE_SCREEN_TAP){
                // tune/play mode transition
                if (oldmode == PLAYMODE)
                    graphicsModel.setMode(TUNEMODE);
                else if (oldmode == TUNEMODE)
                    graphicsModel.setMode(PLAYMODE);
                // noop in RECORDMODE
            } else if (gest.type() == Gesture.Type.TYPE_KEY_TAP){
                // record/play not implemented
            }
        }

        /*
         * We have determined the state we are in so now we need to execute the 
         * corresponding behavior.
         */
        
        ThereminMode newmode = graphicsModel.getMode();
        
        if (newmode == PLAYMODE) {
            // doPlayMode should handle all actions in playmode
            doPlayMode(frame);
        } else if (newmode == TUNEMODE){
            // doTuneMode should handle tuning
            doTuneMode(frame);
        }
    }
    
    /*
     * This method handles all behavior for the main play state of the theremin.
     * 1. evaluate number of hands and their functions
     * 2. Process hand data into usable data
     * 3. Send this data to audio synthesis
     */
    private void doPlayMode(Frame frame){
        /*
         * 1. Evaluate Hands, decide which hands are left and right,
         * if there is only 1 hand, left will be null
         * if there are no hands left and right will be null
         * getTone and getLevel should handle null objects accordingly
         */
        
        double level, tone = 0;
            
        Hand right = null;
        Hand left = null;
        int count = frame.hands().count();
        printDebug("#Hands: " + count);

        if (count > 0) {
            // there is a pitch hand
            right = frame.hands().rightmost();
        }
        if (count > 1) {
            // there is also a level hand, need to find the left one
            left = frame.hands().leftmost();
        }
        
        /*
         * 2. Process hands, we know which is left and right now so lets call
         * getTone and getLevel. This happens regardless of weather they are
         * null or not.
         */
        
        tone = getTone(right);
        level = getLevel(left);

        // TODO: update the graphics model
        /*
         * 3. Now lets send the audio data to PureData,
         * tone and level handled by sendPitch
         */

        printDebug(Double.toString(tone));

        boolean pitchSent = pitchConnection.sendPitch(tone, level);

        if (!pitchSent) {
            printDebug("ERROR: message did not send");
        }
    }
    
    /*
     * This method handles behevior of instrument in tuning state
     * 1. decide if there is a hand shown
     * 2. decide if it has multiple fingers
     * 3. if so, alter theremin tuning based on most extreme fingers
     */
    private void doTuneMode(Frame frame){
        HandList hands = frame.hands();
        if (hands.count() > 0){
            /*
             * There is at least one hand.  Lets use the right hand for tuning.  We need 
             * to get the distance between tips and calculate correct scale value to
             * assign. 
             */
            
            Hand hand = hands.rightmost();
            
            // check that there are multiple fingers
            if ( hand.fingers().count() > 1){
                Finger left = hand.fingers().leftmost();
                Finger right = hand.fingers().rightmost();
                
                // get finger tips
                double leftpos = left.tipPosition().getX();
                double rightpos = right.tipPosition().getX();
                
                // now calculate the difference between tips
                // TODO: should this look at more than one axis?
                double octave = Math.abs(leftpos - rightpos);
                
                /*
                 * If we assign SCALE to be the distance between fingers, our 
                 * pitch formula will half or double pitch when you move your finger 
                 * by that amount.
                 */
                setPitchScale(octave);
                printDebug("SCALE: " + octave, 1);
            }
        }
    }
}
