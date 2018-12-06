package asteroids.participants;

import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;
import static asteroids.game.Constants.*;


public class Debris extends Participant
{
    private Shape outline;
    
    public Debris (double x, double y, int length) {
        
        setPosition(x + RANDOM.nextInt(24)-12, y + RANDOM.nextInt(24)-12);
        setRotation(RANDOM.nextDouble()*2*Math.PI);
        setVelocity(RANDOM.nextDouble(), RANDOM.nextDouble()*2*Math.PI);
        
        
        Path2D.Double line = new Path2D.Double();
        line.moveTo(0, 0);
        line.lineTo(0, length);
        line.closePath();
        
        outline = line;
        
        new ParticipantCountdownTimer(this, null, 3000);
    }

    @Override
    protected Shape getOutline ()
    {
        
        return outline;
    }
    
    public void countdownComplete (Object payload) {
        Participant.expire(this);
    }

    @Override
    public void collidedWith (Participant p)
    {
        
    }

}