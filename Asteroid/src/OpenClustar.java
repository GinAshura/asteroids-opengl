

import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.*;
import org.lwjgl.util.vector.Quaternion;
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
	private static Vector3f pos = new Vector3f(0f,0f,0f);
	private static Vector3f rot = new Vector3f(1f,0f,0f);
	private static Quaternion q = new Quaternion();

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
		

		float[] rr=GetMatrix(q);
		
		FloatBuffer br=BufferUtils.createFloatBuffer(16*4);
		
		br.put(rr);
		
		
		
		/*GL11.glRotatef(rot.y, 0f, 1.0f, 0f);
		GL11.glRotatef(rot.x, 1.0f, 0f, 0f);
		GL11.glRotatef(rot.z, 0f, 0f, 1.0f);*/
		GL11.glLoadMatrix(br);
		GL11.glTranslatef(-pos.x,-pos.y,-pos.z);
		
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

	
	private static Quaternion FromAxis(float x, float y, float z, float deg)
	{
		Quaternion qat = new Quaternion();
		float angle = (float)((deg / 180.0f) * 3.14159265f);

		// Here we calculate the sin( theta / 2) once for optimization
		float result = (float)Math.sin( angle / 2.0f );

		// Calcualte the w value by cos( theta / 2 )
		qat.w = (float)Math.cos( angle / 2.0f );

		// Calculate the x, y and z of the quaternion
		qat.x = (float)(x * result);
		qat.y = (float)(y * result);
		qat.z = (float)(z * result);
		
		return qat;
	}
	
	private static float[] GetMatrix (Quaternion q)
	{
		float pMatrix[]=new float[16];
		pMatrix[ 0] = 1.0f - 2.0f * ( q.y * q.y + q.z * q.z );
		pMatrix[ 1] = 2.0f * (q.x * q.y + q.z * q.w);
		pMatrix[ 2] = 2.0f * (q.x * q.z - q.y * q.w);
		pMatrix[ 3] = 0.0f;
		
		// Second row
		pMatrix[ 4] = 2.0f * ( q.x * q.y - q.z * q.w );
		pMatrix[ 5] = 1.0f - 2.0f * ( q.x * q.x + q.z * q.z );
		pMatrix[ 6] = 2.0f * (q.z * q.y + q.x * q.w );
		pMatrix[ 7] = 0.0f;

		// Third row
		pMatrix[ 8] = 2.0f * ( q.x * q.z + q.y * q.w );
		pMatrix[ 9] = 2.0f * ( q.y * q.z - q.x * q.w );
		pMatrix[10] = 1.0f - 2.0f * ( q.x * q.x + q.y * q.y );
		pMatrix[11] = 0.0f;

		// Fourth row
		pMatrix[12] = 0;
		pMatrix[13] = 0;
		pMatrix[14] = 0;
		pMatrix[15] = 1.0f;

		return pMatrix;
		
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
		Quaternion q1=FromAxis(1.f,0.f,0.f,rot.x);
		Quaternion q2=FromAxis(0.f,1.f,0.f,rot.y);
		Quaternion q3=FromAxis(0.f,0.f,1.f,rot.z);
		q1.normalise();
		q2.normalise();
		q3.normalise();
		Quaternion qq=new Quaternion();
		Quaternion.mul(q2, q3, qq);
		Quaternion.mul(q1,qq,q);
		q.normalise();
		Mouse.setCursorPosition(Display.getDisplayMode().getWidth()/2, Display.getDisplayMode().getHeight()/2);
	}
}