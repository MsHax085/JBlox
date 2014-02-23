
package jblox.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-25
 * @version 1.0
 */
public class ClientInput {
    
    private int MOUSE_X;
    private int MOUSE_Y;
    
    private boolean W_PRESSED = false;
    private boolean A_PRESSED = false;
    private boolean S_PRESSED = false;
    private boolean D_PRESSED = false;
    private boolean SPACE_PRESSED = false;
    private boolean SHIFT_PRESSED = false;
    private boolean ESC_PRESSED = false;

    public void checkForInput() {
        
        MOUSE_X = Mouse.getX();
        MOUSE_Y = Mouse.getY();
        
        while (Keyboard.next()) {
            
            final boolean keyPressed = Keyboard.getEventKeyState();
            
            switch (Keyboard.getEventKey()) {
                case Keyboard.KEY_W: W_PRESSED = keyPressed; break;
                case Keyboard.KEY_A: A_PRESSED = keyPressed; break;
                case Keyboard.KEY_S: S_PRESSED = keyPressed; break;
                case Keyboard.KEY_D: D_PRESSED = keyPressed; break;
                case Keyboard.KEY_SPACE: SPACE_PRESSED = keyPressed; break;
                case Keyboard.KEY_LSHIFT: SHIFT_PRESSED = keyPressed; break;
                case Keyboard.KEY_ESCAPE: ESC_PRESSED = keyPressed; break;
                default: break;
            }
        }
    }
    
    // -------------------------------------------------------------------------
    
    public int getMouseX() {
        return MOUSE_X;
    }
    
    public int getMouseY() {
        return MOUSE_Y;
    }
    
    // -------------------------------------------------------------------------
    
    public byte getXAxisMultiplier() {
        
        if ((!A_PRESSED && !D_PRESSED) || (A_PRESSED && D_PRESSED)) {
            return 0;
            
        } else if (A_PRESSED) {
            return 1;
        
        } else {
            return -1;
        }
    }
    
    public byte getYAxisMultiplier() {
        
        if ((!SPACE_PRESSED && !SHIFT_PRESSED) || (SPACE_PRESSED && SHIFT_PRESSED)) {
            return 0;
            
        } else if (SPACE_PRESSED) {
            return -1;
        
        } else {
            return 1;
        }
    }
    
    public byte getZAxisMultiplier() {
        
        if ((!W_PRESSED && !S_PRESSED) || (W_PRESSED && S_PRESSED)) {
            return 0;
            
        } else if (W_PRESSED) {
            return -1;
        
        } else {
            return 1;
        }
    }
    
    public boolean isESCPressed() {
        return ESC_PRESSED;
    }
}
