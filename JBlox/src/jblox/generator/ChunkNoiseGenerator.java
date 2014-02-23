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
    
    final SimplexOctaveGenerator ground;
    final SimplexOctaveGenerator hills;
    final SimplexOctaveGenerator mountains;
    final SimplexOctaveGenerator particles;
    
    final int GROUND_ELEVATION = 20;
    final int GROUND_MAGNITUDE = 6;
    final int HILLS_MAGNITUDE = 12;
    final int MOUNTAINS_MAGNITUDE = 16;
    
    public ChunkNoiseGenerator() {
        this(new Random().nextInt(Integer.MAX_VALUE));
    }
    
    public ChunkNoiseGenerator(final int seed) {
        
        ground = new SimplexOctaveGenerator(seed, 4);
        hills = new SimplexOctaveGenerator(seed, 6);
        mountains = new SimplexOctaveGenerator(seed, 4);
        particles = new SimplexOctaveGenerator(seed, 8);
        
        ground.setScale(1/96.0);
        hills.setScale(1/32.0);
        mountains.setScale(1/64.0);
        particles.setScale(1/16.0);
        
    }

    public void generateNoise(final int cx, final int cz, final Chunk chunk) {// Chunk coordinates
        
        for (byte x = 0; x < 16; x++) {
            for (byte z = 0; z < 16; z++) {
                int wx = x + cx * 16;
                int wz = z + cz * 16;
                
                final double groundHeight     = ground.noise(wx, wz, 0.5, 0.5)    * GROUND_MAGNITUDE + GROUND_ELEVATION;
                final double hills_height     = hills.noise(wx, wz, 0.8, 0.5)     * HILLS_MAGNITUDE + GROUND_ELEVATION;
                final double mountains_height = mountains.noise(wx, wz, 1.5, 0.6) * MOUNTAINS_MAGNITUDE + GROUND_ELEVATION;
                
                short y = 0;
                for (; y < groundHeight; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(wx, y, wz, 0.4, 0.6);
                    
                    if (PARTICLES_NOISE < 0.9 || y == 0) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y , z), (byte) 1);
                    } else {
                        //blocks[locationToByteIndex(x, y, z)] = 4;
                    }
                    
                }
                
                for (; y < hills_height; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(wx, y, wz, 0.3, 0.5);
                    
                    if (PARTICLES_NOISE < 0.9) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y , z), (byte) 1);
                        //blocks[locationToByteIndex(x, y, z)] = getBlockTypeByHeight(y, mountains_height);
                        // Determines block type by depth from top layer
                    }
                    
                }
                
                for (; y < mountains_height; y++) {
                    
                    final double PARTICLES_NOISE = particles.noise(wx, y, wz, 0.3, 0.5);
                    
                    if (PARTICLES_NOISE < 0.9) {
                        chunk.setDataId(ChunkConstants.coordsToIndex(x, y , z), (byte) 1);
                        //blocks[locationToByteIndex(x, y, z)] = getBlockTypeByHeight(y, mountains_height);
                        // Determines block type by depth from top layer
                    }
                    
                }
                
                if (y > chunk.getUppermostBlockY()) {
                    chunk.setUppermostBlockY(y);
                }
            }
        }
    }
}
