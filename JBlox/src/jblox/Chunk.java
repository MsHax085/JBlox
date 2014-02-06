
package jblox;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

/**
 *
 * @author Richard
 * @since 2014-jan-31
 * @version 1.0
 */
public class Chunk {
    
    private final TextureProcessor textures = new TextureProcessor();

    private final short HEIGHT = 16;// Chunk height
    private short highestBlockY = 0;
    
    // Mini-chunks
    private final byte vertexDataLength = 8;
    private final int vboBufferLength = (vertexDataLength * 4 * 6) * 16 * 16 * 16;// Data * Vertices * Faces * X_Length * Y_Length * Z_Length
    private final int[] vboHandles = new int[16];
    
    // Generated noise-data + loaded & modified data
    private final byte[] chunkData = new byte[16 * 16 * HEIGHT];
    
    // -------------------------------------------------------------------------
    
    public void updateChunk(final byte x, final short y, final byte z, final byte id) {// Coords in chunk
        
        chunkData[coordsToIndex(x, y, z)] = id;
        
        /*
         *  TODO: Modify VBO   
        */
    }
    
    public void renderChunk() {
        
        for (int handle : vboHandles) {
            
            if (!(handle > 0)) {
                break;
            }
            
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.getTexture("STONE").getTextureID());
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);
            
            GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);// 32 = Bytes per vertex
            GL11.glNormalPointer(GL11.GL_FLOAT, 32, 12);
            GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 24);
            
            GL11.glDrawArrays(GL11.GL_QUADS, 0, vboBufferLength / vertexDataLength);
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    
    public void genereateChunk() {
        
        generateNoise();
        generateVBOs();
        
    }
    
    private void generateNoise() {
        for (short y = 0; y < HEIGHT; y++) {
            for (byte x = 0; x < 16; x++) {
                for (byte z = 0; z < 16; z++) {
                    
                    chunkData[coordsToIndex(x, y, z)] = 1;
                    highestBlockY = y;
                    
                }
            }
        }
    }
    
    private void generateVBOs() {
        
        generateVBOHandles();
        
        for (int handle : vboHandles) {
            
            if (!(handle > 0)) {
                break;
            }
            
            final FloatBuffer vboBuffer = BufferUtils.createFloatBuffer(vboBufferLength);
            
            for (byte y = 0; y < 16; y++) {
                for (byte x = 0; x < 16; x++) {
                    for (byte z = 0; z < 16; z++) {
                        
                        if (chunkData[coordsToIndex(x, y, z)] > 0) {
                            vboBuffer.put(generateQuad(x, y, z));
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
    
    private void generateVBOHandles() {
        
        final int vboChunks = (int) Math.ceil(highestBlockY / 16.0);
        final IntBuffer buffer = BufferUtils.createIntBuffer(vboChunks);
        
        GL15.glGenBuffers(buffer);
        
        for (byte intHandle = 0; intHandle < vboChunks; intHandle++) {
            vboHandles[intHandle] = buffer.get(intHandle);
        }
    }
    
    private FloatBuffer generateQuad(final byte x, final byte y, final byte z) {
        
        //  Temp Variables
        final boolean generateFrontQuad = true;
        final boolean generateBackQuad = true;
        final boolean generateLeftQuad = true;
        final boolean generateRightQuad = true;
        final boolean generateTopQuad = true;
        final boolean generateBottomQuad = true;
        
        short quadBufferLength = 0;
        {
            quadBufferLength += (generateFrontQuad)   ? 32 : 0;// 32 = Bytes per vertex
            quadBufferLength += (generateBackQuad)  ? 32 : 0;
            quadBufferLength += (generateLeftQuad)   ? 32 : 0;
            quadBufferLength += (generateRightQuad)  ? 32 : 0;
            quadBufferLength += (generateTopQuad)    ? 32 : 0;
            quadBufferLength += (generateBottomQuad) ? 32 : 0;
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
         width + x,  width + y,  width + z,  0.0f,  0.55f,  1.0f,  1.0f,  0.0f,
        -width + x,  width + y,  width + z,  0.0f,  0.55f,  1.0f,  0.0f,  0.0f,
        -width + x, -width + y,  width + z,  0.0f,  0.55f,  1.0f,  0.0f,  1.0f,
         width + x, -width + y,  width + z,  0.0f,  0.55f,  1.0f,  1.0f,  1.0f};
        
        return backQuad;
    }
    
    private float[] generateBackQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] frontQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
         width + x,  width + y, -width + z,  0.0f,  0.55f, -1.0f,  0.0f,  0.0f,
        -width + x,  width + y, -width + z,  0.0f,  0.55f, -1.0f,  1.0f,  0.0f,
        -width + x, -width + y, -width + z,  0.0f,  0.55f, -1.0f,  1.0f,  1.0f,
         width + x, -width + y, -width + z,  0.0f,  0.55f, -1.0f,  0.0f,  1.0f};

        return frontQuad;
    }
    
    private float[] generateLeftQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] leftQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        -width + x,  width + y, -width + z, -1.0f,  0.55f,  0.0f,  0.0f,  0.0f,
        -width + x,  width + y,  width + z, -1.0f,  0.55f,  0.0f,  1.0f,  0.0f,
        -width + x, -width + y,  width + z, -1.0f,  0.55f,  0.0f,  1.0f,  1.0f,
        -width + x, -width + y, -width + z, -1.0f,  0.55f,  0.0f,  0.0f,  1.0f};

        return leftQuad;
    }
    
    private float[] generateRightQuad(final byte x, final byte y, final byte z, final float width) {
        
        final float[] rightQuad = {
        //      x          y           z    nx      ny     nz   tx     ty
        width + x,  width + y, -width + z,  1.0f,  0.55f,  0.0f,  1.0f,  0.0f,
        width + x,  width + y,  width + z,  1.0f,  0.55f,  0.0f,  0.0f,  0.0f,
        width + x, -width + y,  width + z,  1.0f,  0.55f,  0.0f,  0.0f,  1.0f,
        width + x, -width + y, -width + z,  1.0f,  0.55f,  0.0f,  1.0f,  1.0f};

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
    
    private int coordsToIndex(final byte x, final short y, final byte z) {// Coords in chunk
        return (x * 16 + z) * HEIGHT + y;
    }
    
    /*
        Remove VBO's from graphics card
    */
    public void clear() {
        
        for (int handle : vboHandles) {
            
            if (!(handle > 0)) {
                break;
            }
            
            GL15.glDeleteBuffers(handle);
        }
    }
}
