package jblox.chunks;

/**
 *
 * @author Richard Dahlgren
 */
public class ChunkConstants {
    
    public static final short HEIGHT = 256;// Chunk Height
    public static final byte RENDER_RADIUS = 4;

    public static final byte VERTEX_DATA_LENGTH = 8;
    public static final byte BYTES_PER_VERTEX = 32;
    public static final int VBO_BUFFER_LENGTH = (VERTEX_DATA_LENGTH * 4 * 6) * 16 * 16 * 16;// (Data * Vertices * Faces) * X_Length * Y_Length * Z_Length
    
    public static final float TEXTURE_COLS = 16;
    public static final float TEXTURE_ROWS = 1;
    public static final byte TEXTURE_SIZE = 32;
    
    public final static int coordsToIndex(final byte x, final short y, final byte z) {// Coords in chunk
        return (x * 16 + z) * HEIGHT + y;
    }
}
