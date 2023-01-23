
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Testing code for region finding in PS-1.
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Winter 2014
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for DrawingGUI
 */
public class RegionsTest extends DrawingGUI {
    private BufferedImage image;

    /**
     * Test your RegionFinder by passing an image filename and a color to find.
     * @param name
     * @param targetColor
     */
    public RegionsTest(String name, RegionFinder finder, Color targetColor) {
        super(name, finder.getImage().getWidth(), finder.getImage().getHeight());

        // Do the region finding and recolor the image.
        finder.findRegions(targetColor);
        finder.recolorImage(Color.blue.getRGB(), 'p');
        image = finder.getRecoloredImage();

    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new RegionsTest("baker", new RegionFinder(loadImage("pictures/baker.jpg")), new Color(130, 100, 100));
                new RegionsTest("smiley", new RegionFinder(loadImage("pictures/smiley.png")), new Color(0, 0, 0));}
        });
    }
}
