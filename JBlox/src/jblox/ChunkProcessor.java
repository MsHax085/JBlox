package jblox;

import java.util.ArrayList;
import java.util.Random;
import jblox.generator.Point2D;
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkProcessor {

    private final ArrayList<Point2D> loadedChunks = new ArrayList<>();
    private final byte CHUNK_RENDER_RADIUS = 4;
    private final int seed = new Random().nextInt(Integer.MAX_VALUE);
    
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
        
        // Unload chunks
        for (Point2D p2d : loadedChunks) {
            
            if (!(p2d.x >= chunk_x_min && p2d.x <= chunk_x_max)) {
                if (!(p2d.z >= chunk_z_min && p2d.z <= chunk_z_max)) {
                    
                    p2d.chunkReference.clear();
                    loadedChunks.remove(p2d);
                }
            }
        }
        
        // Load new chunks
        for (int cx = chunk_x_min; cx <= chunk_x_max -1; cx++) {
            for (int cz = chunk_z_min; cz <= chunk_z_max -1; cz++) {
                
                boolean foundNewChunk = true;
                
                for (Point2D p2d : loadedChunks) {
                    
                    if (p2d.x == cx && p2d.z == cz) {
                        foundNewChunk = false;
                        break;
                    }
                }
                
                if (foundNewChunk) {
                    final Point2D p2d = new Point2D(cx, cz);
                    
                    p2d.chunkReference.genereateChunk(cx, cz, seed);
                    loadedChunks.add(p2d);
                }
            }
        }
        
        
        // Loop through visible chunks & render
        for (Point2D p2d : loadedChunks) {
            
            final int cx_global = p2d.x * 16;
            final int cz_global = p2d.z * 16;

            GL11.glPushMatrix();
            {
                GL11.glTranslatef(cx_global, 0, cz_global);
                p2d.chunkReference.renderChunk();

            }

            GL11.glPopMatrix();

        }
    }
    
    public void clear() {
        
        for (Point2D p2d : loadedChunks) {
            p2d.chunkReference.clear();
        }
    }
}
