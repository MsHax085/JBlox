package jblox.chunks;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jblox.Entry;
import jblox.client.Client;
import jblox.generator.ChunkNoiseGenerator;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-23
 * @version 1.0
 */
public class ChunkProcessor implements Runnable {

    private final ChunkHandler handler;
    private final Client client;
    private final ChunkNoiseGenerator chunkNoiseGenerator = new ChunkNoiseGenerator();
    
    private final ArrayList<String> chunk_buffer_copy = new ArrayList<>();
    private final ArrayList<String> chunks_to_remove = new ArrayList<>();
    
    // *************************************************************************
    
    public ChunkProcessor(final ChunkHandler handler) {
        this.handler = handler;
        this.client = handler.getClient();
    }
    
    // *************************************************************************
    
    @Override
    public void run() {
        
        while (!Entry.stop.get()) {
            
            processChunks();
            
            try {
                Thread.sleep(200);// TODO: Determine by render-radius, longer = slower
            } catch (InterruptedException ex) {
                Logger.getLogger(ChunkProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        Entry.run.set(false);
    }
    
    // *************************************************************************
    
    /**
     * This method calculate the render distance and prepare the chunks within
     */
    private void processChunks() {
        
        final float x = 1;
        final float z = 1;
        
        final int chunk_x = (int) (x / 16);
        final int chunk_z = (int) (z / 16);

        // VISIBLE CHUNKS
        final int chunk_x_min = (chunk_x + ChunkConstants.RENDER_RADIUS) * -1;
        final int chunk_z_min = (chunk_z + ChunkConstants.RENDER_RADIUS) * -1;
        final int chunk_x_max = (chunk_x - ChunkConstants.RENDER_RADIUS) * -1;
        final int chunk_z_max = (chunk_z - ChunkConstants.RENDER_RADIUS) * -1;
        
        chunks_to_remove.clear();
        chunks_to_remove.addAll(chunk_buffer_copy);

        // UPDATE SECONDARY CHUNK BUFFER
        for (int cx = chunk_x_min; cx <= chunk_x_max - 1; cx++) {
            for (int cz = chunk_z_min; cz <= chunk_z_max - 1; cz++) {

                final String key = cx + " . " + cz;
                
                if (chunks_to_remove.contains(key)) {// ALSO EXIST IN BUFFER COPY
                    chunks_to_remove.remove(key);
                    
                } else {// CREATE NEW CHUNKS
                    final Chunk chunk = new Chunk();
                    chunkNoiseGenerator.generateNoise(cx, cz, chunk);
                    determineVisibleChunkData(chunk);
                    
                    chunk_buffer_copy.add(key);
                    handler.addToCreateBuffer(key, chunk);
                }
            }
        }
        
        if (!chunks_to_remove.isEmpty()) {// REMOVE OLD CHUNKS
            for (String key : chunks_to_remove) {
                handler.addToDisposeBuffer(key);
            }
            
            chunk_buffer_copy.removeAll(chunks_to_remove);
            chunks_to_remove.clear();
        }
    }
    
    /*
    TODO: SEPARATE INTO CHUNK SECTIONS
    */
    private void determineVisibleChunkData(final Chunk chunk) {
        
        for (short y = 0; y <= chunk.getUppermostBlockY(); y++) {
            for (byte x = 0; x < 16; x++) {
                for (byte z = 0; z < 16; z++) {
                
                    final byte id = chunk.getDataAt(x, y, z, false);
                    if (id == 0) {
                        continue;
                    }
                    
                    // BOTTOM LAYER
                    if (y == 0 &&
                        (chunk.getDataAt((byte) (x - 1), y, z, false) == 0 ||
                        chunk.getDataAt((byte) (x + 1), y, z, false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z - 1), false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z + 1), false) == 0)) {
                        
                        chunk.setDataId(x, y, z, id, true);
                        continue;
                    }
                    
                    // TOP LAYER
                    if (y == ChunkConstants.CHUNK_HEIGHT - 1 &&
                        (chunk.getDataAt((byte) (x - 1), y, z, false) == 0 ||
                        chunk.getDataAt((byte) (x + 1), y, z, false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z - 1), false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z + 1), false) == 0)) {
                        
                        chunk.setDataId(x, y, z, id, true);
                        continue;
                    }
                    
                    // OTHER LAYERS
                    if (y > 0 &&
                        y < ChunkConstants.CHUNK_HEIGHT - 1 &&
                        (chunk.getDataAt((byte) (x - 1), y, z, false) == 0 ||
                        chunk.getDataAt((byte) (x + 1), y, z, false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z - 1), false) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z + 1), false) == 0 ||
                        chunk.getDataAt(x, (short) (y - 1), z, false) == 0 ||
                        chunk.getDataAt(x, (short) (y + 1), z, false) == 0)) {
                        
                        chunk.setDataId(x, y, z, id, true);
                    }
                }
            }
        }
    }
}
