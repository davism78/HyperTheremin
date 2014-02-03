package graphics;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GraphicsModel {
	private static final String TITLE = "Theremin";
	private static final boolean FULL_SCREEN = false;
	private static final int WIN_HEIGHT = 600;
	private static final int WIN_WIDTH = 800;

	private boolean isCreated;

	public GraphicsModel() {
		setupDisplay();

	}

	/**
	 * Sets up the display to the default dimensions
	 */
	private void setupDisplay() {
		try {
			Display.setTitle(TITLE);
			Display.setDisplayMode(new DisplayMode(WIN_WIDTH, WIN_HEIGHT));
			Display.setResizable(true);
			Display.setFullscreen(FULL_SCREEN);
			Display.create();
			isCreated = true;
			glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
			resize();

			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
			glMatrixMode(GL_MODELVIEW);
			
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
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

			render();

			Display.update();
			Display.sync(60);
		}
	}

	private void render() {
		// Clears the screen
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glLoadIdentity();
		glColor3f(0, 0, 1); // set color to blue
		
		glBegin(GL_TRIANGLES); // draw triangle
		glVertex2f(100, 100);
		glVertex2f(150, 100);
		glVertex2f(100, 150);
		
		glEnd();




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
