package jblox;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkProcessor {

    private final TextureProcessor textures = new TextureProcessor();
    private final byte CHUNK_RENDER_RADIUS = 1;
    
    /**
     * Temporary drawing method for plain chunks
     * @param x player x-position
     * @param z player z-position
     */
    public void drawChunks(final int x, final int z) {
        
        final int chunk_x = 0 / 16;
        final int chunk_z = 0 / 16;
        
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

                    for (int block_y = 0; block_y < 16; block_y++) {

                        for (int block_x = 0; block_x < 16; block_x++) {

                            for (int block_z = 0; block_z < 16; block_z++) {

                                    GL11.glPushMatrix();
                                    {
                                        GL11.glTranslatef(block_x, block_y, block_z);
                                        drawBlock(cx_global + block_x, block_y, cz_global + block_z, (chunk_x_min * 16), (chunk_x_max * 16), (chunk_z_min * 16), (chunk_z_max * 16));
                                    }
                                    GL11.glPopMatrix();

                            }

                        }
                    }
                }
                
                GL11.glPopMatrix();
            
            }
            
        }
    }
    
    /**
     * Draw block to matrix
     * @param x Block x-coordinate
     * @param y Block y-coordinate
     * @param z Block z-coordinate
     * @param tXMin Temporary Min-Chunk-X-Location
     * @param tXMax Temporary Max-Chunk-X-Location
     * @param tZMin Temporary Min-Chunk-Z-Location
     * @param tZMax Temporary Max-Chunk-Z-Location
     */
    private void drawBlock(final int x, final int y, final int z, final int tXMin, final int tXMax, final int tZMin, final int tZMax) {
        
        Color.white.bind();
        textures.getTexture("").bind();
        
        GL11.glBegin(GL11.GL_QUADS);
        {
            float width = 0.5f;// 0.5f
            
            if (!isSolidBlock(x, y + 1, z, tXMin, tXMax, tZMin, tZMax)) {
                drawTopFace(width);
            }
            
            if (!isSolidBlock(x, y - 1, z, tXMin, tXMax, tZMin, tZMax)) {
                drawBottomFace(width);
            }
                
            if (!isSolidBlock(x, y, z + 1, tXMin, tXMax, tZMin, tZMax)) {
                drawFrontFace(width);
            }
            
            if (!isSolidBlock(x, y, z - 1, tXMin, tXMax, tZMin, tZMax)) {
                drawBackFace(width);
            }
            
            if (!isSolidBlock(x - 1, y, z, tXMin, tXMax, tZMin, tZMax)) {
                drawLeftFace(width);
            }
            
            if (!isSolidBlock(x + 1, y, z, tXMin, tXMax, tZMin, tZMax)) {
                drawRightFace(width);
            }
        }
        GL11.glEnd();
    }
    
    /**
     * Draw the top block face
     * @param width Width of block
     */
    private void drawTopFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f( width, width,-width);// Top Right (TOP)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-width, width,-width);// Top Left (TOP)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-width, width, width);// Bottom Left (TOP)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f( width, width, width);// Bottom Right (TOP)
    }
    
    /**
     * Draw the bottom block face
     * @param width Width of block
     */
    private void drawBottomFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f( width,-width, width);// Top Right (BOTTOM)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-width,-width, width);// Top Left (BOTTOM)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-width,-width,-width);// Bottom Left (BOTTOM)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f( width,-width,-width);// Bottom Right (BOTTOM)
    }
    
    /**
     * Draw the front block face
     * @param width Width of block
     */
    private void drawFrontFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f( width, width, width);// Top Right (FRONT)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-width, width, width);// Top Left (FRONT)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-width,-width, width);// Bottom Left (FRONT)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f( width,-width, width);// Bottom Right (FRONT)
    }
    
    /**
     * Draw the back block face
     * @param width Width of block
     */
    private void drawBackFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f( width,-width,-width);// Top Right (BACK)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-width,-width,-width);// Top Left (BACK)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-width, width,-width);// Bottom Left (BACK)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f( width, width,-width);// Bottom Right (BACK)
    }
    
    /**
     * Draw the left block face
     * @param width Width of block
     */
    private void drawLeftFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f(-width, width, width);// Top Right (LEFT)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f(-width, width,-width);// Top Left (LEFT)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f(-width,-width,-width);// Bottom Left (LEFT)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f(-width,-width, width);// Bottom Right (LEFT)
    }
    
    /**
     * Draw the right block face
     * @param width Width of block
     */
    private void drawRightFace(final float width) {
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex3f( width, width,-width);// Top Right (RIGHT)
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex3f( width, width, width);// Top Left (RIGHT)
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex3f( width,-width, width);// Bottom Left (RIGHT)
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex3f( width,-width,-width);// Bottom Right (RIGHT)
    }
    
    /**
     * If there's a solid block at specified coordinates
     * @param x     Block X-Position
     * @param y     Block Y-Position
     * @param z     Block Z-Position
     * @param tXMin Temporary Min-Chunk-X-Location
     * @param tXMax Temporary Max-Chunk-X-Location
     * @param tZMin Temporary Min-Chunk-Z-Location
     * @param tZMax Temporary Max-Chunk-Z-Location
     * @return If solid block
     */
    private boolean isSolidBlock(final int x, final int y, final int z, final int tXMin, final int tXMax, final int tZMin, final int tZMax) {
        
        /* 
            Temporary Code
        */
        if (y < 16 && y > -1) {
            if (x >= tXMin && x < tXMax) {
                if (z >= tZMin && z < tZMax) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
