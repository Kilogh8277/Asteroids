package asteroids.participants;

import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;

public class ShipBullets extends Bullets implements AlienShipDestroyer
{

    public ShipBullets (double x, double y, double direction, Controller controller)
    {
        super(x, y, direction, controller);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof ShipDestroyer)
        {
            Participant.expire(this);
            controller.shipBulletsDestoyed();
        }
        
    }
}
