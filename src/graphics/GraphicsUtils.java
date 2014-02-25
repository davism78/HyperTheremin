package graphics;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import leapControl.ThereminListener;

import org.newdawn.slick.TrueTypeFont;

public class GraphicsUtils {
	private static final double C0 = 16.352;
	
	public static final int DEFAULT_WIDTH = 740;
	public static final int DEFAULT_HEIGHT = 600;
	public static final String TITLE = "Theremin";
	public static final boolean FULL_SCREEN = false;
	public static final double DEFAULT_VAL = 0.0;
	public static final boolean QUANTIZED = true;

	public static final List<Double> notes;
	
	static {
		List<Double> notes2 = new ArrayList<Double>();
		double val = C0;
		while(val < ThereminListener.MAXFREQ) {
			notes2.add(val);
			val *= Math.pow(2, 1.0/12.0);
		}
		notes2.add(val);
		notes = Collections.unmodifiableList(notes2);
	}
	
	
	public static final int FONT_SIZE = 24;
	private static final String DEFAULT_FONT = "Monaco";
	

	private static TrueTypeFont font = null;

	public static TrueTypeFont getDefaultFont() {
		if(font == null) {
			Font awtFont = new Font(DEFAULT_FONT, Font.BOLD, FONT_SIZE);
			font = new TrueTypeFont(awtFont, false);
		}
		return font;
	}
}
