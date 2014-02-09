package jblox;

import java.util.ArrayList;
import java.util.Random;
import static jblox.ChunkConstants.BYTES_PER_VERTEX;
import static jblox.ChunkConstants.VBO_BUFFER_LENGTH;
import static jblox.ChunkConstants.VERTEX_DATA_LENGTH;
import jblox.generator.ChunkNoiseGenerator;
import jblox.generator.ChunkVboGenerator;
import jblox.generator.Point2D;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkProcessor {
    
    private final ChunkNoiseGenerator chunkNoiseGenerator = new ChunkNoiseGenerator();
    private final ChunkVboGenerator chunkVboGenerator = new ChunkVboGenerator();
    
    private final TextureProcessor textures = new TextureProcessor();

    private final ArrayList<Point2D> loadedChunks = new ArrayList<>();
    private final byte CHUNK_RENDER_RADIUS = 1;
    
    public void renderChunk(final Chunk chunk) {
        
        final int primaryVboHandle = chunk.getPrimaryVboHandle();
        final int stoneTextureId = textures.getTexture("STONE").getTextureID();
        
        for (int handle : chunk.getVboHandles()) {
            
            if (!(handle > 0)) {// 0 IF NO VBO
                break;
            }
            
            GL11.glPushMatrix();
            GL11.glTranslatef(0, (handle - primaryVboHandle) * 16, 0);// TRANSLATE VBO ALONG Y-AXIS
            
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, stoneTextureId);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);

                GL11.glVertexPointer(3, GL11.GL_FLOAT, BYTES_PER_VERTEX, 0);
                GL11.glNormalPointer(GL11.GL_FLOAT, BYTES_PER_VERTEX, 12);
                GL11.glTexCoordPointer(2, GL11.GL_FLOAT, BYTES_PER_VERTEX, 24);

                GL11.glDrawArrays(GL11.GL_QUADS, 0, VBO_BUFFER_LENGTH / VERTEX_DATA_LENGTH);
            }
            GL11.glPopMatrix();
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    
    /**
     * Temporary drawing method for plain chunks
     * @param x player x-position
     * @param z player z-position
     */
    public void drawChunks(final int x, final int z) {// Player position
        
        final int chunk_x = x / 16;// Default: x / 16
        final int chunk_z = z / 16;// Default: z / 16
        
        // Visible Chunks
        final int chunk_x_min = (chunk_x + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_min = (chunk_z + CHUNK_RENDER_RADIUS) * -1;
        final int chunk_x_max = (chunk_x - CHUNK_RENDER_RADIUS) * -1;
        final int chunk_z_max = (chunk_z - CHUNK_RENDER_RADIUS) * -1;
        
        final ArrayList<Point2D> unloaded_chunks_buffer = new ArrayList<>();
        
        for (Point2D p2d : loadedChunks) {
            
            // Unload chunks
            if (!(p2d.x >= chunk_x_min && p2d.x <= chunk_x_max)) {
                if (!(p2d.z >= chunk_z_min && p2d.z <= chunk_z_max)) {
                    
                    clearChunk(p2d);
                    unloaded_chunks_buffer.add(p2d);
                    continue;
                    
                }
            }
            
            // Draw chunks
            drawChunk(p2d);
        }
        
        if (!unloaded_chunks_buffer.isEmpty()) {
            loadedChunks.removeAll(unloaded_chunks_buffer);
            unloaded_chunks_buffer.clear();
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
                    createChunk(p2d);
                    drawChunk(p2d);
                }
            }
        }
    }
    
    private void drawChunk(final Point2D p2d) {
        
        final int cx_global = p2d.x * 16;
        final int cz_global = p2d.z * 16;

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(cx_global, 0, cz_global);
            renderChunk(p2d.chunkReference);

        }
        GL11.glPopMatrix();
    }
    
    private void createChunk(final Point2D p2d) {
        chunkNoiseGenerator.generateNoise(p2d);
        chunkVboGenerator.generateVBOHandles(p2d.chunkReference);
        chunkVboGenerator.generateVBOs(p2d.chunkReference);

        loadedChunks.add(p2d);
    }
    
    public void clear() {
        
        for (Point2D p2d : loadedChunks) {
            clearChunk(p2d);
        }
        
        loadedChunks.clear();
    }
    
    private void clearChunk(final Point2D p2d) {
        
        for (int handle : p2d.chunkReference.getVboHandles()) {
            
            if (!(handle > 0)) {
                break;
            }
            
            GL15.glDeleteBuffers(handle);
        }
    }
}
