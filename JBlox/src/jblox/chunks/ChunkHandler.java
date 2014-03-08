package jblox.chunks;

import java.util.ArrayList;
import java.util.Map.Entry;
import jblox.client.Client;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import static jblox.chunks.ChunkConstants.BYTES_PER_VERTEX;
import static jblox.chunks.ChunkConstants.VBO_BUFFER_LENGTH;
import static jblox.chunks.ChunkConstants.VERTEX_DATA_LENGTH;
import jblox.generator.ChunkVboGenerator;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class ChunkHandler {
    
    private final Client client;
    private final ChunkProcessor processor;
    private final Thread processorThread;
   
    private final TextureProcessor textureProcessor = new TextureProcessor();
    private final ChunkVboGenerator chunkVboGenerator = new ChunkVboGenerator(textureProcessor);

    private final TreeMap<String, Chunk> chunk_buffer = new TreeMap<>();
    private final TreeMap<String, Chunk> create_buffer = new TreeMap<>();
    private final ArrayList<String> dispose_buffer = new ArrayList<>();
    
    private final ReadWriteLock create_lock = new ReentrantReadWriteLock();
    private final ReadWriteLock dispose_lock = new ReentrantReadWriteLock();
    
    private final Lock create_lock_w = create_lock.writeLock();
    private final Lock dispose_lock_w = dispose_lock.writeLock();
    
    // *************************************************************************
    
    public ChunkHandler(final Client client) {
        this.client = client;
        
        this.processor = new ChunkProcessor(this);
        this.processorThread = new Thread(this.processor);
        this.processorThread.start();
        
    }
    
    // *************************************************************************
    
    /**
     * This method returns a Client reference
     * @return Client A Client reference
     */
    public Client getClient() {
        return client;
    }
    
    /**
     * This method updates the chunk buffer
     * create_buffer must be cleared after it's data is copied, therefore
     * a writelock is used at entry. This minimize the risk of waiting to acquire 
     * the writelock after the reading is done. Using read- and writelocks is not
     * an option in this case.
     * 
     * This method may only be called from the main thread, from which OpenGL is
     * running.
     */
    private void updateChunkBuffer() {
        if (create_lock_w.tryLock()) {// DON'T WAIT TO ACQUIRE LOCK (DON'T LET THE RENDER THREAD WAIT)
            
            try {
                
                if (!create_buffer.isEmpty()) {
                    for (Entry entry : create_buffer.entrySet()) {

                        createChunkVbos((Chunk) entry.getValue());
                        chunk_buffer.put((String) entry.getKey(), (Chunk) entry.getValue());

                    }

                    create_buffer.clear();
                }
                
            } finally {
                create_lock_w.unlock();
            }
        }
        
        if (dispose_lock_w.tryLock()) {// DON'T WAIT TO ACQUIRE LOCK (DON'T LET THE RENDER THREAD WAIT)
            
            try {
                
                if (!dispose_buffer.isEmpty()) {
                    for (String key : dispose_buffer) {

                        clearChunk(chunk_buffer.get(key));
                        chunk_buffer.remove(key);

                    }

                    dispose_buffer.clear();    
                }
                
            } finally {
                dispose_lock_w.unlock();
            }
        }
    }
    
    /**
     * This method adds data to create_buffer
     * This method may only be called from the secondary thread
     * @param key The chunk coordinates
     * @param value The chunk reference
     */
    public void addToCreateBuffer(final String key, final Chunk value) {
        create_lock_w.lock();// WAIT TO ACQUIRE LOCK
        
        try {
            create_buffer.put(key, value);
        } finally {
            create_lock_w.unlock();
        }
    }
    
    /**
     * This method adds data to dispose_buffer
     * This method may only be called from the secondary thread
     * @param key The chunk coordinates
     */
    public void addToDisposeBuffer(final String key) {
        dispose_lock_w.lock();// WAIT TO ACQUIRE LOCK
        
        try {
            dispose_buffer.add(key);
        } finally {
            dispose_lock_w.unlock();
        }
    }
    
    // *************************************************************************
    
    /**
     * This method loop through and draw all the chunks
     */
    public void drawChunks() {
        
        updateChunkBuffer();
        
        for (Entry entry : chunk_buffer.entrySet()) {
            
            final String key = (String) entry.getKey();
            final String[] coordinates = key.split(" . ");
            
            final int x = Integer.parseInt(coordinates[0]);
            final int z = Integer.parseInt(coordinates[1]);
            
            drawChunk(x, z, (Chunk) entry.getValue());
        }
    }
    
    /**
     * This method push, pop and translate the matrix for a specific chunk
     * @param x The x coordinate for the given chunk
     * @param z The z coordinate for the given chunk
     * @param chunk The chunk to be drawn
     */
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
    
    /**
     * This method render a specific chunk
     * @param chunk The chunk to be drawn
     */
    public void renderChunk(final Chunk chunk) {
        
        final int textureId = textureProcessor.getTextureId();
        
        for (Entry<Byte, ChunkSection> entry : chunk.getChunkSections().entrySet()) {
            
            GL11.glPushMatrix();
            GL11.glTranslatef(0, entry.getKey() * 16, 0);// TRANSLATE VBO ALONG Y-AXIS
            
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, entry.getValue().getVboHandle());

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
     * This method create the VBOs for the given chunk
     * @param chunk The chunk for which the VBOs will be drawn
     */
    private void createChunkVbos(final Chunk chunk) {
        chunkVboGenerator.generateVBOHandles(chunk);
        chunkVboGenerator.generateVBOs(chunk);
    }
    
    /**
     * This method remove the VBOs for all chunks
     */
    public void clear() {
        
        for (Chunk chunk : chunk_buffer.values()) {
            clearChunk(chunk);
        }
        
        chunk_buffer.clear();
    }
    
    /**
     * This method remove the VBOs for a given chunk
     * @param chunk The chunk to be cleared
     */
    private void clearChunk(final Chunk chunk) {
        
        for (ChunkSection section : chunk.getChunkSections().values()) {
            
            final int handle = section.getVboHandle();
            
            if (!(handle > 0)) {
                break;
            }
            
            GL15.glDeleteBuffers(handle);
        }
    }
}
