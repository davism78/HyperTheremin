package graphics;

import java.awt.Font;

import org.newdawn.slick.TrueTypeFont;

public class GraphicsUtils {
	public static final int DEFAULT_WIDTH = 740;
	public static final int DEFAULT_HEIGHT = 600;
	public static final String TITLE = "Theremin";
	public static final boolean FULL_SCREEN = false;
	public static final double DEFAULT_VAL = 0.0;

	private static final String DEFAULT_FONT = "Times New Roman";

	private static TrueTypeFont font = null;

	public static TrueTypeFont getDefaultFont() {
		if(font == null) {
			Font awtFont = new Font(DEFAULT_FONT, Font.BOLD, 24);
			font = new TrueTypeFont(awtFont, false);
		}
		return font;
	}
}
