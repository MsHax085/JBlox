
package jblox.chunks;

import java.util.HashMap;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-jan-31
 * @version 1.0
 */
public class Chunk {
    
    private final HashMap<Byte, ChunkSection> sections = new HashMap<>();
    
    private short uppermostBlockY = 0;
    
    // -------------------------------------------------------------------------
    
    /**
     * This method returns the index (order/layer) of a chunk selection
     * @param y The global Y-coordinate
     * @return Returns the index of the chunk selection
     */
    private byte getSectionIndex(final short y) {
        return (byte) Math.floor(y / 16);
    }
    
    /**
     * This method returns the Y-coordinate in a chunk selection
     * @param y The global Y-coordinate
     * @param order In which layer the chunk selection is located
     * @return Returns the Y-coordinate inside the chunk selection
     */
    private byte getSectionY(final short y, final byte order) {
        return (byte) (y - (order * 16));
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * This method returns a block ID from the chunk
     * @param x The X-value inside the chunk
     * @param y The Y-value inside the chunk
     * @param z The Z-value inside the chunk
     * @param visible Determines whether to look in all data or only the visible
     * @return Returns the found block ID
     * 
     * TODO: REMOVE, INEFFICIENT
     */
    public byte getDataAt(final byte x, final short y, final byte z, final boolean visible) {
        
        if (x < 0 || x > 15 ||
            z < 0 || z > 15 ||
            y < 0 || y >= ChunkConstants.CHUNK_HEIGHT) {
            
            return 0;
        }
        
        final byte sectionIndex = getSectionIndex(y);
        final byte y_in_section = getSectionY(y, sectionIndex);
        
        if (!sections.containsKey(sectionIndex)) {
            return 0;
        }
        
        if (visible) {
            return sections.get(sectionIndex).getVisibleDataAt(x, y_in_section, z);
        } else {
            return sections.get(sectionIndex).getDataAt(x, y_in_section, z);
        }
    }
    
    public void setDataId(final int x, final int y, final int z, final int id, final boolean visible) {
        setDataId((byte) x, (short) y, (byte) z, (byte) id, visible);
    }
    
    /**
     * This method modifies the chunk
     * This method modifies the stored data of a chunk, all and the visible data
     * @param x The X-value inside the chunk
     * @param y The Y-value inside the chunk
     * @param z The Z-value inside the chunk
     * @param id The id for the new block
     * @param visible Determines whether to modify all data or only the visible data
     * 
     * TODO: REMOVE, INEFFICIENT
     */
    public void setDataId(final byte x, final short y, final byte z, final byte id, final boolean visible) {
        
        final byte sectionIndex = getSectionIndex(y);
        final byte y_in_section = getSectionY(y, sectionIndex);
        
        if (!sections.containsKey(sectionIndex)) {
            sections.put(sectionIndex, new ChunkSection());
        }
        
        if (visible) {
            sections.get(sectionIndex).setVisibleDataId(x, y_in_section, z, id);
        } else {
            sections.get(sectionIndex).setDataId(x, y_in_section, z, id);
        }
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * This method returns the chunk sections
     * This method returns the hashmap, where the chunk sections are stored
     * @return HashMap of chunk sections
     */
    public HashMap<Byte, ChunkSection> getChunkSections() {
        return sections;
    }
    
    // -------------------------------------------------------------------------
    
    /**
     * This method returns the largest Y-value
     * This method is used to determine the visible blocks in a chunk
     * @return The largest Y-value in this chunk
     */
    public short getUppermostBlockY() {
        return uppermostBlockY;
    }
    
    /**
     * This method set the largest Y-Value
     * This method sets the value used to determine visible blocks in a chunk
     * @param y The new Y-value
     */
    public void setUppermostBlockY(final short y) {
        uppermostBlockY = y;
    }
}
