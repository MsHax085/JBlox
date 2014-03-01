
package jblox.chunks;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-jan-31
 * @version 1.0
 */
public class Chunk {
    
    private final int[] vboHandles = new int[16];
    private byte lastVboHandleIndex = 0;
    
    private short uppermostBlockY = 0;
    
    // Generated noise-data + loaded & modified data
    private final byte[] chunkData = new byte[16 * 16 * ChunkConstants.HEIGHT];// EXISTING BLOCKS
    private final byte[] visibleChunkData = new byte[16 * 16 * ChunkConstants.HEIGHT];// BLOCKS TO BE RENDERED
    
    // -------------------------------------------------------------------------
    
    public byte getDataAt(final byte x, final short y, final byte z) {
        return chunkData[ChunkConstants.coordsToIndex(x, y, z)];
    }
    
    public byte getVisibleDataAt(final byte x, final short y, final byte z) {
        try {
        return visibleChunkData[ChunkConstants.coordsToIndex(x, y, z)];
        } catch (Exception e) {
            System.out.println(x + ", " + y + ", " + z);
        }
        
        return visibleChunkData[ChunkConstants.coordsToIndex(x, y, z)];
    }
    
    public int[] getVboHandles() {
        return vboHandles;
    }
    
    public byte getLastVboHandleIndex() {
        return lastVboHandleIndex;
    }
    
    public short getUppermostBlockY() {
        return uppermostBlockY;
    }
    
    public void setDataId(final byte x, final short y, final byte z, final byte id) {
        chunkData[ChunkConstants.coordsToIndex(x, y, z)] = id;
    }
    
    public void setVisibleDataId(final byte x, final short y, final byte z, final byte id) {
        visibleChunkData[ChunkConstants.coordsToIndex(x, y, z)] = id;
    }
    
    public void setVboHandle(final byte index, final int id) {
        vboHandles[index] = id;
    }
    
    public void setLastVboHandleIndex(final byte index) {
        lastVboHandleIndex = index;
    }
    
    public void setUppermostBlockY(final short y) {
        uppermostBlockY = y;
    }
}
