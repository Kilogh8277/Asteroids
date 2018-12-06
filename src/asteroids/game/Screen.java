package asteroids.game;

import static asteroids.game.Constants.*;
import java.awt.*;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import asteroids.participants.Ship;

/**
 * The area of the display in which the game takes place.
 */
@SuppressWarnings("serial")
public class Screen extends JPanel
{
    private static int BORDER = 5;

    private static int FONT_SIZE = 30;
    /** Legend that is displayed across the screen */
    private String legend;

    /** Game controller */
    private Controller controller;

    private String score;

    private String level;

    private int lives;

    private Font bigFont = new Font("SansSerif", 0, 120);

    private Font littleFont = new Font("SansSerif", 0, 30);

    private Ship shipOutlines;

    /**
     * Creates an empty screen
     */
    public Screen (Controller controller)
    {
        this.controller = controller;
        legend = "";
        level = "1";
        score = "0";
        lives = 3;
        setPreferredSize(new Dimension(SIZE, SIZE));
        setMinimumSize(new Dimension(SIZE, SIZE));
        setBackground(Color.BLACK);
        setForeground(Color.WHITE);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 120));
        setFocusable(true);

        shipOutlines = new Ship(0, 0, -Math.PI / 2);
    }

    /**
     * Set the legend
     */
    public void setLegend (String legend)
    {
        this.legend = legend;
    }

    public void setLives (int lives)
    {
        this.lives = lives;
    }

    public void setLevel (int level)
    {
        this.level = level + "";
    }

    public void setScore (int score)
    {
        this.score = score + "";
    }

    /**
     * Paint the participants onto this panel
     */
    @Override
    public void paintComponent (Graphics graphics)
    {
        // Use better resolution
        Graphics2D g = (Graphics2D) graphics;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Do the default painting
        super.paintComponent(g);

        // Draw each participant in its proper place
        Iterator<Participant> iter = controller.getParticipants();
        while (iter.hasNext())
        {
            iter.next().draw(g);
        }

        // Draw the legend across the middle of the panel
        int size = g.getFontMetrics().stringWidth(legend);
        g.drawString(legend, (SIZE - size) / 2, SIZE / 2);

        g.setFont(littleFont);
        g.drawString(level, 700 - g.getFontMetrics().stringWidth(level), 50);

        g.drawString(score, 50, 50);
        FontMetrics metrics = graphics.getFontMetrics(littleFont);

        int advance = metrics.stringWidth(score);
        double center = (50.0 + (advance / 2.0));

        double start = center - 15 * (lives - 1);

        for (int i = 0; i < lives; i++)
        {
            shipOutlines.setPosition(start, 75);
            shipOutlines.move();
            shipOutlines.draw(g);

            start = start + 30;
        }
    }
}
