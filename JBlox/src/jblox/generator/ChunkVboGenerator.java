package jblox.generator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import jblox.Chunk;
import jblox.ChunkConstants;
import jblox.generator.noise.SimplexOctaveGenerator;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-08
 * @version 1.0
 */
public class ChunkVboGenerator {
    
    public void generateVBOHandles(final Chunk chunk) {
        
        final int vboChunks = (int) Math.ceil(chunk.getUppermostBlockY() / 16.0);
        final IntBuffer buffer = BufferUtils.createIntBuffer(vboChunks);
        
        GL15.glGenBuffers(buffer);
        
        chunk.setPrimaryVboHandle(buffer.get(0));
        
        for (byte intHandle = 0; intHandle < vboChunks; intHandle++) {
            chunk.setVboHandle(intHandle, buffer.get(intHandle));
        }
    }
    
    public void generateVBOs(final Chunk chunk) {
        
        final int primaryVboHandle = chunk.getPrimaryVboHandle();
        
        for (int handle : chunk.getVboHandles()) {
            
            if (!(handle > 0)) {
                break;
            }
            
            final FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(ChunkConstants.VBO_BUFFER_LENGTH);
            
            for (byte y1 = 0; y1 < 16; y1++) {
                for (byte x = 0; x < 16; x++) {
                    for (byte z = 0; z < 16; z++) {
                        
                        final short y2 = (short) (((handle - primaryVboHandle) * 16) + y1);
                        
                        if (chunk.getDataAt(ChunkConstants.coordsToIndex(x, y2, z)) > 0) {
                            vboBuffer.put(generateQuad(x, y1, z));
                        }
                    }
                }
            }
            
            vboBuffer.rewind();
            
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);// Bind new buffer
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vboBuffer, GL15.GL_DYNAMIC_DRAW);// Upload buffer data
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// Unbind buffer
            
        }
    }
    
    /*
     *  Quad Generation
    */// -------------------------------------------------------------------------
    
    private FloatBuffer generateQuad(final byte x, final byte y, final byte z) {// Coords in Chunk
        
        //  Temp Variables
        final boolean generateFrontQuad = true;
        final boolean generateBackQuad = true;
        final boolean generateLeftQuad = true;
        final boolean generateRightQuad = true;
        final boolean generateTopQuad = true;
        final boolean generateBottomQuad = true;
        
        final byte bpv = ChunkConstants.BYTES_PER_VERTEX;
        
        short quadBufferLength = 0;
        {
            quadBufferLength += (generateFrontQuad)     ? bpv : 0;// 32 = Bytes per vertex
            quadBufferLength += (generateBackQuad)      ? bpv : 0;
            quadBufferLength += (generateLeftQuad)      ? bpv : 0;
            quadBufferLength += (generateRightQuad)     ? bpv : 0;
            quadBufferLength += (generateTopQuad)       ? bpv : 0;
            quadBufferLength += (generateBottomQuad)    ? bpv : 0;
        }
        
        final FloatBuffer quadBuffer = BufferUtils.createFloatBuffer(quadBufferLength);
        final float WIDTH = 0.5f;// 0.5f
        
        if (generateFrontQuad) {
            quadBuffer.put(generateFrontQuad(x, y, z, WIDTH));
        }
        
        if (generateBackQuad) {
            quadBuffer.put(generateBackQuad(x, y, z, WIDTH));
        }
        
        if (generateLeftQuad) {
            quadBuffer.put(generateLeftQuad(x, y, z, WIDTH));
        }
        
        if (generateRightQuad) {
            quadBuffer.put(generateRightQuad(x, y, z, WIDTH));
        }
        
        if (generateTopQuad) {
            quadBuffer.put(generateTopQuad(x, y, z, WIDTH));
        }
        
        if (generateBottomQuad) {
            quadBuffer.put(generateBottomQuad(x, y, z, WIDTH));
        }
        
        quadBuffer.rewind();
        
        return quadBuffer;
    }
    
    private float[] generateFrontQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] backQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
         width + x,  width + y,  width + z,  0.0f,  0.45f,  1.0f,  1.0f,  0.0f,
        -width + x,  width + y,  width + z,  0.0f,  0.45f,  1.0f,  0.0f,  0.0f,
        -width + x, -width + y,  width + z,  0.0f,  0.45f,  1.0f,  0.0f,  1.0f,
         width + x, -width + y,  width + z,  0.0f,  0.45f,  1.0f,  1.0f,  1.0f};
        
        return backQuad;
    }
    
    private float[] generateBackQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] frontQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
         width + x,  width + y, -width + z,  0.0f,  0.45f, -1.0f,  0.0f,  0.0f,
        -width + x,  width + y, -width + z,  0.0f,  0.45f, -1.0f,  1.0f,  0.0f,
        -width + x, -width + y, -width + z,  0.0f,  0.45f, -1.0f,  1.0f,  1.0f,
         width + x, -width + y, -width + z,  0.0f,  0.45f, -1.0f,  0.0f,  1.0f};

        return frontQuad;
    }
    
    private float[] generateLeftQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] leftQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x,  width + y, -width + z, -1.0f,  0.45f,  0.0f,  0.0f,  0.0f,
        -width + x,  width + y,  width + z, -1.0f,  0.45f,  0.0f,  1.0f,  0.0f,
        -width + x, -width + y,  width + z, -1.0f,  0.45f,  0.0f,  1.0f,  1.0f,
        -width + x, -width + y, -width + z, -1.0f,  0.45f,  0.0f,  0.0f,  1.0f};

        return leftQuad;
    }
    
    private float[] generateRightQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] rightQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        width + x,  width + y, -width + z,  1.0f,  0.45f,  0.0f,  1.0f,  0.0f,
        width + x,  width + y,  width + z,  1.0f,  0.45f,  0.0f,  0.0f,  0.0f,
        width + x, -width + y,  width + z,  1.0f,  0.45f,  0.0f,  0.0f,  1.0f,
        width + x, -width + y, -width + z,  1.0f,  0.45f,  0.0f,  1.0f,  1.0f};

        return rightQuad;
    }
    
    private float[] generateTopQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] topQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x,  width + y, -width + z,  0.0f,  1.0f,  0.0f,  0.0f,  0.0f,
        -width + x,  width + y,  width + z,  0.0f,  1.0f,  0.0f,  0.0f,  1.0f,
         width + x,  width + y,  width + z,  0.0f,  1.0f,  0.0f,  1.0f,  1.0f,
         width + x,  width + y, -width + z,  0.0f,  1.0f,  0.0f,  1.0f,  0.0f};

        return topQuad;
    }
    
    private float[] generateBottomQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] bottomQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x, -width + y, -width + z,  0.0f, -1.0f,  0.0f,  0.0f,  0.0f,
        -width + x, -width + y,  width + z,  0.0f, -1.0f,  0.0f,  0.0f,  1.0f,
         width + x, -width + y,  width + z,  0.0f, -1.0f,  0.0f,  1.0f,  1.0f,
         width + x, -width + y, -width + z,  0.0f, -1.0f,  0.0f,  1.0f,  0.0f};

        return bottomQuad;
    }
}
