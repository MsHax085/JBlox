
package jblox;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-30
 * @version 1.0
 */
public class GraphicsProcessor {
    
    private final ChunkProcessor chunkProcessor = new ChunkProcessor();
    
    private final float FOV = 90.0f;
    private final float NEAR_VIEW_DISTANCE = 1.0f;
    
    // Drawing outside this distance may cause objects to disappear/flicker on screen at certain angle
    private final float FAR_VIEW_DISTANCE = 100.0f;
    
    private final float NEAR_FOG = FAR_VIEW_DISTANCE - 10;
    private final float FAR_FOG = FAR_VIEW_DISTANCE;
    
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
        
        final float aspect_ratio = (float) width / (float) height;
        
        GLU.gluPerspective(FOV, aspect_ratio, NEAR_VIEW_DISTANCE, FAR_VIEW_DISTANCE);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        initFog();
    }

    /**
     * Initializes the OpenGL graphics rendering.
     */
    public void initOpenGL() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);// Enable 2D texture mapping
        GL11.glShadeModel(GL11.GL_SMOOTH);// Enable Smooth Shading
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);// Values when color buffers are cleared
        GL11.glClearDepth(1.0f);// Value when depth buffer is cleared
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);// Enable depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);// Type of depth testing
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);// Perspective calculations
        
        // Enable VBO
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        
        // Enable Lightning
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));

        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, floatBuffer(0.6f, 0.6f, 0.6f, 1.0f));
    }
    
    private FloatBuffer floatBuffer(final float a, final float b, final float c, final float d) {
        final float[] data = new float[]{a,b,c,d};
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        {
            buffer.put(data);
            buffer.flip();
        }
        return buffer;
    }
    
    private void initFog() {
        GL11.glEnable(GL11.GL_FOG);
        {
            final FloatBuffer fogColours = BufferUtils.createFloatBuffer(4);
            {
                fogColours.put(new float[]{0, 0, 0, 1});
                fogColours.flip();
            }
            GL11.glClearColor(0, 0, 0, 1);
            GL11.glFog(GL11.GL_FOG_COLOR, fogColours);
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_FASTEST);// GL_NICEST
            GL11.glFogf(GL11.GL_FOG_START, NEAR_FOG);
            GL11.glFogf(GL11.GL_FOG_END, FAR_FOG);
            GL11.glFogf(GL11.GL_FOG_DENSITY, 0.005f);
        }
    }
    
    // Rendering methods -------------------------------------------------------
    
    public void draw(final float x, final float y, final float z, final float yaw, final float pitch, final boolean isMoving) {// Player position
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);// Clear screen & depth buffer
        GL11.glLoadIdentity();// Reset View
        
        // Set view
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(x, y, z);
        
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, floatBuffer(x, 256, z, 0.0f));
        
        chunkProcessor.drawChunks((int) x, (int) z, isMoving);
    }
    
    public void clear() {
        chunkProcessor.clear();
    }
}
