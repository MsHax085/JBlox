
package jblox.generator;

import jblox.chunks.Chunk;

/**
 *
 * @author Richard
 * @since 2014-feb-07
 * @version 1.0
 */
public class Point2D {

    public final Chunk chunkReference = new Chunk();
    
    public final int x;
    public final int z;
    
    public Point2D(final int x, final int z) {
        this.x = x;
        this.z = z;
    }
}
