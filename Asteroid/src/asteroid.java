import java.sql.Time;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.*;

/**
 * Asteroid game
 *
 * @author Akuryo <cogney.maxime@gmail.com>
 * @author Sporbie <sporbie@gmail.com>
 * @version 0.0
 */

public class Asteroid {

	public static final String GAME_TITLE = "Asteroid";
	private static final int FRAMERATE = 60;
	private static boolean finished;
	private static long startTime = System.nanoTime();


	public static void main(String[] args) {
		try {
			init(true);
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
		
		GL11.glTranslatef(0.0f,0.0f,(System.nanoTime()-startTime)/-1000000000.0f);
		GL11.glRotatef((System.nanoTime()-startTime)/-100000000.0f, 0.0f, 1.0f, 0.0f);
		
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3f(-1.0f, 1.0f, 0.0f);				// Top Left
			GL11.glVertex3f( 1.0f, 1.0f, 0.0f);				// Top Right
			GL11.glVertex3f( 1.0f,-1.0f, 0.0f);				// Bottom Right
			GL11.glVertex3f(-1.0f,-1.0f, 0.0f);
		GL11.glEnd();
	}

	/**
	 * Initialise the game
	 * @throws Exception if init fails
	 */
	private static void init(boolean fullscreen) throws Exception {
		// Create a fullscreen window with 1:1 orthographic 2D projection (default)
		Display.setTitle(GAME_TITLE);
		Display.setFullscreen(fullscreen);
		Display.setVSyncEnabled(true);
		Display.create();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPerspective(45, Display.getDisplayMode().getWidth()/Display.getDisplayMode().getHeight(), 0, 100);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearColor(0.0f, 0.0f,0.0f,0.0f);
		GL11.glClearDepth(1.0f);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
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
	}
}