
package jblox;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Richard
 * @since 2013-nov-25
 * @version 1.0
 */
public class ClientDisplay {
    
    private final Entry entry;
    
    private final int WIDTH = 1000;
    private final int HEIGHT = 600;
    
    private long lastFrame;
    private long lastFPS;
    
    private int fps;
    
    private float rotquad;
    
    public ClientDisplay(final Entry entry) {
        this.entry = entry;
    }
    
    // -------------------------------------------------------------------------

    public void start() {
        
        initializeDisplay();
        initializeOpenGLScene(WIDTH, HEIGHT);
        if (!initializeOpenGL()) {
            Display.destroy();
        }
        
        getDelta();
        lastFPS = getTime();
        
        while (!Display.isCloseRequested()) {

            final int delta = getDelta();
            
            update(delta);
            renderGraphics();
            
            Display.update();
            Display.sync(60);// Cap at 60fps
        }
        
        Display.destroy();
    }
    
    private void initializeDisplay() {
        
        final DisplayMode mode = new DisplayMode(WIDTH, HEIGHT);
        
        try {
            Display.setDisplayMode(mode);
            Display.create();
        } catch (LWJGLException ex) {
            System.out.println("Exception caught while initializing display: " + ex.getMessage());
            System.exit(0);
        }
    }
    
    private void initializeOpenGLScene(final int width, int height) {
        
        if (height == 0) {
            height = 1;
        }
        
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        final float fov = 45.0f;
        final float aspect_ratio = (float)width/(float)height;
        final float near = 1.0f;
        final float far = 100.0f;
        
        GLU.gluPerspective(fov, aspect_ratio, near, far);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }
    
    private boolean initializeOpenGL() {
        
        GL11.glShadeModel(GL11.GL_SMOOTH);  // Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);//    Values when color buffers are cleared
        GL11.glClearDepth(1.0f);//  Value when depth buffer is cleared
        GL11.glEnable(GL11.GL_DEPTH_TEST);//    Enable depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);// Type of depth testing
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);// Perspective calculations
        return true;
    }
    
    private void update(final int delta) {
        final ClientInput clientInput = entry.getClientInput();
        if (clientInput != null) {
            clientInput.checkForInput();
        }
        
        updateFPS();
    }
    
    private void renderGraphics() {
        //  Clear screen & depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();//    Reset View
        
        GL11.glTranslatef(0.0f, 0.0f, -7.0f);// Translate Into The Screen 7.0 Units
        GL11.glRotatef(rotquad, 0.0f, 1.0f, 0.0f);	//  Rotate Y-Axis
        GL11.glRotatef(rotquad, 1.0f, 1.0f, 1.0f);//    Rotate All-Axis
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glColor3f(0.0f,1.0f,0.0f);//   Blue
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);//   Top Right (TOP)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);//   Top Left (TOP)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);//   Bottom Left (TOP)
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);//   Bottom Right (TOP)
            
            GL11.glColor3f(1.0f, 0.5f, 0.0f);//   Orange
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);//   Top Right (BOTTOM)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);//   Top Left (BOTTOM)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);//   Bottom Left (BOTTOM)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);//   Bottom Right (BOTTOM)
            
            GL11.glColor3f(1.0f, 0.0f, 0.0f);// Red
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);//   Top Right (BOTTOM)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);//   Top Left (BOTTOM)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);//   Bottom Left (BOTTOM)
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);//   Bottom Right (BOTTOM)
            
            GL11.glColor3f(1.0f, 1.0f, 0.0f);// Yellow
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);// Top Right (BACK)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);// Top Left (BACK)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);// Bottom Left (BACK)
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);// Bottom Right (BACK)
            
            GL11.glColor3f(0.0f, 0.0f, 1.0f);// Blue
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);// Top Right (LEFT)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);// Top Left (LEFT)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);// Bottom Left (LEFT)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);// Bottom Right (LEFT)
            
            GL11.glColor3f(1.0f, 0.0f, 1.0f);// Violet
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);// Top Right (RIGHT)
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);// Top Left (RIGHT)
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);// Bottom Left (RIGHT)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);// Bottom Right (RIGHT)
        }
        GL11.glEnd();

        rotquad +=0.1f;
    }
    
    // -------------------------------------------------------------------------
    
    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    
    private int getDelta() {
        final long time = getTime();
        final int delta = (int) (time - lastFrame);
        lastFrame = time;
        
        return delta;
    }
    
    private void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }
}
