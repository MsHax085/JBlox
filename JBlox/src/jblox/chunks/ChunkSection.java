package jblox.chunks;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-mar-07
 * @version 1.0
 */
public class ChunkSection {
    
    private int vboHandle;
    
    private final byte[] sectionData = new byte[16 * 16 * 16];
    private final byte[] visibleSectionData = new byte[16 * 16 * 16];
    
    // -------------------------------------------------------------------------
    
    public int getVboHandle() {
        return vboHandle;
    }
    
    public byte getDataAt(final byte x, final byte y, final byte z) {
        return sectionData[ChunkConstants.coordsToSectionIndex(x, y, z)];
    }
    
    public byte getVisibleDataAt(final byte x, final byte y, final byte z) {
        return visibleSectionData[ChunkConstants.coordsToSectionIndex(x, y, z)];
    }
    
    public void setVboHandle(final int handle) {
        vboHandle = handle;
    }
    
    public void setDataId(final byte x, final byte y, final byte z, final byte id) {
        sectionData[ChunkConstants.coordsToSectionIndex(x, y, z)] = id;
    }
    
    public void setVisibleDataId(final byte x, final byte y, final byte z, final byte id) {
        visibleSectionData[ChunkConstants.coordsToSectionIndex(x, y, z)] = id;
    }
}
