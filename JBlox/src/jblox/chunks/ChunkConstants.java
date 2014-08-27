package jblox.chunks;

/**
 *
 * @author Richard Dahlgren
 */
public class ChunkConstants {
    
    public static final short CHUNK_HEIGHT = 256;
    public static final short CHUNK_SECTION_HEIGHT = 16;
    public static final byte RENDER_RADIUS = 6;

    public static final byte VERTEX_DATA_LENGTH = 4;
    public static final byte BYTES_PER_VERTEX = 32;
    public static final int VBO_BUFFER_LENGTH = (VERTEX_DATA_LENGTH * 4 * 6) * 16 * 16 * 16;// (Data * Vertices * Faces) * X_Length * Y_Length * Z_Length
    
    public static final float TEXTURE_COLS = 16;
    public static final float TEXTURE_ROWS = 1;
    public static final byte TEXTURE_SIZE = 32;
    
    public final static int coordsToIndex(final byte x, final int y, final byte z) {// Coords in chunk
        return (x * 16 + z) * CHUNK_HEIGHT + y;
    }
    
    public final static int coordsToSectionIndex(final byte x, final int y, final byte z) {// Coords in chunk section
        return (x * 16 + z) * CHUNK_SECTION_HEIGHT + y;
    }
}
