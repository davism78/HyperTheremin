package graphics;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.ImageIOImageData;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class GraphicsView {
	
	private GraphicsModel model;
	private Texture background;
	
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
		
		try {
			background = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/theremin.jpg"));
			Display.setIcon(new ByteBuffer[] {
                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon_16.png")), false, false, null),
                    new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File("res/icon_32.png")), false, false, null)
                    });

		} catch (IOException e) {
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
		background.bind();
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glTexCoord2f(0,0);
			GL11.glVertex2f(0, 0);
			GL11.glTexCoord2f(1,0);
			GL11.glVertex2f(background.getTextureWidth(), 0);
			GL11.glTexCoord2f(1,1);
			GL11.glVertex2f(0 + background.getTextureWidth(), 0 + background.getTextureHeight());
			GL11.glTexCoord2f(0,1);
			GL11.glVertex2f(0, 0 + background.getTextureHeight());
		GL11.glEnd();	
		
		GL11.glColor3f(1, 1, 1);
		
		// renderString(tuning stuff model.getTune())
		
		// case playing
		// render hands
		renderString(50, 50, "Pitch: " + model.getPitch());
		renderString(50, 75, "Volume: " + model.getVolume());
		// case tuning
		// render tuning hand fingers
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
