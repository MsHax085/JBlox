
package jblox.client;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.lwjgl.input.Mouse;

/**
 *
 * @author Richard Dahlgren
 * @since 2013-nov-26
 * @version 1.0
 */
public class Client {
    
    private final ClientInput ci;
    
    private final float MOUSE_SENSITIVITY = 0.115f;

    private float x = 0.0f;
    private float y = -50.0f;
    private float z = 0.0f;
    
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;
    private float zSpeed = 0.0f;
    
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
    private final float ACCELERATION = 0.3f;// 0.3f
    private final float FRICTION = -0.95f;// -0.75f
    
    private final ReadWriteLock  rwLock_x = new ReentrantReadWriteLock();
    private final ReadWriteLock  rwLock_z = new ReentrantReadWriteLock();
    private final Lock rLock_x = rwLock_x.readLock();
    private final Lock rLock_z = rwLock_z.readLock();
    
    public Client(final ClientInput clientInput) {
        this.ci = clientInput;
    }
    
    public void update() {
        
        float xForce = 0.0f;
        float yForce = 0.0f;
        float zForce = 0.0f;
        
        {
            yaw += (Mouse.getDX() * MOUSE_SENSITIVITY);
            pitch -= (Mouse.getDY() * MOUSE_SENSITIVITY);
            
            if (yaw >= 360) {
                yaw -= 360;
            } else if (yaw <= -360) {
                yaw += 360;
            }
            
            if (pitch >= 90) {
                pitch = 90;
            } else if (pitch <= -90) {
                pitch = -90;
            }
            
            final byte xAxisMultiplier = ci.getXAxisMultiplier();
            final byte zAxisMultiplier = ci.getZAxisMultiplier();
            
            if (xAxisMultiplier < 0) {// SIDEWAYS
                xForce -= ACCELERATION * (float) Math.cos(Math.toRadians(yaw));
                zForce -= ACCELERATION * (float) Math.sin(Math.toRadians(yaw));
            } else if (xAxisMultiplier > 0) {
                xForce += ACCELERATION * (float) Math.cos(Math.toRadians(yaw));
                zForce += ACCELERATION * (float) Math.sin(Math.toRadians(yaw));
            }
            
            if (zAxisMultiplier < 0) {// DEPTH
                xForce -= ACCELERATION * (float) Math.cos(Math.toRadians(yaw - 90));
                zForce -= ACCELERATION * (float) Math.sin(Math.toRadians(yaw - 90));
            } else if (zAxisMultiplier > 0) {
                xForce -= ACCELERATION * (float) Math.cos(Math.toRadians(yaw + 90));
                zForce -= ACCELERATION * (float) Math.sin(Math.toRadians(yaw + 90));
            }
            
            yForce += ACCELERATION * ci.getYAxisMultiplier();
            
            xForce += xSpeed * FRICTION;
            yForce += ySpeed * FRICTION;
            zForce += zSpeed * FRICTION;
        }
        
        final float multiplier = (ci.isRCTRLPressed()) ? 0.1f : 1.0f;
        
        xSpeed += xForce;
        ySpeed += yForce;
        zSpeed += zForce;
        
        x += xSpeed * multiplier;
        y += ySpeed * multiplier;
        z += zSpeed * multiplier;
    }
    
    // -------------------------------------------------------------------------
    
    public boolean isMoving() {
        return xSpeed > 0 ||
               ySpeed > 0 ||
               zSpeed > 0;
    }
    
    public float getX() {
        rLock_x.lock();
        try {
            return x;
        } finally {
            rLock_x.unlock();
        }
    }
    
    public float getY() {
        return y;
    }
    
    public float getZ() {
        rLock_z.lock();
        try {
            return z;
        } finally {
            rLock_z.unlock();
        }
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
}
