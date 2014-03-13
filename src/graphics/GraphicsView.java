package graphics;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import leapControl.ThereminListener;

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
	private Texture rightFinger;
	private Texture leftFinger;
	private Texture record;
	private Texture play;
	
	private boolean isCreated;
	private boolean stopRequested;
	
	public GraphicsView(GraphicsModel model) {
		this.model = model;
		setupDisplay();
		prepareGraphics();
		model.setupFont();
		isCreated = true;
		stopRequested = false;
	}

	private void setupDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(GraphicsUtils.DEFAULT_WIDTH, GraphicsUtils.DEFAULT_HEIGHT));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle(GraphicsUtils.TITLE);
			//Display.setResizable(true);

		} catch (LWJGLException e) {
			e.printStackTrace();

		}
		
		try {
			background = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("res/theremin.jpg"));
			leftHand = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/hand_64.png"));
			rightFinger = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/finger_64.png"));
			leftFinger = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/finger_left_64.png"));
			record = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/recording.png"));
			play = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("res/playing.png"));
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
		 
		GL11.glClearColor(0.75f, 0.75f, 0.75f, 0.75f);
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
		
		int width = Display.getWidth();
		int height = Display.getHeight();
		ThereminListener.printDebug("Width: " + width);
		ThereminListener.printDebug("Height: "+ height);
		GL11.glViewport(0, 0, width, height);
	}

	/**
	 * Starts up the display in a loop until the user wishes to terminate the application.
	 * When the method returns, the user will have requested to close.
	 */
	public void runDisplay() {
		while(!Display.isCloseRequested() && !stopRequested) {

			if(Display.wasResized()) {
				resize();
			}
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();

			Display.update();
			Display.sync(60);
		}
		Display.destroy();
	}

	private void render() {
		// draw background
		renderTexture(background, 0, 0);
		
		switch(model.getMode()) {
		case PLAY:
		case PLAYBACK:
			renderPlay();
			break;
		case TUNE:
			renderTune();
			break;
		case MENU:
			renderMainMenu();
			break;
		default:
			break;
		}
		
	}

	private void renderMainMenu() {
		/* 1. Build background
		 * 2. Draw boundary lines
		 * 3. Draw names
		 * 4. Draw handPosition
		 */
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glColor3f(0, 0, 0);
		// Draw horizontal line
		renderLine(0, Display.getHeight() / 2, Display.getWidth(), Display.getHeight() / 2);
		renderLine(Display.getWidth() / 2, Display.getHeight() / 2, Display.getWidth() / 2, Display.getHeight());
		/*renderLine(0, model.getMenuData().windowLineY, Display.getWidth(), model.getMenuData().windowLineY);
		renderLine(model.getMenuData().windowLineX, 0, model.getMenuData().windowLineY, Display.getHeight());*/
		
		/*
		 * Here we display the current Menu selections. This involves determining which state we
		 * are currently connected to and adding a "selected" addition to the menu button name.
		 */
		int h = Display.getHeight();
		int w = Display.getWidth();
		int offset = 50;
		
		GL11.glColor3f(0, 0, 0);
		if(model.getMenuData().getSelectedState() == ThereminMode.PLAY) {
			GL11.glColor3f(1, 1, 1);
			renderString(w / 2 - offset, h / 4 - GraphicsUtils.FONT_SIZE, "Play Selected");
			GL11.glColor3f(0, 0, 0);
		} else {
			renderString(w / 2 - offset, h / 4 - GraphicsUtils.FONT_SIZE, "Play");
		}
		if(model.getMenuData().getSelectedState() == ThereminMode.TUNE) {
			GL11.glColor3f(1, 1, 1);
			renderString(offset, 3 * h / 4 - GraphicsUtils.FONT_SIZE, "Tune Selected");
			GL11.glColor3f(0, 0, 0);
		} else {
			renderString(offset, 3 * h / 4 - GraphicsUtils.FONT_SIZE, "Tune");
		}
		if(model.getMenuData().getSelectedState() == ThereminMode.EXIT) {
			GL11.glColor3f(1, 1, 1);
			renderString(offset + w / 2, 3 * h / 4 - GraphicsUtils.FONT_SIZE, "Exit Selected");
			GL11.glColor3f(0, 0, 0);
		} else {
			renderString(offset + w / 2, 3 * h / 4 - GraphicsUtils.FONT_SIZE, "Exit");
		}
		
		// Render the right finger
		renderTexture(rightFinger, (float) model.getMenuData().getHandXPos(), (float) model.getMenuData().getHandYPos());
		
		
		
	}

	private void renderTune() {
		/*
		 * Draw the fingers relative to the middle of the screen.
		 */
		double scale = model.getScale();
		float leftFinger = (float) (Display.getWidth() / 2 - (scale / 2));
		float rightFinger = (float) (Display.getWidth() / 2 + (scale / 2));
		renderTexture(this.leftFinger, leftFinger, 250);
		renderTexture(this.rightFinger, rightFinger, 250);
		
		
		renderString(50, 50, "Tuning Scale: " + scale);
	}

	private void renderPlay() {
		// render pitch hand
		if(model.getPitch() > 0.0) {
			int pitchCoord = (int)Math.floor(model.getPitchPosition());
			renderTexture(rightFinger, pitchCoord, 250);
		} else {
			//System.out.println("NO PITCH");
		}
		// render volume hand
		if(model.getVolume() > 0.0){
			int volumeCoord = (int) Math.floor(model.getVolumePosition());
			renderTexture(leftHand, 50, volumeCoord);
		} else {
			//System.out.println("NO VOL");
		}
		GL11.glColor3f(1, 1, 1);

		renderString(50, 50, "Pitch: " + model.getPitchString());
		renderString(50, 75, "Volume: " + roundedFloat(model.getVolume()));
		
		// render recording / playing icons (if applicable)
		if(model.isRecording()) {
			renderTexture(record, 50, 25);
		}
		if(model.isPlayback()) {
			renderTexture(play, 75, 25);
		}
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
	
	private void renderLine(float x, float y, float x2, float y2) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex2f(x, y);
		GL11.glVertex2f(x2, y2);
		GL11.glEnd();
	}
	
	private void renderString(float x, float y, String message) {
		
		model.getFont().drawString(x, y, message);
	}
	
	private String roundedFloat(double x) {
		return ((int) (x * 100)) / 100.0 + ""; 
	}
	
	/**
	 * Cleans up the Display used in the Application.
	 */
	public void cleanup() {
		if(isCreated) {
			stopRequested = true;
			isCreated = false;
		}
	}
}
