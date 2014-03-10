package graphics;

import java.awt.Font;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import leapControl.ThereminListener;

import org.newdawn.slick.TrueTypeFont;

public class GraphicsUtils {
	private static final int DEBUG = 0;

	private static final String SETTINGS_FILE = "res/settings.thm";
	private static final String DEFAULT_SETTINGS_FILE = "res/settings.default";
	private static Properties appProps;
	
	private static final double C0;
	
	public static final int DEFAULT_WIDTH;
	public static final int DEFAULT_HEIGHT;
	public static final String TITLE;
	public static final double DEFAULT_VAL;
	public static final boolean QUANTIZED;
	// the ratio to find antennae locations relative to image width
	public static final double PITCH_ANT_RATIO;
	public static final double LEVEL_ANT_RATIO;
	
	public static final int FONT_SIZE;
	private static final String FONT;

	public static final List<Double> notes;
	public static final String[] noteNames;

	/*
	 * Initializes the fields according to the settings file.
	 */
	static {
		loadProps();
		
		C0 = Double.valueOf(appProps.getProperty("C0"));
		DEFAULT_WIDTH = Integer.valueOf(appProps.getProperty("Width"));
		DEFAULT_HEIGHT = Integer.valueOf(appProps.getProperty("Height"));
		TITLE = appProps.getProperty("Title");
		DEFAULT_VAL = Double.valueOf(appProps.getProperty("DefaultVal"));
		QUANTIZED = Boolean.valueOf(appProps.getProperty("Quantized"));
		PITCH_ANT_RATIO = Double.valueOf(appProps.getProperty("PitchRatio"));
		LEVEL_ANT_RATIO = Double.valueOf(appProps.getProperty("LevelRatio"));
		FONT_SIZE = Integer.valueOf(appProps.getProperty("FontSize"));
		FONT = appProps.getProperty("Font");
		/*System.out.println("Properties loaded and constants updated");
		System.out.println("C0: " + C0);
		System.out.println("WIDTH: " + DEFAULT_WIDTH);
		System.out.println("HEIGHT: " + DEFAULT_HEIGHT);
		System.out.println("TITLE: " + TITLE);
		System.out.println("DEFAULT_VAL: " + DEFAULT_VAL);
		System.out.println("QUANTIZED: " + QUANTIZED);
		System.out.println("PITCH_ANT_RATIO: " + PITCH_ANT_RATIO);
		System.out.println("LEVEL_ANT_RATIO: " + LEVEL_ANT_RATIO);
		System.out.println("FONT_SIZE: " + FONT_SIZE);
		System.out.println("FONT: " + FONT);*/
		
		//
		notes = setupNotes();
		noteNames =	setupNoteNames();
	}

	private static void loadProps() {
		try {
			Properties defaultProps = new Properties();
			InputStream in = new FileInputStream(DEFAULT_SETTINGS_FILE);
			defaultProps.load(in);
			in.close();
			
			// Load the default properties into the appProps
			appProps = new Properties(defaultProps);
			
			// look for the apps properties file to load updated props
			try {
				in = new FileInputStream(SETTINGS_FILE);
				appProps.load(in);
				in.close();
			} catch(FileNotFoundException fne) {
				printDebug("No application properties file found.");
			}
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	private static String[] setupNoteNames() {
		String[] n2 = new String[] {
				"C", "C#/Db", "D", "D#/Eb", "E", "F", "F#/Gb", "G", "G#/Ab", "A", "A#/Bb", "B"
		};
		return n2;
	}
	
	private static List<Double> setupNotes() {
		List<Double> notes2 = new ArrayList<Double>();
		double val = C0;
		while(val < ThereminListener.MAXFREQ) {
			notes2.add(val);
			val *= Math.pow(2, 1.0/12.0);
		}
		notes2.add(val);
		return Collections.unmodifiableList(notes2);
	}
	
	/**************************************************************
	 * Font setup
	 **************************************************************/
	

	private static TrueTypeFont font = null;

	public static TrueTypeFont getDefaultFont() {
		if(font == null) {
			Font awtFont = new Font(FONT, Font.BOLD, FONT_SIZE);
			font = new TrueTypeFont(awtFont, false);
		}
		return font;
	}
	
	public static void printDebug(String message){
        printDebug(message, 5);
    }
	
	public static void printDebug(String message, int dbgLevel) {
        if (DEBUG >= dbgLevel) {
            System.out.println(message);
        }
    }
}
