package jblox.generator;

import java.util.Random;
import jblox.chunks.Chunk;
import jblox.chunks.Material;
import jblox.generator.noise.SimplexOctaveGenerator;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-08
 * @version 1.0
 */
public class ChunkNoiseGenerator {
    
    private final Random random = new Random();
    
    private final SimplexOctaveGenerator base;
    private final SimplexOctaveGenerator hills;
    private final SimplexOctaveGenerator mountains;
    private final SimplexOctaveGenerator particles;
    
    private final int BASE_ELEVATION = 30;// 30
    private final int BASE_MAGNITUDE = 4;// 4
    private final int HILLS_MAGNITUDE = 12;// 12
    private final int MOUNTAINS_MAGNITUDE = 25;// 25
    
    public ChunkNoiseGenerator() {
        this(new Random().nextInt(Integer.MAX_VALUE));
    }
    
    public ChunkNoiseGenerator(final int seed) {
        
        base = new SimplexOctaveGenerator(seed, 4);// 4
        hills = new SimplexOctaveGenerator(seed, 6);// 6
        mountains = new SimplexOctaveGenerator(seed, 9);// 9
        particles = new SimplexOctaveGenerator(seed, 6);// 6
        
        base.setScale(1 / 32.0);// 32
        hills.setScale(1 / 84.0);// 84
        mountains.setScale(1 / 70.0);// 70
        particles.setScale(1 / 12.0);// 12
        
    }
    
    /**
     * This method generate the bedrock layer
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param x The x-coordinate in the chunk
     * @param global_x The global x-coordinate of the block
     * @param global_z The global z-coordinate of the block
     * @param height The current height
     */
    private short gen_Bedrock(final Chunk chunk, final byte x, final byte z) {
        
        short y = 0;
        for (; y < 3; y++) {
            
            if (y == 0 || random.nextInt(2) > 0) {
                chunk.setDataId(x, y, z, Material.BEDROCK, false);
            } else {
                chunk.setDataId(x, y, z, Material.STONE, false);
            }
        }
        
        return y;
    }
    /**
     * This method generate the base layer
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param x The x-coordinate in the chunk
     * @param global_x The global x-coordinate of the block
     * @param global_z The global z-coordinate of the block
     * @param height The current height
     */
    private short gen_Base(Chunk chunk, byte x, byte z, int global_x, int global_z, short height) {
        
        final double baseHeight = base.noise(global_x, global_z, 0.2, 0.7, true) * BASE_MAGNITUDE + BASE_ELEVATION;
        
        short y = height;
        for (; y < baseHeight; y++) {
            chunk.setDataId(x, y, z, Material.STONE, false);
        }
        
        return y;
    }
    /**
     * This method generate hills
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param x The x-coordinate in the chunk
     * @param global_x The global x-coordinate of the block
     * @param global_z The global z-coordinate of the block
     * @param height The current height
     */
    private short gen_Hills(Chunk chunk, byte x, byte z, int global_x, int global_z, short height) {
        
        final double hillsHeight = hills.noise(global_x, global_z, 0.8, 0.3, true) * HILLS_MAGNITUDE + BASE_ELEVATION;
         
        short y = height;
        for (; y < hillsHeight; y++) {
            chunk.setDataId(x, y, z, Material.STONE, false);
        }
        
        return y;
    }
    
    /**
     * This method generate mountains
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param x The x-coordinate in the chunk
     * @param global_x The global x-coordinate of the block
     * @param global_z The global z-coordinate of the block
     * @param height The current height
     */
    private short gen_Mountains(Chunk chunk, byte x, byte z, int global_x, int global_z, short height) {
        
        final double mountainsHeight = mountains.noise(global_x, global_z, 0.85, 0.2, true) * MOUNTAINS_MAGNITUDE + BASE_ELEVATION;
         
        short y = height;
        for (; y < mountainsHeight; y++) {
            chunk.setDataId(x, y, z, Material.STONE, false);
        }
        
        return y;
    }
    
    /**
     * This method generate caves and particles
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param x The x-coordinate in the chunk
     * @param global_x The global x-coordinate of the block
     * @param global_z The global z-coordinate of the block
     * @param height The current height
     */
    private short gen_Particles(Chunk chunk, byte x, byte z, int global_x, int global_z, short height) {
         
        short y = 0;
        for (; y < height; y++) {
            
            final double noise = particles.noise(global_x, y, global_z, 0.8, 0.85, false);
            
            if (noise > 0.9) {
                
                if (chunk.getDataAt(x, y, z, false) == Material.BEDROCK) {
                    continue;
                }
                
                chunk.setDataId(x, y, z, Material.AIR, false);
            }
        }
        
        return y;
    }
    
    /**
     * This method place grass and dirt on the top layer
     * @param chunk The chunk reference
     * @param x The x-coordinate in the chunk
     * @param z The z-coordinate in the chunk
     * @param height The current height
     */
    private void gen_Toplayer(Chunk chunk, byte x, byte z, short height) {
        
        int depth = height - (3 + random.nextInt(2));
        
        for (short y = height; y >= depth; y--) {
            
            if (chunk.getDataAt(x, y, z, false) == Material.AIR) {
                continue;
            }
            
            if (y == height - 1) {
                chunk.setDataId(x, y, z, Material.GRASS, false);
            } else {
                chunk.setDataId(x, y, z, Material.DIRT, false);
            }
        }
    }

    public void generateNoise(final int cx, final int cz, final Chunk chunk) {// Chunk coordinates
        
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                
                int global_x = x + cx * 16;
                int global_z = z + cz * 16;
                
                short y;
                
                // GENERATE BOTTOM/BEDLOCK LAYER
                y = gen_Bedrock(chunk, x, z);
                
                // GENERATE BASE TERRAIN, AKA. GROUND
                y = gen_Base(chunk, x, z, global_x, global_z, y);
                
                // GENERATE HILLS
                y = gen_Hills(chunk, x, z, global_x, global_z, y);
                
                // GENERATE MOUNTAINS
                y = gen_Mountains(chunk, x, z, global_x, global_z, y);

                // GENERATE CAVES AND PARTICLES
                y = gen_Particles(chunk, x, z, global_x, global_z, y);
                
                /*
                    When gen_Particles runs the mothod does not decrease the
                    height value if top blocks are removed. Has to be fixed
                    in future versions.
                */
                // PLACE GRASS AND DIRT ON TOP LAYERS
                gen_Toplayer(chunk, x, z, y);
                
                
                // SET UPPERMOST Y-COORDINATE, FOR USE WHEN DETERMINING VISIBLE BLOCKS
                if (y > chunk.getUppermostBlockY()) {
                    chunk.setUppermostBlockY(y);
                }
            }
        }
        
        int trees = random.nextInt(3) + 2;
        
        for (int t = 0; t < trees; t++) {
            
            final byte tx = (byte) (random.nextInt(8) + 4);
            final byte tz = (byte) (random.nextInt(8) + 4);
            final short ty = chunk.getUppermostBlockY();
            
            for (short y = ty; y > -1; y--) {
                
                final byte id = chunk.getDataAt(tx, y, tz, false);
                if (id == Material.AIR) {
                    continue;
                }
                
                if (id != Material.GRASS) {
                    break;
                }
                
                gen_Tree(chunk, tx, (short) (y + 1), tz);
            }
        }
    }
    
    private void gen_Tree(Chunk chunk, byte tx, short ty, byte tz) {
        
        final int height = random.nextInt(2) + 3 + ty;
        
        short y = ty;
        for (; y <= height; y++) {    
            chunk.setDataId(tx, y, tz, Material.LOG, false);
        }
        
        y--;
        chunk.setDataId(tx + 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 1, Material.LEAVES, false);
        
        y++;
        chunk.setDataId(tx + 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 2, Material.LEAVES, false);
        
        y++;
        chunk.setDataId(tx + 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 2, Material.LEAVES, false);
        
        y++;
        chunk.setDataId(tx + 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz - 2, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx + 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx - 2, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 2, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 2, Material.LEAVES, false);
        
        y++;
        chunk.setDataId(tx, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx + 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx - 1, y, tz, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz + 1, Material.LEAVES, false);
        chunk.setDataId(tx, y, tz - 1, Material.LEAVES, false);
        
        // SET UPPERMOST Y-COORDINATE, FOR USE WHEN DETERMINING VISIBLE BLOCKS
        if (y > chunk.getUppermostBlockY()) {
            chunk.setUppermostBlockY(y);
        }
    }
}
