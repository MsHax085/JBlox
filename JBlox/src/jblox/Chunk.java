
package jblox;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;
import jblox.generator.noise.SimplexOctaveGenerator;
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

    private final int seed = new Random().nextInt(Integer.MAX_VALUE);
    private final short HEIGHT = 256;// Chunk height
    private short highestBlockY = 0;
    
    // Mini-chunks
    private final byte vertexDataLength = 8;
    private final byte bytesPerVertex = 32;
    private final int vboBufferLength = (vertexDataLength * 4 * 6) * 16 * 16 * 16;// (Data * Vertices * Faces) * X_Length * Y_Length * Z_Length
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
            
            GL11.glPushMatrix();
            GL11.glTranslatef(0, (handle - 1) * 16, 0);// Move mini-chunks along Y-Axis
            
            {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures.getTexture("STONE").getTextureID());
                GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, handle);

                GL11.glVertexPointer(3, GL11.GL_FLOAT, bytesPerVertex, 0);
                GL11.glNormalPointer(GL11.GL_FLOAT, bytesPerVertex, 12);
                GL11.glTexCoordPointer(2, GL11.GL_FLOAT, bytesPerVertex, 24);

                GL11.glDrawArrays(GL11.GL_QUADS, 0, vboBufferLength / vertexDataLength);
            }
            GL11.glPopMatrix();
        }
        
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }
    
    public void genereateChunk() {
        
        generateNoise(0, 0);
        generateVBOs();
        
    }
    
    private void generateNoise(final int cx, final int cz) {// Chunk coordinates

        //final byte[] blocks = new byte[16 * 16 * HEIGHT];
        
        SimplexOctaveGenerator ground = new SimplexOctaveGenerator(seed, 4);
        SimplexOctaveGenerator hills = new SimplexOctaveGenerator(seed, 6);
        SimplexOctaveGenerator mountains = new SimplexOctaveGenerator(seed, 4);
        SimplexOctaveGenerator particles = new SimplexOctaveGenerator(seed, 8);
        
        ground.setScale(1/96.0);
        hills.setScale(1/32.0);
        mountains.setScale(1/64.0);
        particles.setScale(1/16.0);
        
        int ground_elevation = 20;
        
        double ground_mag = 6;
        double hills_mag = 12;
        double mountains_mag = 16;

        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                int wx = x + cx * 16;
                int wz = z + cz * 16;
                
                double groundHeight = ground.noise(wx, wz, 0.5, 0.5) * ground_mag + ground_elevation;
                double hills_height = hills.noise(wx, wz, 0.8, 0.5) * hills_mag + ground_elevation;
                double mountains_height = mountains.noise(wx, wz, 1.5, 0.6) * mountains_mag + ground_elevation;
                double particles_noise;
                
                short y = 0;
                for (; y < groundHeight; y++) {
                    particles_noise = particles.noise(wx, y, wz, 0.4, 0.6);
                    if (particles_noise < 0.9 || y == 0) {
                        chunkData[coordsToIndex(x, y, z)] = 1;
                    } else {
                        //blocks[locationToByteIndex(x, y, z)] = 4;
                    }
                    
                }
                
                for (; y < hills_height; y++) {
                    particles_noise = particles.noise(wx, y, wz, 0.3, 0.5);
                    if (particles_noise < 0.9) {
                        chunkData[coordsToIndex(x, y, z)] = 1;
                        //blocks[locationToByteIndex(x, y, z)] = getBlockTypeByHeight(y, mountains_height);
                        // Determines block type by depth from top layer
                    }
                    
                }
                
                for (; y < mountains_height; y++) {
                    particles_noise = particles.noise(wx, y, wz, 0.3, 0.5);
                    if (particles_noise < 0.9) {
                        chunkData[coordsToIndex(x, y, z)] = 1;
                        //blocks[locationToByteIndex(x, y, z)] = getBlockTypeByHeight(y, mountains_height);
                        // Determines block type by depth from top layer
                    }
                    
                }
                
                if (y > highestBlockY) {
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
                        if (chunkData[coordsToIndex(x, (short) (y + ((handle - 1) * 16)), z)] > 0) {
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
            quadBufferLength += (generateFrontQuad)     ? bytesPerVertex : 0;// 32 = Bytes per vertex
            quadBufferLength += (generateBackQuad)      ? bytesPerVertex : 0;
            quadBufferLength += (generateLeftQuad)      ? bytesPerVertex : 0;
            quadBufferLength += (generateRightQuad)     ? bytesPerVertex : 0;
            quadBufferLength += (generateTopQuad)       ? bytesPerVertex : 0;
            quadBufferLength += (generateBottomQuad)    ? bytesPerVertex : 0;
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
