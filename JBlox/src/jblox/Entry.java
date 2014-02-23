package jblox;

import jblox.client.ClientInterface;
import jblox.client.ClientInput;
import jblox.client.Client;
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
 * 
 * -Djava.library.path=C:\Users\nti\Documents\GitHub\JBlox\lwjgl-2.9.0\native\windows
 * -Djava.library.path=F:\GitHub\JBlox\lwjgl-2.9.0\native\windows
 * 
 */
public class Entry {
    
    private final ClientInput clientInput;
    private final Client client;
    private final ClientInterface clientInterface;
    private final Scene scene;
    
    private final int WIDTH = 1000;
    private final int HEIGHT = 600;
    
    private long lastFrame;
    private long lastFPS;
    private int fpsCounter;
    
    private int fps;

    public Entry() {
        clientInput = new ClientInput();
        client = new Client(clientInput);
        clientInterface = new ClientInterface(this);
        scene = new Scene(client);
        
        start();
    }
    
    // Main-Methods ------------------------------------------------------------

    /**
     * Starts the game
     */
    private void start() {
        
        // Initialize OpenGL
        initDisplay();
        clientInterface.init();
        scene.initOpenGLScene(WIDTH, HEIGHT);
        scene.initOpenGL();
        
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
            
            final boolean isMoving = client.isMoving();
            
            update(delta);
            scene.draw(x, y, z, yaw, pitch, isMoving);
            clientInterface.update();
            
            Display.update();
            Display.sync(120);// Cap at 120fps
        }
        
        scene.clear();// Dispose VBO's
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
    
    public static void main(final String[] args) {
        final Entry entry = new Entry();
    }
}

