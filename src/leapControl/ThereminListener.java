package leapControl;

import static graphics.ThereminMode.MENU;
import static graphics.ThereminMode.PLAY;
import static graphics.ThereminMode.PLAYBACK;
import graphics.GraphicsModel;
import graphics.GraphicsUtils;
import graphics.ThereminMode;
import main.Theremin;

import java.util.Collections;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Finger;
import com.leapmotion.leap.FingerList;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Hand;
import com.leapmotion.leap.HandList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.Vector;

import java.io.File;

public class ThereminListener extends Listener {
    private static final double DEFAULT_SCALE = 100.0;

    public static final double OFFSET = 25.0; // Leap motion min sensitivity
    public static final double MAXFREQ = 2000.0; // freq when touching
                                                   // antennae

    public static final double ANTENNAE = 350.0; // the distance of the virtual
                                                  // antennae from the origin
    
    // the number of dead frames to ignore before defaulting to 0
    private static final int MAXDEADFRAMES = 4;
    
    // Files to write recroding to
	private static final String FILERECORD = "recording.wav";
	private static final String FILEPLAY   = "playback.wav";
    
    private int deadPitchFrames = 0;
    private int deadLevelFrames = 0;


    private OSCConnection pitchConnection;
    private GraphicsModel graphicsModel;

    public ThereminListener(GraphicsModel model) {
        this.graphicsModel = model;
        this.graphicsModel.setScale(DEFAULT_SCALE); // default initial value
    }

    public void onInit(Controller controller) {
        pitchConnection = new OSCConnection(8000);
    }

    public void onConnect(Controller controller) {
        printDebug("Connected");
    }

    public void onDisconnect(Controller controller) {
        // Note: not dispatched when running in a debugger.
        printDebug("Disconnected");
    }

    public void onExit(Controller controller) {
        printDebug("Exited");
    }

    public static void printDebug(String message){
        printDebug(message, 5);
    }
    
    public static void printDebug(String message, int dbgLevel) {
        GraphicsUtils.printDebug(message, dbgLevel);
    }

    public void setPitchScale(double scale) {
        graphicsModel.setScale(scale);
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
    	FingerList figs = null;
        if (hand == null || (figs = hand.fingers()) == null || figs.isEmpty()){
        	deadPitchFrames++;
            // if we get here, there is no pitch hand in the frame
        	if (deadPitchFrames <= MAXDEADFRAMES){
        		// play last known pitch
        		
        		return graphicsModel.getPitch();
        		
        	} else {
        		graphicsModel.setPitch(0.0);
            	return 0;
        	}
        }

        deadPitchFrames = 0;
        assert(figs != null);
        
        // take the right finger (closest to antennae)
        float max = figs.rightmost().tipPosition().getX();

        // Finds the distance of the finger from the "antennae"
        double position = Math.abs(ANTENNAE - (double) max);

        // Pitch formula = MAX * (1/2)^(pos / SCALE)
        // This formula pins the maximum frequency to be at the location of
        // ANTENNAE
        // SCALE is used to tune the pitch scale
        double tone = MAXFREQ * Math.pow(.5, position / graphicsModel.getScale());
        assert(position > 0 && tone <= MAXFREQ);

        if(GraphicsUtils.QUANTIZED) {
        	tone = quantizeTone(tone);
        }
        
        // communicate with graphics
        HandData data = new HandData(tone, max);
        graphicsModel.setRightHand(data);
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
    	FingerList figs = null;
    	
        // if we get here, there is no level hand in the frame
        if (hand == null || (figs = hand.fingers()) == null || figs.isEmpty()){
        	deadLevelFrames++;
        	// if we get here, there is no pitch hand in the frame
        	if (deadLevelFrames <= MAXDEADFRAMES){
        		// play last known pitch

        		return graphicsModel.getVolume();
        	} else {
        		graphicsModel.setVolume(0.0);
            	return 0;
        	}
        }

        deadLevelFrames = 0;
        assert(figs != null);
        
        float Yval = figs.get(0).tipPosition().getY();
        if(Yval > 660) { // this is max leap motion Y sensitivity
        	Yval = 660;
        	//FIXME: Hardcoded image dimension!!!
        } else if(Yval < OFFSET) {
        	Yval = (float) (OFFSET + 1);
        }
        printDebug("Y VAL: " + Yval);

        // Volume formula = C * log(height - offset)
        // The formula should pin the height of OFFSET to 0 db
        // Levels beyond 0 are computed logarithmically and scaled with a
        // constant
        double level = 14 * Math.log(Yval - OFFSET);

        // 100 db will be the limit on volume
        if (level > 100.0) {
            level = 100.0;
        }

        // communicate with graphics
        HandData data = new HandData(level, Yval);
        graphicsModel.setLeftHand(data);
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

        // FIXME: We dont know if there will only be one gesture!!!
        GestureList gests = frame.gestures();
        assert(gests.count() <= 1);
        
        if (gests.count() > 0){
            
            Gesture gest = gests.get(0);
            switch(gest.type()) {
            case TYPE_CIRCLE:
            	// state change triggered
            	printDebug("STATE TRANSFER: GESTURE SEEN", 1);

            	// Get the CircleGesture and check if a circle has been made
            	CircleGesture circleGest = new CircleGesture(gest);
            	if (circleGest.progress() > 2.0){
            		// Switch to menu.
            		graphicsModel.setMode(MENU);
                	
                    // tune/play mode transition
                    if (oldmode == PLAY || oldmode == PLAYBACK){
                        // mute sound
                        pitchConnection.sendPitch(0, 0);
                        // stop recording
                        graphicsModel.setRecording(false);
                        pitchConnection.sendRecordOff(FILEPLAY);
                        // stop playback
                        pitchConnection.sendPlayBackOff(FILEPLAY);
                        graphicsModel.setPlayback(false);

                    } 
            	}
            	break;
            case TYPE_KEY_TAP:
                // turn on/off recording
            	if(oldmode == PLAY) {
            		graphicsModel.flipRecording();
            		
            		// check status and notify PD
            		if (graphicsModel.isRecording()){
            			// We are now recording, turn on recording
            			pitchConnection.sendRecordOn(FILERECORD);
            		} else {
            			// We turned off recording
            			pitchConnection.sendRecordOff(FILEPLAY);
            			// turn off playback if on
            			pitchConnection.sendPlayBackOff(FILEPLAY);
                        graphicsModel.setPlayback(false);

            			try {
            			    /* This sleep is here to ensure that the two files are closed by
            			     * Puredata before we try to delete/ copy over them.
            			     */
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            /* ignore */
                        }
            			// copy recording to secondary file
            			if (!moveRecording()){
            				printDebug("MOVE NOT WORKY");
            			}
            		}
            	}
            	break;
            case TYPE_SCREEN_TAP:
            	// if we are in the menu, transfer to selected menu state
            	if(oldmode == MENU) {
            		graphicsModel.setMode(graphicsModel.getMenuData().getSelectedState());
            	} else if (oldmode == PLAY){ // if we are in Play mode, flip playback
            		graphicsModel.flipPlayback();
            		if (graphicsModel.isPlayback()){
                        pitchConnection.sendPlayBackOn(FILEPLAY);
                        printDebug("PLAYBACK ON");
            		} else {
                        pitchConnection.sendPlayBackOff(FILEPLAY);
            		}
            	}
            	break;
            default:
            	printDebug("DEFAULT GESTURE", 1);
            }
            
        }

        /*
         * We have determined the state we are in so now we need to execute the 
         * corresponding behavior.
         */
        
        ThereminMode newmode = graphicsModel.getMode();
        
        switch(newmode) {
        case PLAY:
            // doPlayMode should handle all actions in playmodel
            doPlayMode(frame);
        	break;
        case TUNE:
            // doTuneMode should handle tuning
            doTuneMode(frame);
        	break;
        case MENU:
        	doMenuMode(frame);
        	break;
        case PLAYBACK:
        	break;
        case EXIT:
        	Theremin.exit();
        default:
        	break;
        }
    }
    
    /*
     * move recording.wav to playback.wav.  Call this when recording is disabled
     */
    private boolean moveRecording(){
    	String url = this.getClass().getResource("").getPath();

		printDebug(url);
		
		String newurl = url + "../../pd_src/";
		
		File file = new File(newurl + "recording.wav");
		
		try{
			File newfile = new File(newurl + "playback.wav");
			if(newfile.exists() && newfile.delete()){
				printDebug("File was deleted", 0);
			} else {
				printDebug("File is failed to delete!", 0);
			}
			
			
			if(file.renameTo(newfile)){
	    		printDebug("File is moved successful!", 0);
			}else{
	    		printDebug("File is failed to move!", 0);
	    	}
	 
	    }catch(Exception e){
	    	return false;
	   	}
		return true;
    }

	/*
     * This method handles all behavior for the main play state of the theremin.
     * 1. evaluate number of hands and their functions
     * 2. Process hand data into usable data
     * 3. Send this data to audio synthesis
     * 4. Record (if enabled)
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
    
    private double quantizeTone(double tone) {
    	int index = Collections.binarySearch(GraphicsUtils.notes, tone);
    	if(index < 0) {
    		index = Math.abs(index) - 1;
    	}
    	return GraphicsUtils.notes.get(index);
    	
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
                graphicsModel.setLeftAndRightTuningFingers(leftpos, rightpos);
                
                // now calculate the difference between tips
                // this is multiplied by 1.5 to make the scale range 
                // from 30 to 225
                // TODO: should this look at more than one axis?
                double octave = 1.5 * Math.abs(leftpos - rightpos);
                
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
    
    
    /*
     * TODO: we are moving to puredata based recording, this method and the PLAYBACK mode
     * enum will not be used
     */
    /*
     * Defines the behavior of the MainMenu
     * 1. get x,y position of hand
     * 2. send to model for updating
     */
    private void doMenuMode(Frame frame) {
    	Vector fingerPos = frame.hands().rightmost().fingers().rightmost().tipPosition();
    	graphicsModel.getMenuData().updateMainMenu(fingerPos.getX(), fingerPos.getY());
    	
    }
	
	public void cleanup() {
		// stop recording and playback
		pitchConnection.sendPlayBackOff(FILEPLAY);
		pitchConnection.sendRecordOff(FILEPLAY);
	}

}
