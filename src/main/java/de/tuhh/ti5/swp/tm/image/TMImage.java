package de.tuhh.ti5.swp.tm.image;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import de.tuhh.ti5.swp.tm.constraints.Dimension;

/**
 * 
 * @author Rustam Magomedov, Aleksej Davletcurin, Daniel Fischer
 *
 */

public abstract class TMImage {

	private String name;
	private int width;
	private int height;
	private int size;
	private int channels;
	private double[][] pixels;

	/**
	 * Constructor of TMImage.
	 * 
	 * @param path
	 *            path image location on hard disk
	 * @param width
	 *            image width
	 * @param height
	 *            image height
	 * @param channels
	 *            number of image channels
	 */
	public TMImage(String path, int width, int height, int channels) {
		this.name = path;
		this.width = width;
		this.height = height;
		this.size = width * height;
		this.channels = channels;
		this.pixels = new double[size][channels];
	}

	/**
	 * Constructor of TMImage. Takes a path to the location of an Image and saves its information
	 * into a workable format.
	 * 
	 * @param path
	 *            path image location on hard disk
	 * @param channels
	 *            number of channels, e.g red, green, blue & alpha
	 * @param constraint
	 *            constraint object to check whether the template is bigger or not
	 * @throws IOException
	 *             can be thrown during the image read
	 */
	public TMImage(String path, int channels, Dimension constraint) throws IOException {
		BufferedImage image = ImageIO.read(new File(path));
		Raster raster;
		boolean tooSmall = false;

		name = path;
		width = image.getWidth();
		height = image.getHeight();
		size = width * height;

		if (constraint != null) {
			tooSmall = (width <= constraint.getWidth() || height <= constraint.getHeight());
			System.out.println("###" + path + " is too small");
		}

		if (!tooSmall) {
			raster = image.getRaster();
			pixels = new double[size][channels];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					raster.getPixel(x, y, pixels[y * width + x]);
				}
			}
		}
	}

	/**
	 * Returns the name/path of the image.
	 * 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the width of the image.
	 * 
	 * @return int width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the height of the image.
	 * 
	 * @return int height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the size of the image.
	 * 
	 * @return int size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns the pixels of the image.
	 * 
	 * @return double[][] pixel values
	 */
	public double[][] getPixels() {
		return pixels;
	}

	/**
	 * Returns a singe pixel at the given index.
	 * 
	 * @param index
	 *            index to access pixel array
	 * @return double[] pixel as a double array
	 */
	public double[] getPixel(int index) {
		return pixels[index];
	}

	/**
	 * Takes an index and a pixel and stores it at the given index.
	 * 
	 * @param index
	 *            index to access pixel array
	 * @param pixel
	 *            new pixel value to assign to
	 */
	public void setPixel(int index, double[] pixel) {
		pixels[index] = new double[channels];
		for (int channel = 0; channel < channels; channel++) {
			pixels[index][channel] = pixel[channel];
		}
	}

	/**
	 * Abstract method to override.
	 * 
	 * @param index
	 *            index to get pixel from
	 * @return double pixel value
	 */
	public abstract double getBWPixel(int index);
}