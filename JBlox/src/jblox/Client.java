
package jblox;

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
    private float y = -2.0f;
    private float z = -70.0f;
    
    private float xSpeed = 0.0f;
    private float ySpeed = 0.0f;
    private float zSpeed = 0.0f;
    
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
    private final float ACCELERATION = 0.3f;// 0.3f
    private final float FRICTION = -0.75f;
    
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
            
            if (pitch >= 360) {
                pitch -= 360;
            } else if (pitch <= -360) {
                pitch += 360;
            }
            
            final byte xAxisMultiplier = ci.getXAxisMultiplier();
            final byte zAxisMultiplier = ci.getZAxisMultiplier();
            
            if (xAxisMultiplier < 0) {//    Sideways
                xForce -= ACCELERATION * (float) Math.cos(Math.toRadians(yaw));
                zForce -= ACCELERATION * (float) Math.sin(Math.toRadians(yaw));
            } else if (xAxisMultiplier > 0) {
                xForce += ACCELERATION * (float) Math.cos(Math.toRadians(yaw));
                zForce += ACCELERATION * (float) Math.sin(Math.toRadians(yaw));
            }
            
            if (zAxisMultiplier < 0) {//    Depth
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
        
        xSpeed += xForce;
        ySpeed += yForce;
        zSpeed += zForce;
        
        x += xSpeed;
        y += ySpeed;
        z += zSpeed;
    }
    
    // -------------------------------------------------------------------------
    
    public float getX() {
        return x;
    }
    
    public float getY() {
        return y;
    }
    
    public float getZ() {
        return z;
    }
    
    public float getYaw() {
        return yaw;
    }
    
    public float getPitch() {
        return pitch;
    }
}
