
package jblox;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-25
 * @version 1.0
 */
public class ClientDisplay {
    
    private final ClientInput clientInput;
    private final Client client;
    private final ClientInterface clientInterface;
    private final GraphicsProcessor gProcessor;
    
    private final int WIDTH = 1000;
    private final int HEIGHT = 600;
    
    private long lastFrame;
    private long lastFPS;
    private int fpsCounter;
    
    private int fps;
    
    public ClientDisplay() {
        clientInput = new ClientInput();
        client = new Client(clientInput);
        clientInterface = new ClientInterface(this);
        gProcessor = new GraphicsProcessor();
    }
    
    // Main-Methods ------------------------------------------------------------

    /**
     * Starts the game
     */
    public void start() {
        
        // Initialize OpenGL
        initDisplay();
        clientInterface.init();
        gProcessor.initOpenGLScene(WIDTH, HEIGHT);
        gProcessor.initOpenGL();
        
        Mouse.setGrabbed(true);
        
        // Run Game
        loop();
        Display.destroy();
    }
    
    /**
     * Creates the OpenGL display
     */
    private void initDisplay() {
        
        final DisplayMode mode = new DisplayMode(WIDTH, HEIGHT);
        
        try {
            Display.setDisplayMode(mode);
            Display.setTitle("JBlox");
            Display.create();
        } catch (LWJGLException ex) {
            System.out.println("Exception caught while initializing display: " + ex.getMessage());
            System.exit(0);
        }
    }
    
    /**
     * Defines the game loop
     */
    private void loop() {
        getDelta();
        lastFPS = getTime();
        
        while (!Display.isCloseRequested() && !clientInput.isESCPressed()) {

            final int delta = getDelta();
            
            final float x = client.getX();
            final float y = client.getY();
            final float z = client.getZ();
            
            final float yaw = client.getYaw();
            final float pitch = client.getPitch();
            
            update(delta);
            gProcessor.draw(x, y, z, yaw, pitch);
            clientInterface.update();
            
            Display.update();
            Display.sync(60);// Cap at 60fps
        }
        
        gProcessor.clear();// Dispose VBO's
    }
    
    /**
     * Updates the game
     * @param delta The time passed between current and last update
     */
    private void update(final int delta) {
        
        clientInput.checkForInput();
        client.update();
        
        updateFPS();
    }
    
    // Sub-Methods -------------------------------------------------------------
    
    /**
     * Updates the FPS
     */
    private void updateFPS() {
        if (getTime() - lastFPS > 250) {
            fps = fpsCounter * 4;
            fpsCounter = 0;
            lastFPS += 250;
        }
        fpsCounter++;
    }
    
    /**
     * Returns the accurate OpenGL time
     * @return time OpenGL time
     */
    private long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }
    
    /**
     * Returns the time passed between current and last frame
     * @return time Time difference
     */
    private int getDelta() {
        final long time = getTime();
        final int delta = (int) (time - lastFrame);
        lastFrame = time;
        
        return delta;
    }
    
    /**
     * Returns the calculated FPS
     * @return fps Calculated FPS
     */
    public int getFPS() {
        return fps;
    }
    
    /**
     * Returns a Client reference
     * @return Client Client Reference
     */
    public Client getClient() {
        return client;
    }
}
