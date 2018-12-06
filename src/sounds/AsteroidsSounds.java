package sounds;

import java.io.BufferedInputStream;
import java.io.IOException;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.Timer;
import asteroids.game.ParticipantCountdownTimer;
import static asteroids.game.Constants.*;
public class AsteroidsSounds
{
    private Clip fire;

    private Clip smallSaucer;

    private Clip largeSaucer;

    private Clip largeAsteroidExplosion;

    private Clip mediumAsteroidExplosion;

    private Clip smallAsteroidExplosion;

    private Clip alienShipExplosion;
    
    private Clip shipExplosion;

    private Clip thrust;
    
    private Clip beat1;
    private Clip beat2;
    
    private Timer timer;

    public AsteroidsSounds ()
    {
        fire = createClip("/sounds/fire.wav");
        smallSaucer = createClip("/sounds/saucerSmall.wav");
        largeSaucer = createClip("/sounds/saucerBig.wav");
        largeAsteroidExplosion = createClip("/sounds/bangLarge.wav");
        mediumAsteroidExplosion = createClip("/sounds/bangMedium.wav");
        smallAsteroidExplosion = createClip("/sounds/bangSmall.wav");
        alienShipExplosion = createClip("/sounds/bangAlienShip.wav");
        shipExplosion = createClip("/sounds/bangShip.wav");
        thrust = createClip("/sounds/thrust.wav");
        beat1 = createClip("/sounds/beat1.wav");
        beat2 = createClip("/sounds/beat2.wav");
    }

    public Clip createClip (String soundFile)
    {
        // Opening the sound file this way will work no matter how the
        // project is exported. The only restriction is that the
        // sound files must be stored in a package.
        try (BufferedInputStream sound = new BufferedInputStream(getClass().getResourceAsStream(soundFile)))
        {
            // Create and return a Clip that will play a sound file. There are
            // various reasons that the creation attempt could fail. If it
            // fails, return null.
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(sound));
            return clip;
        }
        catch (LineUnavailableException e)
        {
            return null;
        }
        catch (IOException e)
        {
            return null;
        }
        catch (UnsupportedAudioFileException e)
        {
            return null;
        }
    }

    public void playFireSound ()
    {
        if (fire.isRunning())
        {
            fire.stop();
        }
        fire.setFramePosition(0);
        fire.start();
    }

    public void playSmallSaucer ()
    {
        smallSaucer.setFramePosition(0);
        smallSaucer.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stopSmallSaucer ()
    {
        smallSaucer.stop();
    }
    
    public void playLargeSaucer ()
    {
        largeSaucer.setFramePosition(0);
        largeSaucer.loop(Clip.LOOP_CONTINUOUSLY);
    }
    
    public void stopLargeSaucer ()
    {
        largeSaucer.stop();
    }
    
    public void playThrust ()
    {
        if (!thrust.isRunning())
        {
            thrust.setFramePosition(0);
            thrust.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stopThrust ()
    {
        thrust.stop();
    }
    
    public void playLargeAsteroidExplosion ()
    {
        if (largeAsteroidExplosion.isRunning())
        {
            largeAsteroidExplosion.stop();
        }
        largeAsteroidExplosion.setFramePosition(0);
        largeAsteroidExplosion.start();
    }
    
    public void playMediumAsteroidExplosion ()
    {
        if (mediumAsteroidExplosion.isRunning())
        {
            mediumAsteroidExplosion.stop();
        }
        mediumAsteroidExplosion.setFramePosition(0);
        mediumAsteroidExplosion.start();
    }
    
    public void playSmallAsteroidExplosion ()
    {
        if (smallAsteroidExplosion.isRunning())
        {
            smallAsteroidExplosion.stop();
        }
        smallAsteroidExplosion.setFramePosition(0);
        smallAsteroidExplosion.start();
    }
    
    public void playAlienShipExplosion ()
    {
        if (alienShipExplosion.isRunning())
        {
            alienShipExplosion.stop();
        }
        alienShipExplosion.setFramePosition(0);
        alienShipExplosion.start();
    }
    
    public void playBeat1()
    {
        beat1.setFramePosition(0);
        beat1.start();
    }
    
    public void playBeat2 ()
    {
        beat2.setFramePosition(0);
        beat2.start();
    }
}
