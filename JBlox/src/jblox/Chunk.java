
package jblox;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-jan-31
 * @version 1.0
 */
public class Chunk {
    
    private final int[] vboHandles = new int[16];
    private int primaryVboHandle = 0;
    
    private short uppermostBlockY = 0;
    
    // Generated noise-data + loaded & modified data
    private final byte[] chunkData = new byte[16 * 16 * ChunkConstants.HEIGHT];
    
    // -------------------------------------------------------------------------
    
    public byte getDataAt(final int index) {
        return chunkData[index];
    }
    
    public int[] getVboHandles() {
        return vboHandles;
    }
    
    public int getPrimaryVboHandle() {
        return primaryVboHandle;
    }
    
    public short getUppermostBlockY() {
        return uppermostBlockY;
    }
    
    public void setDataId(final int index, final byte id) {
        chunkData[index] = id;
    }
    
    public void setVboHandle(final byte index, final int id) {
        vboHandles[index] = id;
    }
    
    public void setPrimaryVboHandle(final int id) {
        primaryVboHandle = id;
    }
    
    public void setUppermostBlockY(final short y) {
        uppermostBlockY = y;
    }
}
