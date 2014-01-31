
package jblox;

/**
 *
 * @author Richard
 * @since 2014-jan-31
 * @version 1.0
 */
public class MiniChunk {

    private final byte[] blocks = new byte[16 * 16 * 16];
    private int VBOHandle;
    
    public int getVBOHandle() {
        return VBOHandle;
    }
    
    public byte getBlockId(final byte x, final byte y, final byte z) {
        return blocks[coordsToIndex(x, y, z)];
    }
    
    public void setBlockId(final byte x, final byte y, final byte z, final byte id) {
        blocks[coordsToIndex(x, y, z)] = id;
        updateVBO(x, y, z);
    }
    
    /*
     *  Create VBO, only on startup
    */
    public void createVBO() {
        // Implement
    }
    
    /*
     *  Update part of VBO upon block change,
     *  Only accessible through setBlockId function
    */
    private void updateVBO(final byte x, final byte y, final byte z) {
        // Implement
    }
    
    /* 
     *  Convert minichunk coordinate to byte-array index
    */
    private int coordsToIndex(final byte x, final byte y, final byte z) {
        final byte HEIGHT = 16;
        return (x * 16 * z) * HEIGHT + y;
    }
}
