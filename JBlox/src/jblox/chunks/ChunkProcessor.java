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
        
        final int chunk_x = (int) (client.getX() / 16);
        final int chunk_z = (int) (client.getZ() / 16);

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
                    //determineVisibleChunkData(chunk);
                    
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
    
    /*private void determineVisibleChunkData(final Chunk chunk) {
        
        for (int y = chunk.getLowermostBlockY(); y <= chunk.getUppermostBlockY(); y++) {
            for (byte x = 0; x < 16; x++) {
                for (byte z = 0; z < 16; z++) {
                
                    final byte id = chunk.getDataAt(x, y, z);
                    if (id == 0) {
                        continue;
                    }
                    
                    if (y - 1 < 0 ||
                        y + 1 >= ChunkConstants.HEIGHT ||
                        chunk.getDataAt(x, y + 1, z) == 0) {
                        
                        chunk.addVisibleDataId(new int[]{x,y,z});
                        continue;
                    }
                    
                    if (x - 1 < 0 ||
                        x + 1 > 15 ||
                        chunk.getDataAt((byte) (x - 1), y, z) == 0 ||
                        chunk.getDataAt((byte) (x + 1), y, z) == 0) {
                        
                        chunk.addVisibleDataId(new int[]{x,y,z});
                        continue;
                    }
                    
                    if (z - 1 < 0 ||
                        z + 1 > 15 ||
                        chunk.getDataAt(x, y, (byte) (z - 1)) == 0 ||
                        chunk.getDataAt(x, y, (byte) (z + 1)) == 0) {
                        
                        chunk.addVisibleDataId(new int[]{x,y,z,id});
                    }
                }
            }
        }
    }*/
}
