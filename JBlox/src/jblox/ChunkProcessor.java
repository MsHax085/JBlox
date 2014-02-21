package jblox;

import java.util.ArrayList;
import java.util.TreeMap;
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

    // DEPRECATED
    private final ArrayList<Point2D> loaded_chunks_buffer = new ArrayList<>();
        final ArrayList<Point2D> unloaded_chunks_buffer = new ArrayList<>();
        
    private final TreeMap<String, Chunk> primary_chunk_buffer = new TreeMap<>();
    private final TreeMap<String, Chunk> secondary_chunk_buffer = new TreeMap<>();
    
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
        
        // UPDATE SECONDARY CHUNK BUFFER
        for (int cx = chunk_x_min; cx <= chunk_x_max -1; cx++) {
            for (int cz = chunk_z_min; cz <= chunk_z_max -1; cz++) {
                
                final String key = cx + "." + cz;
                final Chunk value;
                
                if (primary_chunk_buffer.containsKey(key)) {// MOVE ALREADY LOADED CHUNKS
                    value = primary_chunk_buffer.get(key);
                    secondary_chunk_buffer.put(key, value);
                    
                } else {// LOAD NEW CHUNKS TO SECONDARY BUFFER
                    final Chunk chunk = new Chunk();
                    {
                        createChunk(cx, cz, chunk);
                        secondary_chunk_buffer.put(key, chunk);
                        value = chunk;
                    }
                }
                
                drawChunk(cx, cz, value);
            }
        }
        
        // FLIP BUFFERS
        primary_chunk_buffer.clear();
        primary_chunk_buffer.putAll(secondary_chunk_buffer);
        secondary_chunk_buffer.clear();
    }
    
    private void drawChunk(final int x, final int z, final Chunk chunk) {
        
        final int cx_global = x * 16;
        final int cz_global = z * 16;

        GL11.glPushMatrix();
        {
            GL11.glTranslatef(cx_global, 0, cz_global);
            renderChunk(chunk);

        }
        GL11.glPopMatrix();
    }
    
    private void createChunk(final int x, final int z, final Chunk chunk) {
        chunkNoiseGenerator.generateNoise(x, z, chunk);
        chunkVboGenerator.generateVBOHandles(chunk);
        chunkVboGenerator.generateVBOs(chunk);
    }
    
    public void clear() {
        
        for (Point2D p2d : loaded_chunks_buffer) {
            clearChunk(p2d);
        }
        
        loaded_chunks_buffer.clear();
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
