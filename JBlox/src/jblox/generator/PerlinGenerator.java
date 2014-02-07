package jblox.generator;

import java.util.Random;

public class PerlinGenerator {
    
    private int[] permutation = new int[256];
    private int[] p = new int[permutation.length * 2];
    
    private float frequency = 0.053f;//0.023
    private float amplitude = 0.7f;//2.2
    private float persistence = 0.15f;//1
    private int octaves = 9;
    
    /*private float frequency = 0.013f;//0.023
    private float amplitude = 0.7f;//2.2
    private float persistence = 0.35f;//1
    private int octaves = 6;*/
    
    public PerlinGenerator() {
        initNoiseFunctions();
    }
    
    private void initNoiseFunctions() {
        Random rand = new Random();
        
        // Fill empty
        for (int i = 0; i < permutation.length; i++) {
            permutation[i] = -1;
        }
        
        // Generate random numbers
        for (int i = 0; i < permutation.length; i++) {
            while(true) {
                int ip = rand.nextInt(Integer.MAX_VALUE) % permutation.length;
                if (permutation[ip] == -1) {
                    permutation[ip] = i;
                    break;
                }
            }
        }
        
        // Copy
        for (int i = 0; i < permutation.length; i++) {
            p[permutation.length + i] = p[i] = permutation[i];
        }
    }
    
    public float compute(float x, float y, float z) {
        float noise = 0;
        float amp = this.amplitude;
        float freq = this.frequency;
        
        for (int i = 0; i < this.octaves; i++) {
            noise += getNoise(x * freq, y * freq, z * freq) * amp;
            freq *= 2;
            amp *= this.persistence;
        }
        return noise;
    }
    
    private float getNoise(float x, float y, float z) {
        // Find unit cube that contains point
         int iX = (int)Math.floor(x) & 255;
         int iY = (int)Math.floor(y) & 255;
         int iZ = (int)Math.floor(z) & 255;

         // Find relative x, y, z of the point in the cube.
         x -= (float)Math.floor(x);
         y -= (float)Math.floor(y);
         z -= (float)Math.floor(z);

         // Compute fade curves for each of x, y, z
         float u = fade(x);
         float v = fade(y);
         float w = fade(z);

         // Hash coordinates of the 8 cube corners
         int A = p[iX] + iY;
         int AA = p[A] + iZ;
         int AB = p[A + 1] + iZ;
         int B = p[iX + 1] + iY;
         int BA = p[B] + iZ;
         int BB = p[B + 1] + iZ;

         // And add blended results from 8 corners of cube.
         return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z),
                            grad(p[BA], x - 1, y, z)),
                    lerp(u, grad(p[AB], x, y - 1, z),
                            grad(p[BB], x - 1, y - 1, z))),
                    lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1),
                            grad(p[BA + 1], x - 1, y, z - 1)),
                    lerp(u, grad(p[AB + 1], x, y - 1, z - 1),
                            grad(p[BB + 1], x - 1, y - 1, z - 1))));
    }
    
    private float fade(float t) {
        return (t * t * t * (t * (t * 6 - 15) + 10));
    }
    
    private float lerp(float alpha, float a, float b) {
        return (a + alpha * (b - a));
    }
    
    private float grad(int hashCode, float x, float y, float z) {
        // Convert lower 4 bits of hash code into 12 gradient directions
        int h = hashCode & 15;
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return (((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v));
    }
}