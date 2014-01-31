
package jblox;

import java.util.HashMap;

/**
 *
 * @author Richard
 * @since 2014-jan-31
 * @version 1.0
 */
public class Chunk {

    private final HashMap<Byte, MiniChunk> miniChunks = new HashMap<>();// Ascending order of minichunks, void -> sky
    
    public void createMiniChunks() {
        // TODO: Loop through minichunks
    }
}
