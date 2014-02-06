package graphics;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Font;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;

public class GraphicsModel {
	private static final String TITLE = "Theremin";
	private static final boolean FULL_SCREEN = false;
	private static final int WIN_HEIGHT = 600;
	private static final int WIN_WIDTH = 800;

	private boolean isCreated;
	
	private TrueTypeFont font;
	private double val;

	public GraphicsModel() {
		setupDisplay();
	}

	/**
	 * Sets up the display to the default dimensions
	 */
	private void setupDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WIN_WIDTH,WIN_HEIGHT));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle(TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);       
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                   
		 
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               
		GL11.glClearDepth(1);                                      
		 
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		 
		GL11.glViewport(0,0,WIN_WIDTH,WIN_HEIGHT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		 
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, WIN_WIDTH, WIN_HEIGHT, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	

		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, false);
		val = 3.14159;
	}

	/**
	 * resizes the viewport on the Display to match the dimensions of the Display.
	 */
	private void resize() {
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	/**
	 * Starts up the display in a loop until the user wishes to terminate the application.
	 * When the method returns, the user will have requested to close.
	 */
	public void runDisplay() {
		while(!Display.isCloseRequested()) {

			if(Display.wasResized()) {
				resize();
			}
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();

			Display.update();
			Display.sync(60);
		}
	}

	private void render() {
		Color.white.bind();
		font.drawString(50, 50, Double.toString(val), Color.yellow);
	}

	/**
	 * Cleans up the Display used in the Application.
	 */
	public void cleanup() {
		if(isCreated) {
			Display.destroy();
			isCreated = false;
		}
	}
	
	public void setVal(double v){
		val = v;
	}
}
