
package jblox.chunks;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-dec-03
 * @version 1.0
 */
public class TextureProcessor {
    
    private boolean loadedTextures = false;
    private Texture textures;
    private int id;

    private void loadTextures() {
        
        if (!loadedTextures) {
            try {
                textures = TextureLoader.getTexture("PNG", new FileInputStream("src/jblox/res/textures.png"));
                id = textures.getTextureID();
            } catch (IOException ex) {
                Logger.getLogger(TextureProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
            loadedTextures = true;
        }
    }
    
    public int getTextureId() {
        loadTextures();
        return id;
    }
    
    public float getTextureX1(final byte id, final boolean top, final boolean bottom) {
        return (1 / ChunkConstants.TEXTURE_COLS) * getActualId(id, top, bottom);
    }
    
    public float getTextureY1(final byte id, final boolean top, final boolean bottom) {
        return (1 / ChunkConstants.TEXTURE_ROWS) * getActualId(id, top, bottom);
    }
    
    /**
     * This method convert ID's to texture ID's
     * @param id The id to be converted into an texture id
     * @param top Defines whether it's a top face or not
     * @param bottom Defines whether it's a bottom face or not
     * @return Returns the texture id
     */
    private int getActualId(final byte id, final boolean top, final boolean bottom) {
        
        switch (id) {
            
            case 1:
            {
                return Material.AIR;
            }
            
            case 2:
            {
                if (top) {
                    return Material.GRASS;
                }
                
                if (bottom) {
                    return Material.DIRT;
                }
                
                return id - 1;
            }
            
            default:
            {
                return id;
            }
        }
    }
}
