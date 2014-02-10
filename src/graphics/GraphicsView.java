package graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class GraphicsView {
	
	private GraphicsModel model;
	
	private boolean isCreated;
	
	public GraphicsView(GraphicsModel model) {
		this.model = model;
		setupDisplay();
		prepareGraphics();
		model.setupFont();
		isCreated = true;
	}

	private void setupDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(GraphicsUtils.DEFAULT_WIDTH, GraphicsUtils.DEFAULT_HEIGHT));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle(GraphicsUtils.TITLE);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private void prepareGraphics() {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);       
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);                   
		 
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);               
		GL11.glClearDepth(1);                                      
		 
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		 
		GL11.glViewport(0,0,Display.getWidth(),Display.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		 
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);		
	}
	
	/**
	 * resizes the viewport on the Display to match the dimensions of the Display.
	 */
	private void resize() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
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
		
		GL11.glColor3f(1, 1, 0);
		
		renderString(50, 50, "Pitch: " + model.getPitch());
		renderString(50, 75, "Volume: " + model.getVolume());
		
	}
	
	private void renderString(float x, float y, String message) {
		
		model.getFont().drawString(x, y, message);
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
}
