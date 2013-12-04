
package jblox;

import de.matthiasmann.twl.GUI;
import de.matthiasmann.twl.Label;
import de.matthiasmann.twl.Widget;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;
import de.matthiasmann.twl.theme.ThemeManager;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-29
 * @version 1.0
 */
public class ClientInterface extends Widget {
    
    private final ClientDisplay display;

    private LWJGLRenderer renderer;
    private GUI gui;
    private ThemeManager manager;
    
    private boolean isCreated = false;
    
    private Label label_fps;
    private Label label_x;
    private Label label_y;
    private Label label_z;
    private Label label_yaw;
    private Label label_pitch;
    private Label label_dir;
    
    public ClientInterface(final ClientDisplay display) {
        this.display = display;
    }
    
    public void init() {
        
        try {
            renderer = new LWJGLRenderer();
        } catch (LWJGLException ex) {
            Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        gui = new GUI(this, renderer);
        
        try {
            manager = ThemeManager.createThemeManager(getClass().getResource("ui.xml"), renderer);
        } catch (IOException ex) {
            Logger.getLogger(ClientInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        gui.applyTheme(manager);
    }
    
    @Override
    protected void layout() {
        label_fps.adjustSize();
        label_x.adjustSize();
        label_y.adjustSize();
        label_z.adjustSize();
        label_dir.adjustSize();
        label_yaw.adjustSize();
        label_pitch.adjustSize();
        
        label_fps.setPosition(20, 20);
        label_x.setPosition(20, 60);
        label_y.setPosition(20, 80);
        label_z.setPosition(20, 100);
        label_dir.setPosition(20, 140);
        label_yaw.setPosition(20, 160);
        label_pitch.setPosition(20, 180);
    }
    
    public void update() {
        if (!isCreated) {
            createDebugMenu();
            isCreated = true;
        }
        
        final Client client = display.getClient();
        label_fps.setText("FPS: " + display.getFPS());
        label_x.setText("X-POS: " + client.getX());
        label_y.setText("Y-POS: " + client.getY());
        label_z.setText("Z-POS: " + client.getZ());
        
        final float yaw = client.getYaw();
        
        label_dir.setText("DIRECTION: " + getDirection(yaw));
        label_yaw.setText("YAW: " + yaw);
        label_pitch.setText("PITCH: " + client.getPitch());
        
        gui.update();
    }
    
    private void createDebugMenu() {
        label_fps = new Label();
        label_x = new Label();
        label_y = new Label();
        label_z = new Label();
        label_dir = new Label();
        label_yaw = new Label();
        label_pitch = new Label();
        
        add(label_fps);
        add(label_x);
        add(label_y);
        add(label_z);
        add(label_dir);
        add(label_yaw);
        add(label_pitch);
    }
    
    private String getDirection(final float yaw) {
        
        String direction = "NORTH";// (yaw > 315 && yaw < 45) || (yaw > -45 && yaw < -315)
        
        if ((yaw > 45 && yaw < 135) || (yaw > -315 && yaw < -225)) {
            direction = "EAST";
            
        } else if ((yaw > 135 && yaw < 225) || (yaw > -225 && yaw < -135)) {
            direction = "SOUTH";
            
        } else if ((yaw > 225 && yaw < 315) || (yaw > -135 && yaw < -45)) {
            direction = "WEST";
            
        }
        
        return direction;
    }
}
