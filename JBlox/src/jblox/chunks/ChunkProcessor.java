package jblox.chunks;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-23
 * @version 1.0
 */
public class ChunkProcessor implements Runnable {

    private final ChunkHandler handler;
    
    private final ArrayList<String> chunks_to_be_created = new ArrayList<>();
    private final TreeMap<String, Chunk> chunks_to_be_destroyed = new TreeMap<>();
    
    public ChunkProcessor(final ChunkHandler handler) {
        this.handler = handler;
    }
    
    @Override
    public void run() {
        
        while (true) {// MAYBE? MAYBE NOT? :S
            
        }
    }
    
    private void processChunks() {
        
    }
}
