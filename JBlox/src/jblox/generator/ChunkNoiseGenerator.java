package jblox.generator;

import java.util.Random;
import jblox.chunks.Chunk;
import jblox.chunks.ChunkConstants;
import jblox.generator.noise.SimplexOctaveGenerator;

/**
 *
 * @author Richard Dahlgren
 * @since 2014-feb-08
 * @version 1.0
 */
public class ChunkNoiseGenerator {
    
    private final Random random = new Random();
    
    private final SimplexOctaveGenerator ground;
    private final SimplexOctaveGenerator hills;
    private final SimplexOctaveGenerator mountains;
    private final SimplexOctaveGenerator particles;
    
    private final int GROUND_ELEVATION = 30;
    private final int GROUND_MAGNITUDE = 4;
    private final int HILLS_MAGNITUDE = 8;
    private final int MOUNTAINS_MAGNITUDE = 16;
    
    public ChunkNoiseGenerator() {
        this(new Random().nextInt(Integer.MAX_VALUE));
    }
    
    public ChunkNoiseGenerator(final int seed) {
        
        ground = new SimplexOctaveGenerator(seed, 4);
        hills = new SimplexOctaveGenerator(seed, 6);
        mountains = new SimplexOctaveGenerator(seed, 4);
        particles = new SimplexOctaveGenerator(seed, 8);
        
        ground.setScale(1 / 96.0);
        hills.setScale(1 / 32.0);
        mountains.setScale(1 / 64.0);
        particles.setScale(1 / 16.0);
        
    }

    public void generateNoise(final int cx, final int cz, final Chunk chunk) {// Chunk coordinates
        
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                
                int global_x = x + cx * 16;
                int global_z = z + cz * 16;
                
                final double groundHeight     = ground.noise(global_x, global_z, 0.5, 0.5)    * GROUND_MAGNITUDE + GROUND_ELEVATION;
                final double hills_height     = hills.noise(global_x, global_z, 0.8, 0.5)     * HILLS_MAGNITUDE + GROUND_ELEVATION;
                final double mountains_height = mountains.noise(global_x, global_z, 1.5, 0.6) * MOUNTAINS_MAGNITUDE + GROUND_ELEVATION;
                
                short y = 0;
                for (; y < groundHeight; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(global_x, y, global_z, 0.4, 0.6);
                    
                    if (PARTICLES_NOISE < 0.9 || y == 0) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y, z),
                                        getBlockIdByDepth(y, mountains_height));
                    } else {
                        //blocks[locationToByteIndex(x, y, z)] = 4;
                        // WATER
                    }
                    
                }
                
                for (; y < hills_height; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(global_x, y, global_z, 0.3, 0.5);
                    
                    if (PARTICLES_NOISE < 0.9) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y, z),
                                        getBlockIdByDepth(y, mountains_height));
                    }
                    
                }
                
                for (; y < mountains_height; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(global_x, y, global_z, 0.3, 0.5);
                    
                    if (PARTICLES_NOISE < 0.9) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y, z),
                                        getBlockIdByDepth(y, mountains_height));
                    }
                    
                }
                
                if (y > chunk.getUppermostBlockY()) {
                    chunk.setUppermostBlockY(y);
                }
            }
        }
    }
    
    private byte getBlockIdByDepth(final int y, final double uppermostBlockY) {
        final double depth = uppermostBlockY - y;
        
        if (depth < 0) {
            return 0;// AIR, PREVENTS DOUBLE-GRASS
        }
        
        if (depth < 1) {
            return 2;// GRASS
        }
        
        if (depth <= 4 + random.nextInt(2)) {
            return 3;// DIRT
        }
        
        return 1;// STONE
    }
}
