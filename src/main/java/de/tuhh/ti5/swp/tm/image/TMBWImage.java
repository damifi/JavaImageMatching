package de.tuhh.ti5.swp.tm.image;

/**
 * This class represents a black and white image. It inherits from TMImage.
 * 
 * @author Rustam Magomedov, Aleksej Davletcurin, Daniel Fischer
 *
 */

public class TMBWImage extends TMImage {

	private static final int CHANNELS = 1;

	/**
	 * @param proto
	 *            RGB image to use as prototype
	 */
	public TMBWImage(TMRGBImage proto) {
		super(proto.getName(), proto.getWidth(), proto.getHeight(), TMBWImage.CHANNELS);
	}

	/**
	 * Method to set a single pixel at the given index.
	 * 
	 * @param index
	 *            index to save pixel at
	 * @param pixel
	 *            pixel value to assign
	 */
	public void setBWPixel(int index, double pixel) {
		setPixel(index, new double[] { pixel });
	}

	/**
	 * Method to return a single black and white pixel.
	 * 
	 * @param index
	 *            index to which pixel is returned
	 */
	@Override
	public double getBWPixel(int index) {
		return getPixel(index)[0];
	}

	/**
	 * Method to return all black and white pixels in a double array.
	 * 
	 * @return double[] grey pixel values
	 */
	public double[] getBWPixels() {
		double[] pixels = new double[getSize()];
		for (int i = 0; i < getSize(); i++) {
			pixels[i] = getPixels()[i][0];
		}
		return pixels;
	}
}