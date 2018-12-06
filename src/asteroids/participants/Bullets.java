package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import static asteroids.game.Constants.*;

public class Bullets extends Participant implements AsteroidDestroyer
{
    protected Controller controller;
    private int width = 1;
    private int height = 1;
    private Shape outline;
    public Bullets (double x, double y, double direction, Controller controller)
    {
        this.controller = controller;
        
        setPosition(0, 0);
        setVelocity(BULLET_SPEED, direction);
        Rectangle2D.Double poly = new Rectangle2D.Double(x, y, width, height);
        outline = poly;
        
        new ParticipantCountdownTimer(this, "Bullet", BULLET_DURATION);
    }
    
    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
        
    }
    
    /**
     * 
     */
    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("Bullet"))
        {
            Participant.expire(this);
        }
    }
}
