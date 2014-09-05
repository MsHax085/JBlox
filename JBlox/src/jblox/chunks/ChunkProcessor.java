package jblox.chunks;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jblox.Entry;
import static jblox.chunks.ChunkConstants.RENDER_RADIUS;
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
        
        final double yaw = Math.toRadians(client.getYaw() - 90);
        
        final float x = (client.getX() / 16);// CHUNK COORDINATES
        final float z = (client.getZ() / 16);

        final int x_far_center = (int) ((RENDER_RADIUS * Math.cos(yaw)) - x);// Chunk far left on screen
        final int z_far_center = (int) ((RENDER_RADIUS * Math.sin(yaw)) - z);
        
        final int x_far_left = (int) ((RENDER_RADIUS * Math.cos(yaw + 45)) - x);// Chunk far left on screen
        final int z_far_left = (int) ((RENDER_RADIUS * Math.sin(yaw + 45)) - z);
        
        final int x_far_right = (int) ((RENDER_RADIUS * Math.cos(yaw - 45)) - x);// Chunk far right on screen
        final int z_far_right = (int) ((RENDER_RADIUS * Math.sin(yaw - 45)) - z);
        
        final int x_behind = (int) ((Math.cos(yaw - 180) * (RENDER_RADIUS / 2)) - x);// Chunk near (behind) on screen
        final int z_behind = (int) ((Math.sin(yaw - 180) * (RENDER_RADIUS / 2)) - z);
        
        final int max_x_1 = (x_far_left < x_far_right) ? x_far_right : x_far_left;
        final int max_x_2 = (max_x_1 < x_behind) ? x_behind : max_x_1;
        final int max_x_3 = (max_x_2 < x_far_center) ? x_far_center : max_x_2;
        
        final int max_z_1 = (z_far_left < z_far_right) ? z_far_right : z_far_left;
        final int max_z_2 = (max_z_1 < z_behind) ? z_behind : max_z_1;
        final int max_z_3 = (max_z_2 < z_far_center) ? z_far_center : max_z_2;
        
        final int min_x_1 = (x_far_left > x_far_right) ? x_far_right : x_far_left;
        final int min_x_2 = (min_x_1 > x_behind) ? x_behind : min_x_1;
        final int min_x_3 = (min_x_2 > x_far_center) ? x_far_center : min_x_2;
        
        final int min_z_1 = (z_far_left > z_far_right) ? z_far_right : z_far_left;
        final int min_z_2 = (min_z_1 > z_behind) ? z_behind : min_z_1;
        final int min_z_3 = (min_z_2 > z_far_center) ? z_far_center : min_z_2;
        
        chunks_to_remove.clear();
        chunks_to_remove.addAll(chunk_buffer_copy);
        
        // UPDATE SECONDARY CHUNK BUFFER
        for (int cx = min_x_3; cx < max_x_3; cx++) {
            
            for (int cz = min_z_3; cz < max_z_3; cz++) {
                
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
