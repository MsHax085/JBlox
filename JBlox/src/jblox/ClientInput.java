
package jblox;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 *
 * @author Richard
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
                default: break;
            }
        }
    }
}
