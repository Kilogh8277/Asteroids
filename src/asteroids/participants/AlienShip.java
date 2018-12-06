package asteroids.participants;

import static asteroids.game.Constants.RANDOM;
import java.awt.Shape;
import java.awt.geom.Path2D;
import asteroids.destroyers.AlienShipDestroyer;
import asteroids.destroyers.AsteroidDestroyer;
import asteroids.destroyers.ShipDestroyer;
import asteroids.game.Controller;
import asteroids.game.Participant;
import asteroids.game.ParticipantCountdownTimer;

public class AlienShip extends Participant implements AsteroidDestroyer, ShipDestroyer
{
    private Controller controller;

    private double direction;
    
    private int level;

    private Shape outline;

    public AlienShip (int x, int y, int level, Controller controller)
    {
        this.controller = controller;
        this.direction = 0;
        this.level = level;

        setPosition(x, y);
        setRotation(-Math.PI / 2);

        if (level >= 3)
        {
            Path2D.Double poly = new Path2D.Double();
            poly.moveTo(0, 0);
            poly.lineTo(0, 20);
            poly.lineTo(-4, 28);
            poly.lineTo(-8, 20);
            poly.lineTo(-8, 0);
            poly.lineTo(-4, -8);
            poly.lineTo(0, 0);
            poly.lineTo(0, 4);
            poly.lineTo(6, 8);
            poly.lineTo(6, 12);
            poly.lineTo(0, 16);
            poly.closePath();

            outline = poly;
        }
        else if (level == 2)
        {
            Path2D.Double poly = new Path2D.Double();
            poly.moveTo(0, 0);
            poly.lineTo(0, 40 * 3 / 4);
            poly.lineTo(-12, 56 * 3 / 4);
            poly.lineTo(-11, -16 * 3 / 4);
            poly.lineTo(-11, 56 * 3 / 4);
            poly.lineTo(-22, 40 * 3 / 4);
            poly.lineTo(-22, 0);
            poly.lineTo(-12, -16 * 3 / 4);
            poly.lineTo(0, 0);
            poly.lineTo(0, 6);
            poly.lineTo(8, 12);
            poly.lineTo(8, 20);
            poly.lineTo(0, 26);
            poly.closePath();

            outline = poly;
        }
    }

    @Override
    protected Shape getOutline ()
    {
        return outline;
    }

    @Override
    public void collidedWith (Participant p)
    {
        if (p instanceof AlienShipDestroyer)
        {
            controller.alienShipDestroyed();
            if (level == 2)
            {
                controller.addScore(200);
                controller.makeDebris(1, this.getX(), this.getY());
            }
            else if (level >= 3)
            {
                controller.addScore(1000);
                controller.makeDebris(2, this.getX(), this.getY());
            }
            Participant.expire(this);
        }
    }

    /**
     * 
     */
    @Override
    public void countdownComplete (Object payload)
    {
        if (payload.equals("small alien"))
        {
            controller.addParticipant(this);
            controller.asteroidsSounds.playSmallSaucer();
            setSpeed(5);
            setDirection((RANDOM.nextInt(1) + 1) * Math.PI);
            direction = getDirection();
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
        else if (payload.equals("medium alien"))
        {
            controller.addParticipant(this);
            controller.asteroidsSounds.playLargeSaucer();
            setSpeed(3);
            setDirection((RANDOM.nextInt(1) + 1) * Math.PI);
            direction = getDirection();
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
        else if (payload.equals("direction"))
        {
            setDirection(RANDOM.nextInt(3) + 1);
        }
        else if (payload.equals("shoot"))
        {
            controller.shootAlienBullets();
            new ParticipantCountdownTimer(this, "shoot", (RANDOM.nextInt(3) + 1) * 1000);
        }
    }

    private void setDirection (int i)
    {
        if (i == 1)
        {
            if (getDirection() != (direction + 1.0))
            {
                super.setDirection(direction + 1.0);
            }
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
        else if (i == 2)
        {
            if (getDirection() != (direction - 1.0))
            {
                super.setDirection(direction - 1.0);
            }
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
        else if (i == 3)
        {
            if (getDirection() != direction)
            {
                super.setDirection(direction);
            }
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
        else
        {
            new ParticipantCountdownTimer(this, "direction", 1000);
        }
    }
}