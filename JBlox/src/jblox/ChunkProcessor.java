package jblox;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkProcessor {

    private final byte CHUNK_RENDER_RADIUS = 4;
    
    /**
     * Temporary drawing method for plain chunks
     * @param x player x-position
     * @param z player z-position
     */
    public void drawChunks(final int x, final int z) {
        
        final int chunk_x = x / 16;
        final int chunk_z = z / 16;
        
        final int chunk_x_min = (chunk_x + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_min = (chunk_z + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_x_max = (chunk_x - CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_max = (chunk_z - CHUNK_RENDER_RADIUS) * -1;
        
        for (int cx = chunk_x_min; cx < chunk_x_max; cx++) {
            
            for (int cz = chunk_z_min; cz < chunk_z_max; cz++) {
            
                final int cx_global = cx * 16;
                final int cz_global = cz * 16;
                
                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(cx_global, 0, cz_global);

                    for (int block_x = 0; block_x < 16; block_x++) {

                        for (int block_z = 0; block_z < 16; block_z++) {

                                GL11.glPushMatrix();
                                {
                                    GL11.glTranslatef(block_x, 0, block_z);
                                    drawBlock();
                                }
                                GL11.glPopMatrix();

                        }

                    }
                }
                
                GL11.glPopMatrix();
            
            }
            
        }
    }
    
    /**
     * Draws a block on matrix
     */
    private void drawBlock() {
        GL11.glBegin(GL11.GL_QUADS);
        {
            float width = 0.45f;
            GL11.glColor3f(0.0f,1.0f,0.0f);// Blue
            GL11.glVertex3f( width, width,-width);// Top Right (TOP)
            GL11.glVertex3f(-width, width,-width);// Top Left (TOP)
            GL11.glVertex3f(-width, width, width);// Bottom Left (TOP)
            GL11.glVertex3f( width, width, width);// Bottom Right (TOP)

            GL11.glColor3f(1.0f, 0.5f, 0.0f);// Orange
            GL11.glVertex3f( width,-width, width);// Top Right (BOTTOM)
            GL11.glVertex3f(-width,-width, width);// Top Left (BOTTOM)
            GL11.glVertex3f(-width,-width,-width);// Bottom Left (BOTTOM)
            GL11.glVertex3f( width,-width,-width);// Bottom Right (BOTTOM)

            GL11.glColor3f(1.0f, 0.0f, 0.0f);// Red
            GL11.glVertex3f( width, width, width);// Top Right (FRONT)
            GL11.glVertex3f(-width, width, width);// Top Left (FRONT)
            GL11.glVertex3f(-width,-width, width);// Bottom Left (FRONT)
            GL11.glVertex3f( width,-width, width);// Bottom Right (FRONT)

            GL11.glColor3f(1.0f, 1.0f, 0.0f);// Yellow
            GL11.glVertex3f( width,-width,-width);// Top Right (BACK)
            GL11.glVertex3f(-width,-width,-width);// Top Left (BACK)
            GL11.glVertex3f(-width, width,-width);// Bottom Left (BACK)
            GL11.glVertex3f( width, width,-width);// Bottom Right (BACK)

            GL11.glColor3f(0.0f, 0.0f, 1.0f);// Blue
            GL11.glVertex3f(-width, width, width);// Top Right (LEFT)
            GL11.glVertex3f(-width, width,-width);// Top Left (LEFT)
            GL11.glVertex3f(-width,-width,-width);// Bottom Left (LEFT)
            GL11.glVertex3f(-width,-width, width);// Bottom Right (LEFT)

            GL11.glColor3f(1.0f, 0.0f, 1.0f);// Violet
            GL11.glVertex3f( width, width,-width);// Top Right (RIGHT)
            GL11.glVertex3f( width, width, width);// Top Left (RIGHT)
            GL11.glVertex3f( width,-width, width);// Bottom Left (RIGHT)
            GL11.glVertex3f( width,-width,-width);// Bottom Right (RIGHT)
        }
        GL11.glEnd();
    }
}
