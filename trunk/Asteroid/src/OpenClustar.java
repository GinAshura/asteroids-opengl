

import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.*;
import org.lwjgl.util.vector.Vector3f;

/**
 * Asteroid game
 *
 * @author Akuryo <cogney.maxime@gmail.com>
 * @author Sporbie <sporbie@gmail.com>
 * @version 0.0
 */

public class OpenClustar {

	public static final String GAME_TITLE = "Asteroid";
	private static final int FRAMERATE = 60;
	private static boolean finished;
	private static Vector3f pos = new Vector3f(0f,0f,0.0f);
	private static Vector3f rot = new Vector3f(-1f,0f,0f);

	public static void main(String[] args) {
		
		try {
			init(false);
			run();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			Sys.alert(GAME_TITLE, "An error occured and the game will exit.");
		} finally {
			cleanup();
		}
		System.exit(0);
	}

	private static void render() {
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clearing up the screen and depth buffer
		GL11.glLoadIdentity(); // Resetting the matrix (so X is left-right, Y up-down and Z front-behind)
			
		GLU.gluLookAt(5f, 5f, 5f, 0f, 0f, 0f, 0f, 0f, 1f);
		
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(1f, 0f, 0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
			GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
			GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(0f, 1f, 0f);
			GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
			GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
			GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
			GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(0f, 0f, 1f);
			GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
			GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(1f, 1f, 0f);
			GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
			GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
			GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
			GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(1f, 1f, 0f);
			GL11.glVertex3f( 1.0f,-1.0f, 1.0f);
			GL11.glVertex3f(-1.0f,-1.0f, 1.0f);
			GL11.glVertex3f( 1.0f,-1.0f,-1.0f);
			GL11.glVertex3f(-1.0f,-1.0f,-1.0f);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_QUADS); GL11.glColor3f(1f, 1f, 0f);
			GL11.glVertex3f( 1.0f, 1.0f, 1.0f);
			GL11.glVertex3f(-1.0f, 1.0f, 1.0f);
			GL11.glVertex3f( 1.0f, 1.0f,-1.0f);
			GL11.glVertex3f(-1.0f, 1.0f,-1.0f);
			GL11.glEnd();
		
		
		
	}

	/**
	 * Initialise the game
	 * @throws Exception if init fails
	 */
	private static void init(boolean fullscreen) throws Exception {
		// Create a fullscreen window with 1:1 orthographic 2D projection (default)
		Display.setTitle(GAME_TITLE);
		Display.setDisplayMode(new DisplayMode(800, 600));
		Display.setFullscreen(fullscreen);
		Display.setVSyncEnabled(true);
		Display.create();

		GL11.glEnable(GL11.GL_TEXTURE_2D); // Enable Texture Mapping
		GL11.glShadeModel(GL11.GL_SMOOTH); // Enable Smooth Shading
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		GL11.glClearDepth(1.0); // Depth Buffer Setup
		GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Testing To Do

		GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
		GL11.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(
				45.0f,
				Display.getDisplayMode().getWidth() / Display.getDisplayMode().getHeight(),
				0.1f,
				100.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix

		// Really Nice Perspective Calculations
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);

		Mouse.create();
		Mouse.setGrabbed(true);
		
		GL11.glPushMatrix();
	}

	private static void run() {

		while (!finished) {
			// Always call Window.update(), all the time - it does some behind the
			// scenes work, and also displays the rendered output
			Display.update();

			// Check for close requests
			if (Display.isCloseRequested()) {
				finished = true;
			} 

			// The window is in the foreground, so we should play the game
			else if (Display.isActive()) {
				logic();
				render();
				Display.sync(FRAMERATE);
			} 

			// The window is not in the foreground, so we can allow other stuff to run and
			// infrequently update
			else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				logic();

				// Only bother rendering if the window is visible or dirty
				if (Display.isVisible() || Display.isDirty()) {
					render();
				}
			}
		}
	}
	
	/**
	 * Do any game-specific cleanup
	 */
	private static void cleanup() {
		// Close the window
		Display.destroy();
	}

	/**
	 * Do all calculations, handle input, etc.
	 */
	private static void logic() {
		
		// Example input handler: we'll check for the ESC key and finish the game instantly when it's pressed
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			finished = true;
		}
		
		float speed = 0.2f;
		
		/*if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			pos.z+=speed*Math.sin((rot.x*3.14)/180)*Math.cos((rot.z*3.14)/180);
			pos.x+=speed*Math.cos((rot.x*3.14)/180)*Math.cos((rot.z*3.14)/180);
			pos.y+=speed*Math.sin(-(rot.z*3.14)/180);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			pos.z-=speed*Math.sin((rot.x*3.14)/180)*Math.cos(-(rot.z*3.14)/180);
			pos.x-=speed*Math.cos((rot.x*3.14)/180)*Math.cos(-(rot.z*3.14)/180);
			pos.y-=speed*Math.sin(-(rot.z*3.14)/180);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pos.z+=speed*Math.cos(-(rot.x*3.14)/180)*Math.cos((rot.y*3.14)/180);
			pos.x+=speed*Math.sin(-(rot.x*3.14)/180)*Math.cos((rot.y*3.14)/180);
			pos.y+=speed*Math.sin(-(rot.y*3.14)/180);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
			pos.z-=speed*Math.cos(-(rot.x*3.14)/180)*Math.cos(-(rot.y*3.14)/180);
			pos.x-=speed*Math.sin(-(rot.x*3.14)/180)*Math.cos(-(rot.y*3.14)/180);
			pos.y-=speed*Math.sin(-(rot.y*3.14)/180);
		}*/
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			pos.x+=speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			pos.x-=speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			pos.z+=speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			pos.z-=speed;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
			rot.z-=2;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_E)) {
			rot.z+=2;
		}
		float DY = Mouse.getDX()/2.0f;
		float DX = Mouse.getDY()/2.0f;

		rot.x-=speed*DX;
		rot.y-=-speed*DY;
		Mouse.setCursorPosition(Display.getDisplayMode().getWidth()/2, Display.getDisplayMode().getHeight()/2);
	}
}