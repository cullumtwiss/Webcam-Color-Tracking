import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */
public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting = null;			// the resulting masterpiece
	//brush mode will store what type of brush we want, defaults to x, meaning the brush is blue
	private char brushMode = 'x';

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		//I wanted the target color to reset if we clear the painting so that it doesn't keep tracking the same color
		targetColor = null;
		//if we don't clear largestCollection, all the previously found largest regions will instantly get painted
		finder.clearLargestCollection();
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		//only do something if there's a target color
		if (this.targetColor != null ) processImage(this.targetColor);
		if(targetColor != null) {
			//if there is a target color, paint
			g.drawImage(painting,0,0, null);
		} else {
			//if there's not a target color just show the webcam
			g.drawImage(image,0,0, null);
		}
	}

	/**
	 * Webcam method, here finding regions and updating the painting.
	 */
	//@Override
	public void processImage(Color targetColor) {
		//x makes brush blue
		if(brushMode == 'x') {
			paintColor = Color.blue;
		} //y makes brush the color that you clicked
		else if(brushMode == 'y'){
			paintColor = targetColor;
		} else {
			//z makes the brush color randomly change
			paintColor = new Color((int) (Math.random()*16777216));
		}
		//first we have to set the image
			finder.setImage(this.image);
		//then we find the regions, recolor, and update painting
			finder.findRegions(targetColor);
			finder.recolorImage(paintColor.getRGB(), displayMode);
			painting = finder.getRecoloredImage();

	}

	/**
	 * Overrides the DrawingGUI method to set the track color.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		//once the mouse is clicked we set targetColor
		if (image != null) {
			targetColor = new Color(image.getRGB(x, y));

		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "painting.png", "png");
		}
		//These are the brushes I added
		else if(k == 'x' || k == 'y' || k == 'z') {
			brushMode = k;
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
