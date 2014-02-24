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
	private Texture leftHand;
	private Texture rightHand;
	private Texture finger;
	
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
			leftHand = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/hand_64.png"), false);
			rightHand = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/hand_64.png"), true);
			finger = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/finger_64.png"));
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
		// draw background
		renderTexture(background, 0, 0);
		
		switch(model.getMode()) {
		case PLAY:
			renderPlay();
			break;
		case TUNE:
			renderTune();
			break;
		case MENU:
			renderMainMenu();
		default:
			break;
		}
		
	}

	private void renderMainMenu() {
		// 1. Build background
		// 2. Draw boundary lines
		// 3. Draw names
		// 4. Draw handPosition
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
	}

	private void renderTune() {
		double scale = model.getScale();
		float leftFinger = (float) (Display.getWidth() / 2 - (scale / 2));
		float rightFinger = (float) (Display.getWidth() / 2 + (scale / 2));
		renderTexture(finger, leftFinger, 250);
		renderTexture(finger, rightFinger, 250);
		
		
		renderString(50, 50, "Tuning Scale: " + scale);
	}

	private void renderPlay() {
		// TODO: hand positions are based on old model of pitch and volume, change to HandData based
		if(model.getPitch() > 0.0){
			int pitchCoord = (int)Math.floor(model.getPitch());
			renderTexture(rightHand, pitchCoord, 250);
		} else
			System.out.println("NO PITCH");
		
		if(model.getVolume() > 0.0){
			int volumeCoord = (int) Math.floor(model.getVolume());
			renderTexture(leftHand, 50, 450 - volumeCoord);
		} else
			System.out.println("NO VOL");
		
		GL11.glColor3f(1, 1, 1);

		renderString(50, 50, "Pitch: " + model.getPitch());
		renderString(50, 75, "Volume: " + model.getVolume());
	}
	
	private void renderTexture(Texture tex, float topLeftX, float topLeftY) {
		tex.bind();
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0,0);
		GL11.glVertex2f(topLeftX, topLeftY); // topLeft
		GL11.glTexCoord2f(1,0);
		GL11.glVertex2f(topLeftX + tex.getTextureWidth(), topLeftY); // topRight
		GL11.glTexCoord2f(1,1);
		GL11.glVertex2f(topLeftX + tex.getTextureWidth(), topLeftY + tex.getTextureHeight()); // bottomRight
		GL11.glTexCoord2f(0,1);
		GL11.glVertex2f(topLeftX, topLeftY + tex.getTextureHeight()); // bottom left
		GL11.glEnd();
		
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
