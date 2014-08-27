
package jblox;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import jblox.chunks.ChunkConstants;
import jblox.chunks.ChunkHandler;
import jblox.client.Client;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.util.glu.GLU;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-30
 * @version 1.0
 */
public class Scene {
    
    private final ChunkHandler chunkHandler;
    
    private final float FOV = 90.0f;
    private final float NEAR_VIEW_DISTANCE = 0.1f;
    
    // Drawing outside this distance may cause objects to disappear/flicker on screen at certain angle
    private final float FAR_VIEW_DISTANCE = (ChunkConstants.RENDER_RADIUS + 1) * 16;// 100.0f
    
    private final float NEAR_FOG = FAR_VIEW_DISTANCE - 32;
    private final float FAR_FOG = FAR_VIEW_DISTANCE - 16;
    
    private int background_gradient_vbo_handle;
    
    public Scene(final Client client) {
        this.chunkHandler = new ChunkHandler(client);
    }
    
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
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);// Values when color buffers are cleared
        GL11.glClearDepth(1.0f);// Value when depth buffer is cleared
        
        GL11.glEnable(GL11.GL_DEPTH_TEST);// Enable depth testing
        GL11.glDepthFunc(GL11.GL_LEQUAL);// Type of depth testing
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);// Perspective calculations
        
        GL11.glEnable(GL11.GL_BLEND);// Enable blending: "incoming primitive color is blended with the color already stored in the framebuffer"
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);// Set blending method
        GL11.glEnable(GL11.GL_ALPHA_TEST);// Enable alpha channels or transparency
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);// Specify reference value
        
        // Enable VBO
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        
        // Enable Lightning
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);

        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, floatBuffer(1.0f, 1.0f, 1.0f, 1.0f));

        GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, floatBuffer(0.6f, 0.6f, 0.6f, 1.0f));
        
        /**
         * Test
         */
        final IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer);
        background_gradient_vbo_handle = buffer.get(0);
        
        final float[] quad = {
    //      x     y     r     g     b
         1.0f,  1.0f, 0.2f, 0.5f, 1.0f,
         1.0f, -1.0f, 0.2f, 0.7f, 1.0f,
        -1.0f, -1.0f, 0.4f, 0.7f, 1.0f,
        -1.0f,  1.0f, 0.4f, 0.7f, 1.0f};
        
        final FloatBuffer quadBuffer = BufferUtils.createFloatBuffer(quad.length);
        quadBuffer.put(quad);
        quadBuffer.rewind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, background_gradient_vbo_handle);// Bind new buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadBuffer, GL15.GL_STATIC_DRAW);// Upload buffer data
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// Unbind buffer
        
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
                fogColours.put(new float[]{0.4f, 0.7f, 1.0f, 0.2f});
                fogColours.flip();
            }
            GL11.glClearColor(0, 0, 0, 1);
            GL11.glFog(GL11.GL_FOG_COLOR, fogColours);
            GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
            GL11.glHint(GL11.GL_FOG_HINT, GL11.GL_FASTEST);// GL_NICEST
            GL11.glFogf(GL11.GL_FOG_START, NEAR_FOG);
            GL11.glFogf(GL11.GL_FOG_END, FAR_FOG);
            GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f);
        }
    }
    
    // Rendering methods -------------------------------------------------------
    
    public void draw(final float x, final float y, final float z, final float yaw, final float pitch, final boolean isMoving) {// Player position
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);// Clear screen & depth buffer
        GL11.glLoadIdentity();// Reset View
        
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();// Reset View
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();// Reset View

        GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        {
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, background_gradient_vbo_handle);
            {
                GL11.glVertexPointer(2, GL11.GL_FLOAT, 20, 0);
                GL11.glColorPointer(3, GL11.GL_FLOAT, 20, 8);

                GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);// 4 = no. of vertices

                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            }
        }
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
        GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
            
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();// Reset View
        GL11.glPopMatrix();
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();// Reset View
        
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        
        // Set view
        GL11.glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(x, y, z);
        
        GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, floatBuffer(-x, 256, -z, 1.0f));// 0.0f = direction, 1.0f = point
        
        chunkHandler.drawChunks();
    }
    
    public void clear() {
        chunkHandler.clear();
    }
}
