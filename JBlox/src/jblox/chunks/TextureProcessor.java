
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
    //private Texture stone;// Temporary

    private void loadTextures() {
        
        if (!loadedTextures) {
            try {
                textures = TextureLoader.getTexture("PNG", new FileInputStream("src/jblox/res/textures.png"));
                id = textures.getTextureID();
                //stone = TextureLoader.getTexture("PNG", new FileInputStream("src/jblox/res/stone.png"));// Temporary
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
}
