package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.event.*;
import java.util.Iterator;
import javax.swing.*;
import sounds.*;
import asteroids.participants.AlienBullets;
import asteroids.participants.AlienShip;
import asteroids.participants.Asteroid;
import asteroids.participants.Bullets;
import asteroids.participants.Debris;
import asteroids.participants.Ship;
import asteroids.participants.ShipBullets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controls a game of Asteroids.
 */
public class Controller implements KeyListener, ActionListener
{
    /** The state of all the Participants */
    private ParticipantState pstate;

    /** The ship (if one is active) or null (otherwise) */
    private Ship ship;

    private AlienShip alienShip;

    private AlienBullets alienBullet;

    private ShipBullets shipBullet;

    public AsteroidsSounds asteroidsSounds;

    public boolean drawThrust;

    /** When this timer goes off, it is time to refresh the animation */
    private Timer refreshTimer;

    private Timer beatTimer;

    private boolean upPressed;

    private boolean rightPressed;

    private boolean leftPressed;

    private boolean downPressed;

    private boolean beat1;

    /** Current level of the game */
    private int level;

    /** Current player score */
    private int score;

    /** Count of the bullets on the screen */
    private int bulletCount;

    /**
     * The time at which a transition to a new stage of the game should be made. A transition is scheduled a few seconds
     * in the future to give the user time to see what has happened before doing something like going to a new level or
     * resetting the current level.
     */
    private long transitionTime;

    /** Number of lives left */
    private int lives;

    /** The game display */
    private Display display;

    private int beatIntervals;

    /**
     * Constructs a controller to coordinate the game and screen
     */
    public Controller ()
    {
        beat1 = true;
        downPressed = false;
        upPressed = false;
        rightPressed = false;
        leftPressed = false;
        drawThrust = false;

        beatIntervals = INITIAL_BEAT;

        level = 1;

        score = 0;

        bulletCount = 0;

        asteroidsSounds = new AsteroidsSounds();

        // Initialize the ParticipantState
        pstate = new ParticipantState();

        // Set up the refresh timer.
        refreshTimer = new Timer(FRAME_INTERVAL, this);

        beatTimer = new Timer(beatIntervals, this);

        // Clear the transitionTime
        transitionTime = Long.MAX_VALUE;

        // Record the display object
        display = new Display(this);

        // Bring up the splash screen and start the refresh timer
        splashScreen();
        display.setVisible(true);
        refreshTimer.start();
    }

    /**
     * Returns the ship, or null if there isn't one
     */
    public Ship getShip ()
    {
        return ship;
    }

    /**
     * Configures the game screen to display the splash screen
     */
    private void splashScreen ()
    {
        // Clear the screen, reset the level, and display the legend
        clear();
        display.setLegend("Asteroids");

        // Place four asteroids near the corners of the screen.
        placeAsteroids();
    }

    /**
     * The game is over. Displays a message to that effect.
     */
    private void finalScreen ()
    {
        display.setLegend(GAME_OVER);
        display.removeKeyListener(this);
    }

    /**
     * Place a new ship in the center of the screen. Remove any existing ship first.
     */
    private void placeShip ()
    {
        upPressed = false;
        downPressed = false;
        rightPressed = false;
        leftPressed = false;

        beatTimer.start();

        // Place a new ship
        Participant.expire(ship);
        ship = new Ship(SIZE / 2, SIZE / 2, -Math.PI / 2, this);
        addParticipant(ship);
        display.setLegend("");
    }

    private void placeAlienShip ()
    {
        int timeShip = RANDOM.nextInt(5) + 5;
        int timeBullet = (RANDOM.nextInt(3) + 11) * 1000;
        Participant.expire(alienShip);
        if (level == 2)
        {
            alienShip = new AlienShip(0, RANDOM.nextInt(751), level, this);
            new ParticipantCountdownTimer(alienShip, "medium alien", timeShip * 1000);
            new ParticipantCountdownTimer(alienShip, "shoot", timeBullet);
        }
        else if (level >= 3)
        {
            alienShip = new AlienShip(0, RANDOM.nextInt(751), level, this);
            new ParticipantCountdownTimer(alienShip, "small alien", timeShip * 1000);
            new ParticipantCountdownTimer(alienShip, "shoot", timeBullet);
        }
    }

    /**
     * Places an asteroid near one corner of the screen. Gives it a random velocity and rotation.
     */
    private void placeAsteroids ()
    {
        for (int i = 0; i < level + 3; i++)
        {
            int ranNum = RANDOM.nextInt(3);
            int ranHeight = RANDOM.nextInt(325);
            int ranWidth = RANDOM.nextInt(325);
            if (i == 1)
            {
                ranHeight = 750 - ranHeight;
                addParticipant(new Asteroid(ranNum, 2, ranWidth, ranHeight, MAXIMUM_LARGE_ASTEROID_SPEED, this));
            }
            else if (i % 2 == 0)
            {
                ranWidth = 750 - ranWidth;
                addParticipant(new Asteroid(ranNum, 2, ranWidth, ranHeight, MAXIMUM_LARGE_ASTEROID_SPEED, this));
            }
            else if (i % 3 == 0)
            {
                ranHeight = 750 - ranHeight;
                ranWidth = 750 - ranWidth;
                addParticipant(new Asteroid(ranNum, 2, ranWidth, ranHeight, MAXIMUM_LARGE_ASTEROID_SPEED, this));
            }
            else
            {
                addParticipant(new Asteroid(ranNum, 2, ranWidth, ranHeight, MAXIMUM_LARGE_ASTEROID_SPEED, this));
            }
        }
    }

    /**
     * Clears the screen so that nothing is displayed
     */
    private void clear ()
    {
        Participant.expire(alienShip);
        pstate.clear();
        display.setLegend("");
        ship = null;
        alienShip = null;
        shipBullet = null;
        alienBullet = null;
        if (level == 2)
        {
            asteroidsSounds.stopLargeSaucer();
        }
        else if (level >= 3)
        {
            asteroidsSounds.stopSmallSaucer();
        }
    }

    /**
     * Sets things up and begins a new game.
     */
    private void initialScreen ()
    {
        beatTimer.stop();
        beatIntervals = INITIAL_BEAT;
        beatTimer = new Timer(beatIntervals, this);
        beatTimer.start();
        // Clear the screen
        clear();

        // Reset statistics
        lives = 3;
        level = 1;
        upPressed = false;
        rightPressed = false;
        leftPressed = false;
        downPressed = false;

        // Place asteroids
        placeAsteroids();

        // Place the ship
        placeShip();

        // Start listening to events (but don't listen twice)
        display.removeKeyListener(this);
        display.addKeyListener(this);

        // Give focus to the game screen
        display.requestFocusInWindow();
    }

    /**
     * Adds a new Participant
     */
    public void addParticipant (Participant p)
    {
        pstate.addParticipant(p);
    }

    /**
     * The ship has been destroyed
     */
    public void shipDestroyed ()
    {
        beatTimer.stop();
        asteroidsSounds.stopThrust();
        drawThrust = false;
        // Null out the ship
        ship = null;

        // Decrement lives
        lives--;

        // Since the ship was destroyed, schedule a transition
        scheduleTransition(END_DELAY);
    }

    public void alienShipDestroyed ()
    {
        alienShip = null;
        if (level == 2)
        {
            asteroidsSounds.stopLargeSaucer();
        }
        if (level >= 3)
        {
            asteroidsSounds.stopSmallSaucer();
        }
        asteroidsSounds.playAlienShipExplosion();
        placeAlienShip();
    }

    /**
     * An asteroid has been destroyed
     */
    public void asteroidDestroyed (int type)
    {
        if (type == 0)
        {
            asteroidsSounds.playSmallAsteroidExplosion();
        }
        else if (type == 1)
        {
            asteroidsSounds.playMediumAsteroidExplosion();
        }
        else
        {
            asteroidsSounds.playLargeAsteroidExplosion();
        }
        // If all the asteroids are gone, schedule a transition
        if (pstate.countAsteroids() == 0)
        {
            beatTimer.stop();
            level++;
            scheduleTransition(END_DELAY);
        }
    }

    public void shipBulletsDestoyed ()
    {
        shipBullet = null;
    }

    /**
     * Schedules a transition m msecs in the future
     */
    private void scheduleTransition (int m)
    {
        transitionTime = System.currentTimeMillis() + m;
    }

    /**
     * This method will be invoked because of button presses and timer events.
     */
    @Override
    public void actionPerformed (ActionEvent e)
    {
        // The start button has been pressed. Stop whatever we're doing
        // and bring up the initial screen
        if (e.getSource() instanceof JButton)
        {
            initialScreen();
        }

        else if (e.getSource() == beatTimer)
        {
            if (beat1 && beatIntervals >= 300)
            {
                asteroidsSounds.playBeat1();
                beat1 = false;
                beatIntervals -= BEAT_DELTA;
                beatTimer.stop();
                beatTimer = new Timer(beatIntervals, this);
                beatTimer.start();
            }
            else if (!beat1 && beatIntervals >= 300)
            {
                asteroidsSounds.playBeat2();
                beat1 = true;
                beatIntervals -= BEAT_DELTA;
                beatTimer.stop();
                beatTimer = new Timer(beatIntervals, this);
                beatTimer.start();
            }
            else if (beat1)
            {
                asteroidsSounds.playBeat1();
                beat1 = false;
                beatTimer.stop();
                beatTimer = new Timer(FASTEST_BEAT, this);
                beatTimer.start();
            }
            else
            {
                asteroidsSounds.playBeat2();
                beat1 = true;
                beatTimer.stop();
                beatTimer = new Timer(FASTEST_BEAT, this);
                beatTimer.start();
            }
        }
        // Time to refresh the screen and deal with keyboard input
        else if (e.getSource() == refreshTimer)
        {
            // It may be time to make a game transition
            performTransition();

            // Move the participants to their new locations
            pstate.moveParticipants();

            if (ship != null)
            {
                if (upPressed)
                {
                    ship.accelerate();
                    drawThrust = !drawThrust;
                    asteroidsSounds.playThrust();
                }
                else
                {
                    ship.applyFriction(SHIP_FRICTION);
                }
                if (rightPressed)
                {
                    ship.turnRight();
                }

                if (leftPressed)
                {
                    ship.turnLeft();
                }
                if (downPressed && countBullets() < 8)
                {
                    shootBullet();

                }
            }

            display.setScore(score);
            display.setLevel(level);
            display.setLives(lives);

            // Refresh screen
            display.refresh();
        }
    }

    /**
     * Returns an iterator over the active participants
     */
    public Iterator<Participant> getParticipants ()
    {
        return pstate.getParticipants();
    }

    /**
     * If the transition time has been reached, transition to a new state
     */
    private void performTransition ()
    {
        // Do something only if the time has been reached
        if (transitionTime <= System.currentTimeMillis())
        {
            // Clear the transition time
            transitionTime = Long.MAX_VALUE;

            // If there are no lives left, the game is over. Show the final
            // screen.
            if (lives <= 0)
            {
                finalScreen();
            }
            else if (pstate.countAsteroids() == 0)
            {
                beatTimer.stop();
                beatIntervals = INITIAL_BEAT;
                beatTimer = new Timer(beatIntervals, this);
                beatTimer.start();

                placeShip();
                placeAsteroids();
                placeAlienShip();
                if (level == 3)
                {
                    asteroidsSounds.stopLargeSaucer();
                }
                else if (level >= 4)
                {
                    asteroidsSounds.stopSmallSaucer();
                }
            }
            else
            {
                placeShip();
            }
        }
    }

    /**
     * If a key of interest is pressed, record that it is down.
     */
    @Override
    public void keyPressed (KeyEvent e)
    {

        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D && ship != null)
        {
            rightPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A && ship != null)
        {
            leftPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP && ship != null || e.getKeyCode() == KeyEvent.VK_W && ship != null)
        {
            upPressed = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE)
        {
            downPressed = true;
        }
    }

    /**
     * These events are ignored.
     */
    @Override
    public void keyTyped (KeyEvent e)
    {
    }

    /**
     * When acceleration is finished, apply friction to eventually stop the ship
     */
    @Override
    public void keyReleased (KeyEvent e)
    {
        if ((e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) && ship != null)
        {
            rightPressed = false;
        }

        if ((e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) && ship != null)
        {
            leftPressed = false;
        }

        if ((e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) && ship != null)
        {
            upPressed = false;
            drawThrust = false;
            asteroidsSounds.stopThrust();
        }
        if ((e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_SPACE) && ship != null)
        {
            downPressed = false;
        }
    }

    public void shootBullet ()
    {
        double bulletDir = ship.getRotation();
        double bulletX = ship.getXNose();
        double bulletY = ship.getYNose();

        shipBullet = new ShipBullets(bulletX, bulletY, bulletDir, this);
        asteroidsSounds.playFireSound();
        addParticipant(shipBullet);
    }

    public void addScore (int increase)
    {
        score += increase;
    }

    public void makeMediumAsteroids (double x, double y)
    {
        int variety = RANDOM.nextInt(3);
        int speed = RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_MEDIUM_ASTEROID_SPEED)
                + MAXIMUM_MEDIUM_ASTEROID_SPEED;
        addParticipant(new Asteroid(variety, 1, x, y, speed, this));
        variety = RANDOM.nextInt(3);
        speed = RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_MEDIUM_ASTEROID_SPEED)
                + MAXIMUM_MEDIUM_ASTEROID_SPEED;
        addParticipant(new Asteroid(variety, 1, x, y, speed, this));
    }

    public void makeSmallAsteroids (double x, double y)
    {
        int variety = RANDOM.nextInt(3);
        int speed = RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_LARGE_ASTEROID_SPEED)
                + MAXIMUM_LARGE_ASTEROID_SPEED;
        addParticipant(new Asteroid(variety, 0, x, y, speed, this));
        variety = RANDOM.nextInt(3);
        speed = RANDOM.nextInt(MAXIMUM_SMALL_ASTEROID_SPEED - MAXIMUM_LARGE_ASTEROID_SPEED)
                + MAXIMUM_LARGE_ASTEROID_SPEED;
        addParticipant(new Asteroid(variety, 0, x, y, speed, this));
    }

    public int countBullets ()
    {
        bulletCount = 0;
        Iterator<Participant> parts = getParticipants();
        while (parts.hasNext())
        {
            if (parts.next() instanceof Bullets)
            {
                bulletCount++;
            }
        }
        return bulletCount;
    }

    public void shootAlienBullets ()
    {
        if (ship != null)
        {
            double shipX = ship.getX();
            double shipY = ship.getY();

            double alienX = alienShip.getX();
            double alienY = alienShip.getY();

            double diffX = alienX - shipX;
            double diffY = alienY - shipY;

            double direction = 0;

            if (level == 2)
            {
                direction = RANDOM.nextInt(361) * (2 * Math.PI) / 360;
            }
            else if (level >= 3)
            {
                int upOrDown = RANDOM.nextInt(2);
                double chooseRadianAngle = RANDOM.nextInt(5) * (2 * Math.PI / 360);
                if (upOrDown == 0)
                {
                    direction = Math.atan2(diffY, diffX) + Math.PI + chooseRadianAngle;
                }
                else
                {
                    direction = Math.atan2(diffY, diffX) + Math.PI - chooseRadianAngle;
                }
            }
            alienBullet = new AlienBullets(alienX, alienY, direction, this);
            addParticipant(alienBullet);
        }
    }

    public void makeDebris (int type, double x, double y)
    {
        if (type == 0)
        {
            for (int i = 0; i < 4; i++)
            {
                addParticipant(new Debris(x, y, 1));
            }
        }
        if (type == 1 || type == 2)
        {
            for (int i = 0; i < 6; i++)
            {
                addParticipant(new Debris(x, y, RANDOM.nextInt(20) + 10));
            }
        }
        if (type == 3)
        {
            for (int i = 0; i < 4; i++)
            {
                addParticipant(new Debris(x, y, 1));
            }
            for (int i = 0; i < 3; i++)
            {
                addParticipant(new Debris(x, y, RANDOM.nextInt(10) + 30));
            }
        }
    }
}
