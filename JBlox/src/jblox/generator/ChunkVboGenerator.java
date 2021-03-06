package jblox.generator;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import jblox.chunks.Chunk;
import jblox.chunks.ChunkConstants;
import jblox.chunks.ChunkSection;
import jblox.chunks.TextureProcessor;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-08
 * @version 1.0
 */
public class ChunkVboGenerator {
    
    private final TextureProcessor textureProcessor;
    
    public ChunkVboGenerator(final TextureProcessor textureProcessor) {
        this.textureProcessor = textureProcessor;
    }
    
    public void generateVBOHandles(final Chunk chunk) {
        
        final HashMap<Byte, ChunkSection> entry = chunk.getChunkSections();
        final IntBuffer buffer = BufferUtils.createIntBuffer(entry.size());
        
        GL15.glGenBuffers(buffer);
        
        byte index = 0;
        for (ChunkSection section : entry.values()) {
            section.setVboHandle(buffer.get(index));
            index++;
        }
    }
    
    public void generateVBOs(final Chunk chunk) {
        
        for (ChunkSection section : chunk.getChunkSections().values()) {
            
            final FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(ChunkConstants.VBO_BUFFER_LENGTH);
            
            for (byte[] visibleData : section.getVisibleSectionData()) {
                
                final byte x    = visibleData[0];
                final byte y    = visibleData[1];
                final byte z    = visibleData[2];
                final byte id   = visibleData[3];
                
                vboBuffer.put(generateQuad(x, y, z, id));
                
            }
            
            vboBuffer.rewind();
            
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, section.getVboHandle());// Bind new buffer
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vboBuffer, GL15.GL_DYNAMIC_DRAW);// Upload buffer data
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);// Unbind buffer
            
        }
    }
    
    /*
     *  Quad Generation
    */// -------------------------------------------------------------------------
    
    private FloatBuffer generateQuad(final byte x, final byte y, final byte z, final byte id) {// Coords in Chunk
        
        short quadBufferLength = ChunkConstants.BYTES_PER_VERTEX * 6;
        final float WIDTH = 0.5f;// 0.5f
        
        final FloatBuffer quadBuffer = BufferUtils.createFloatBuffer(quadBufferLength);
        {
            quadBuffer.put(generateFrontQuad(x, y, z, WIDTH, id));
            quadBuffer.put(generateBackQuad(x, y, z, WIDTH, id));
            quadBuffer.put(generateLeftQuad(x, y, z, WIDTH, id));
            quadBuffer.put(generateRightQuad(x, y, z, WIDTH, id));
            quadBuffer.put(generateTopQuad(x, y, z, WIDTH, id));
            quadBuffer.put(generateBottomQuad(x, y, z, WIDTH, id));

            quadBuffer.rewind();
        }
        
        return quadBuffer;
    }
    
    private float[] generateFrontQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, false, false);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, false, false);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] backQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
         width + x,  width + y,  width + z,  0.0f,  0.4f,  0.4f,  tx2,  ty1,
        -width + x,  width + y,  width + z,  0.0f,  0.4f,  0.4f,  tx1,  ty1,
        -width + x, -width + y,  width + z,  0.0f,  0.4f,  0.4f,  tx1,  ty2,
         width + x, -width + y,  width + z,  0.0f,  0.4f,  0.4f,  tx2,  ty2};
        
        return backQuad;
    }
    
    private float[] generateBackQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, false, false);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, false, false);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] frontQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
         width + x,  width + y, -width + z,  0.0f,  0.4f, -0.4f,  tx1,  ty1,
        -width + x,  width + y, -width + z,  0.0f,  0.4f, -0.4f,  tx2,  ty1,
        -width + x, -width + y, -width + z,  0.0f,  0.4f, -0.4f,  tx2,  ty2,
         width + x, -width + y, -width + z,  0.0f,  0.4f, -0.4f,  tx1,  ty2};

        return frontQuad;
    }
    
    private float[] generateLeftQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, false, false);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, false, false);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] leftQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x,  width + y, -width + z, -0.4f,  0.4f,  0.0f,  tx1,  ty1,
        -width + x,  width + y,  width + z, -0.4f,  0.4f,  0.0f,  tx2,  ty1,
        -width + x, -width + y,  width + z, -0.4f,  0.4f,  0.0f,  tx2,  ty2,
        -width + x, -width + y, -width + z, -0.4f,  0.4f,  0.0f,  tx1,  ty2};

        return leftQuad;
    }
    
    private float[] generateRightQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, false, false);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, false, false);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] rightQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        width + x,  width + y, -width + z,  0.4f,  0.4f,  0.0f,  tx2,  ty1,
        width + x,  width + y,  width + z,  0.4f,  0.4f,  0.0f,  tx1,  ty1,
        width + x, -width + y,  width + z,  0.4f,  0.4f,  0.0f,  tx1,  ty2,
        width + x, -width + y, -width + z,  0.4f,  0.4f,  0.0f,  tx2,  ty2};

        return rightQuad;
    }
    
    private float[] generateTopQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, true, false);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, true, false);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] topQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x,  width + y, -width + z,  0.0f,  0.8f,  0.0f,  tx1,  ty1,
        -width + x,  width + y,  width + z,  0.0f,  0.8f,  0.0f,  tx1,  ty2,
         width + x,  width + y,  width + z,  0.0f,  0.8f,  0.0f,  tx2,  ty2,
         width + x,  width + y, -width + z,  0.0f,  0.8f,  0.0f,  tx2,  ty1};

        return topQuad;
    }
    
    private float[] generateBottomQuad(byte x, byte y, byte z, float width, byte id) {
        
        final float tx1 = textureProcessor.getTextureX1(id, false, true);
        final float tx2 = tx1 + (1 / ChunkConstants.TEXTURE_COLS);
        
        final float ty1 = textureProcessor.getTextureY1(id, false, true);
        final float ty2 = ty1 + (1 / ChunkConstants.TEXTURE_ROWS);
        
        final float[] bottomQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x, -width + y, -width + z,  0.0f, 0.2f,  0.0f,  tx1,  ty1,
        -width + x, -width + y,  width + z,  0.0f, 0.2f,  0.0f,  tx1,  ty2,
         width + x, -width + y,  width + z,  0.0f, 0.2f,  0.0f,  tx2,  ty2,
         width + x, -width + y, -width + z,  0.0f, 0.2f,  0.0f,  tx2,  ty1};

        return bottomQuad;
    }
}
