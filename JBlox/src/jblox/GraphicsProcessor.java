
package jblox;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Richard
 * @since 2013-nov-30
 * @version 1.0
 */
public class GraphicsProcessor {
    
    private float rotquad;
    
    // Init-methods ------------------------------------------------------------

    /**
     * Creates the OpenGL scene and setup camera perspective.
     * @param  width   Specifies the width of the window
     * @param  height  Specifies the height of the window 
     */
    public void initOpenGLScene(final int width, int height) {
        
        if (height == 0) {
            height = 1;
        }
        
        GL11.glViewport(0, 0, width, height);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        
        final float fov = 90.0f;
        final float aspect_ratio = (float) width / (float) height;
        final float near = 1.0f;
        final float far = 100.0f;
        
        GLU.gluPerspective(fov, aspect_ratio, near, far);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    /**
     * Initializes the OpenGL graphics rendering.
     */
    public void initOpenGL() {
        GL11.glShadeModel(GL11.GL_SMOOTH);// Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);// Values when color buffers are cleared
        GL11.glClearDepth(1.0f);// Value when depth buffer is cleared
        GL11.glEnable(GL11.GL_CULL_FACE);// Don't draw faces facing away from camera
        GL11.glEnable(GL11.GL_DEPTH_TEST);// Enable depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);// Type of depth testing
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);// Perspective calculations
    }
    
    // Rendering methods -------------------------------------------------------
    
    public void draw(final float x, final float y, final float z, final float yaw, final float pitch) {
        //  Clear screen & depth buffer
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity();// Reset View
        
        //  Set view
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(x, y, z);
        
        GL11.glPushMatrix();
        GL11.glTranslatef(5.0f, 0.0f, -10.0f);// Move Oject
        drawRotatingSquare();
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslatef(-5.0f, 0.0f, -10.0f);
        drawRotatingSquare();
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, 0.0f, -20.0f);
        drawRotatingSquare();
        GL11.glPopMatrix();
    }
    
    /**
     * Draws a rotating square
     */
    private void drawRotatingSquare() {
        GL11.glRotatef(rotquad, 0.0f, 1.0f, 0.0f);	// Rotate Y-Axis
        GL11.glRotatef(rotquad, 1.0f, 1.0f, 1.0f);// Rotate All-Axis
        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glColor3f(0.0f,1.0f,0.0f);// Blue
            GL11.glVertex3f( 1.0f, 1.0f,-1.0f);// Top Right (TOP)
            GL11.glVertex3f(-1.0f, 1.0f,-1.0f);// Top Left (TOP)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);// Bottom Left (TOP)
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);// Bottom Right (TOP)

            GL11.glColor3f(1.0f, 0.5f, 0.0f);// Orange
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);// Top Right (BOTTOM)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);// Top Left (BOTTOM)
            GL11.glVertex3f(-1.0f,-1.0f,-1.0f);// Bottom Left (BOTTOM)
            GL11.glVertex3f( 1.0f,-1.0f,-1.0f);// Bottom Right (BOTTOM)

            GL11.glColor3f(1.0f, 0.0f, 0.0f);// Red
            GL11.glVertex3f( 1.0f, 1.0f, 1.0f);// Top Right (FRONT)
            GL11.glVertex3f(-1.0f, 1.0f, 1.0f);// Top Left (FRONT)
            GL11.glVertex3f(-1.0f,-1.0f, 1.0f);// Bottom Left (FRONT)
            GL11.glVertex3f( 1.0f,-1.0f, 1.0f);// Bottom Right (FRONT)

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
}
