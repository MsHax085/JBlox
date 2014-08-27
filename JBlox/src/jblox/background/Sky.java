package jblox.background;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard
 * @since 2014-aug-27, 18:16:37
 * @version 0.0.1
 */
public class Sky {
    
    private int gradient_vbo_handle;

    public void createSky(float near_fog, float far_fog) {
        createFog(near_fog, far_fog);
        createGradient();
    }
    
    private void createFog(float near_fog, float far_fog) {
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
            GL11.glFogf(GL11.GL_FOG_START, near_fog);
            GL11.glFogf(GL11.GL_FOG_END, far_fog);
            GL11.glFogf(GL11.GL_FOG_DENSITY, 0.2f);
        }
    }
    
    private void createGradient() {
        
        final IntBuffer buffer = BufferUtils.createIntBuffer(1);
        GL15.glGenBuffers(buffer);
        
        gradient_vbo_handle = buffer.get(0);
        
        final float[] quad = {
        //  x      y     r     g     b
         1.0f,  1.0f, 0.2f, 0.6f, 1.0f,
         1.0f, -1.0f, 0.2f, 0.7f, 1.0f,
        -1.0f, -1.0f, 0.4f, 0.7f, 1.0f,
        -1.0f,  1.0f, 0.4f, 0.6f, 1.0f};
        
        final FloatBuffer quadBuffer = BufferUtils.createFloatBuffer(quad.length);
        {
            quadBuffer.put(quad);
            quadBuffer.rewind();
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, gradient_vbo_handle);// Bind new buffer
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, quadBuffer, GL15.GL_STATIC_DRAW);// Upload buffer data
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// Unbind buffer
        
    }
    
    public void drawSky() {// For future use
        drawGradient();
    }
    
    private void drawGradient() {
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();// Reset View
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();// Reset View
        
        GL11.glDisable(GL11.GL_TEXTURE_2D);// Disable 2D texture mapping
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);// Disable depth testing
        {
            GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);
            GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);

            {
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, gradient_vbo_handle);
                {
                    GL11.glVertexPointer(2, GL11.GL_FLOAT, 20, 0);
                    GL11.glColorPointer(3, GL11.GL_FLOAT, 20, 8);

                    GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);// 4 = number of vertices

                    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
                }
            }
            GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
            GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
            GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
        }
        GL11.glEnable(GL11.GL_DEPTH_TEST);// Enable depth testing
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);// Enable 2D texture mapping
            
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();// Reset View
        GL11.glPopMatrix();
        
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();// Reset View
    }
}
