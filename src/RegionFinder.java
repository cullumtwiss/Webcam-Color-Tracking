import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 * @author Will Hodgson
 */
public class RegionFinder {
	private static final int maxColorDiff = 27;                // how similar a pixel color must be to the target color, to belong to a region
	private static final int minRegion = 50;                // how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;            // a region is a list of points
	// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}
	private ArrayList<ArrayList<Point>> largestCollection;

	/**
	 * Tests whether the two colors are "similar enough," i.e. the absolute value of the distance between each color
	 * channel is less than the threshold.
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// Absolute value of distance between colors
		return ((Math.abs(c1.getRed() - c2.getRed())) < maxColorDiff)
				&& ((Math.abs(c1.getGreen() - c2.getGreen())) < maxColorDiff)
				&& ((Math.abs(c1.getBlue() - c2.getBlue())) < maxColorDiff);
	}

	/**
	 * Sets regions to the flood-fill regions in the image, similar enough to the trackColor.
	 */
	public void findRegions(Color targetColor) {
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// loop through each pixel in image to find one that matches targetColor and hasn't been visited
		regions = new ArrayList<ArrayList<Point>>();
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				Color currColor = new Color(image.getRGB(x, y));
				if (visited.getRGB(x, y) == 0 && colorMatch(currColor, targetColor)) {
					ArrayList<Point> toVisit = new ArrayList<Point>();    // create a new list including that point
					Point point = new Point(x, y);
					toVisit.add(point);
					ArrayList<Point> newRegion = new ArrayList<Point>(); // create a new list tracking the region

					// flood fill
					while (!toVisit.isEmpty()) {            // As long as there's some pixel that needs to be visited
						Point curr = toVisit.remove(0);
						;        // Get one to visit, removes from list
						if (visited.getRGB((int) curr.getX(), (int) curr.getY()) == 0) {
							visited.setRGB((int) curr.getX(), (int) curr.getY(), 1);    // Mark it as visited
							newRegion.add(curr);                // Add it to the region
							for (int i = Math.max((int) curr.getX() - 1, 0); i <= Math.min(curr.getX() + 1, image.getWidth() - 1); i++) {   // search for neighbors of targetColor
								for (int j = Math.max((int) curr.getY() - 1, 0); j <= Math.min(curr.getY() + 1, image.getHeight() - 1); j++) {
									Color neighborColor = new Color(image.getRGB(i, j));
									if (colorMatch(neighborColor, targetColor) && visited.getRGB(i, j) != 1) {    // If the neighbor is of the correct color
										toVisit.add(new Point(i, j));    // add it to the list of pixels to be visited

									}
								}

							}
						}
					}
					if (newRegion.size() >= minRegion) {    // If the region is big enough to be worth keeping, do so
						regions.add(newRegion);
					}


				}
			}
		}
	}


	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		ArrayList<Point> largest = new ArrayList<Point>();
		for (ArrayList<Point> region : regions) {
			if (region.size() > largest.size()) {
				largest = region;
			}
		}
		return largest;
	}
	//we will need this method to clear the painting when we hit 'c'

	/**
	 * clears the collection of the largest found regions
	 */
	public void clearLargestCollection() {
		largestCollection = new ArrayList<ArrayList<Point>>();
	}

	/**
	 * Sets recoloredImage to be a copy of image,
	 * but with each region a uniform random color,
	 * so we can see where they are
	 */
	public void recolorImage(int paintColor, char displayMode) {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(), image.copyData(null), image.getColorModel().isAlphaPremultiplied(), null);
		// Now recolor the regions in it
		//commented out the random colors from part one
		//for (ArrayList<Point> region : regions){
		//int randColor = (int) (Math.random()*16777216);

		//any time we recolor the image, we add the current largest region to the list of largest regions we've found
		largestCollection.add(largestRegion());
		if (displayMode == 'r') {
			//if display mode is r, just fill the largest region
			for (Point point : largestRegion()) {
				recoloredImage.setRGB(point.x, point.y, paintColor);
				//I don't want to keep painting while in r mode, so clear largestCollection
				clearLargestCollection();
				//}
			}
		} else if (displayMode == 'p') {
			//if display mode is p, color the largest region plus all the largest regions we found since clicking
			System.out.println(largestCollection.size());
			for (ArrayList<Point> region : largestCollection) {
				for(Point p : region) {
					recoloredImage.setRGB(p.x, p.y, paintColor);
					}
				}

			}

	}
}
