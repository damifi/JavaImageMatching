package de.tuhh.ti5.swp.tm.image;

import java.io.IOException;

import de.tuhh.ti5.swp.tm.constraints.Dimension;

/**
 * 
 * @author Rustam Magomedov, Aleksej Davletcurin, Daniel Fischer
 *
 */

public class TMRGBImage extends TMImage {

	private static final int CHANNELS = 3;
	private static final double RVALUE = 0.3;
	private static final double GVALUE = 0.59;
	private static final double BVALUE = 0.11;

	/**
	 * 
	 * @param path
	 *            image location on the hard drive
	 * @throws IOException
	 *             throws IOException during image read phase
	 */
	public TMRGBImage(String path) throws IOException {
		super(path, CHANNELS, null);
	}

	/**
	 * 
	 * @param path
	 *            image location on the hard drive
	 * @param constraint
	 *            minimal image dimensions allowed
	 * @throws IOException
	 *             throws IOException during image read phase
	 */
	public TMRGBImage(String path, Dimension constraint) throws IOException {
		super(path, CHANNELS, constraint);
	}

	@Override
	public double getBWPixel(int index) {
		double[] pixel = getPixel(index);
		return Math.round(pixel[0] * RVALUE + pixel[1] * GVALUE + pixel[2] * BVALUE);
	}
}