package jblox;

import org.lwjgl.opengl.GL11;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkProcessor {

    private final Chunk chunk = new Chunk();
    private final byte CHUNK_RENDER_RADIUS = 1;
    
    public void generateChunks() {
        chunk.genereateChunk();
    }
    
    /**
     * Temporary drawing method for plain chunks
     * @param x player x-position
     * @param z player z-position
     */
    public void drawChunks(final int x, final int z) {// Player position
        
        final int chunk_x = 0 / 16;
        final int chunk_z = 0 / 16;
        
        // Visible Chunks
        final int chunk_x_min = (chunk_x + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_min = (chunk_z + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_x_max = (chunk_x - CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_max = (chunk_z - CHUNK_RENDER_RADIUS) * -1;
        
        // Loop through visible chunks & render
        for (int cx = chunk_x_min; cx < chunk_x_max; cx++) {// Change to chunk_x_max
            for (int cz = chunk_z_min; cz < chunk_z_max; cz++) {// Change to chunk_x_max
            
                final int cx_global = cx * 16;
                final int cz_global = cz * 16;
                
                GL11.glPushMatrix();
                {
                    GL11.glTranslatef(cx_global, 0, cz_global);
                    chunk.renderChunk();
                    
                }
                
                GL11.glPopMatrix();
            
            }
            
        }
    }
    
    public void clear() {
        chunk.clear();
    }
}
