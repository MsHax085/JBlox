package jblox.chunks;

import java.util.ArrayList;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-mar-07
 * @version 1.0
 */
public class ChunkSection {
    
    private int vboHandle;
    
    private final byte[] sectionData = new byte[16 * 16 * 16];
    private final ArrayList<byte[]> visibleSectionData = new ArrayList<>();// int[x,y,z,id]
    
    // -------------------------------------------------------------------------
    
    public int getVboHandle() {
        return vboHandle;
    }
    
    public byte getDataAt(final byte x, final byte y, final byte z) {
        return sectionData[ChunkConstants.coordsToSectionIndex(x, y, z)];
    }
    
    public ArrayList<byte[]> getVisibleSectionData() {
        return visibleSectionData;
    }
    
    public void addVisibleData(byte x, byte y, byte z, byte id) {
        final byte[] array = new byte[]{x, y, z, id};
        visibleSectionData.add(array);
    }
    
    public void setVboHandle(final int handle) {
        vboHandle = handle;
    }
    
    public void setDataId(final byte x, final byte y, final byte z, final byte id) {
        sectionData[ChunkConstants.coordsToSectionIndex(x, y, z)] = id;
    }
}
