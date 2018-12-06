package asteroids.participants;

import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class AlienBullets extends Bullets implements ShipDestroyer
{

    public AlienBullets (double x, double y, double direction, Controller controller)
    {
        super(x, y, direction, controller);
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            Participant.expire(this);
        }
    }
}
