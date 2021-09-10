package de.tuhh.ti5.swp.tm.concurrent;

import java.util.concurrent.CountDownLatch;

import de.tuhh.ti5.swp.tm.image.TMBWImage;

/**
 * Class to blur an Image as tasks.
 * 
 * @author Daniel Fischer
 *
 */
public class TMBlurringTask implements Runnable {
	private static final int GAUSS_KERNEL_SUM = 16;
	private static final int GAUSS_KERNEL_TOPLEFT = 1;
	private static final int GAUSS_KERNEL_TOP = 2;
	private static final int GAUSS_KERNEL_TOPRIGHT = 1;
	private static final int GAUSS_KERNEL_LEFT = 2;
	private static final int GAUSS_KERNEL_BOTTOMLEFT = 1;
	private static final int GAUSS_KERNEL_BOTTOM = 2;
	private static final int GAUSS_KERNEL_BOTTOMRIGHT = 1;
	private static final int GAUSS_KERNEL_RIGHT = 2;
	private static final int GAUSS_KERNEL_MIDDLE = 4;
	private TMBWImage image;
	private int start;
	private int end;
	private CountDownLatch latch;

	/**
	 * Constructor of the task to blur an Image.
	 * 
	 * @param image
	 *            black and white image that will be blurred
	 * @param start
	 *            index at which task starts blurring
	 * @param end
	 *            index at which task ends blurring
	 * @param latch
	 *            latch to count down
	 */
	public TMBlurringTask(TMBWImage image, int start, int end, CountDownLatch latch) {
		this.image = image;
		this.start = start;
		this.end = end;
		this.latch = latch;
	}

	@Override
	public void run() {
		int x;
		int y;
		int w = image.getWidth();
		for (int i = start; i < end; i++) {
			x = i % w; // calculate x coordinate of the image
			y = i / w; // calculate y coordinate of the image
			image.setBWPixel(i, gaussianBlur(image, x, y));
		}
		latch.countDown();
		System.out
				.println("... " + image.getName() + " from: " + start + " to: " + end + " blurred");
	}

	/**
	 * Calculates a single blurred pixel. The pixel is calculated by calling a function to calculate
	 * the blurred value of the current pixel, depending on the location of the current pixel.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @return double returns a blurred pixel
	 */
	public static double gaussianBlur(TMBWImage image, int x, int y) {
		int w = image.getWidth();
		int h = image.getHeight();
		int index = y * w + x;
		if (index > image.getSize() || index < 0) {
			System.out.println("Error during blurring. x or y are out of bounds");
			throw new IndexOutOfBoundsException("x: " + x + " y: " + y + "is out of bounds.");
		}

		if (y == 0) { // first row
			if (x == 0) { // first column
				return gaussianTopLeft(image, index);
			} else if ((x + 1) == w) { // last column
				return gaussianTopRight(image, index);
			} else {
				return gaussianTop(image, index);
			}
		} else if (y + 1 == h) { // last row
			if (x == 0) { // first column
				return gaussianBottomLeft(image, index);
			} else if ((x + 1) == w) { // last column
				return gaussianBottomRight(image, index);
			} else {
				return gaussianBottom(image, index);
			}
		} else {
			if (x == 0) { // first column
				return gaussianLeft(image, index);
			} else if ((x + 1) == w) { // last column
				return gaussianRight(image, index);
			} else {
				return gaussianPixel(image, index);
			}
		}
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the top row of the image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns a blurred pixel
	 */
	public static double gaussianTop(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_TOP);
		// ----------------------
		sum += image.getBWPixel(i + w) * GAUSS_KERNEL_BOTTOM; // bottom
		sum += image.getBWPixel(i - 1) * GAUSS_KERNEL_LEFT + GAUSS_KERNEL_TOPLEFT; // left
		sum += image.getBWPixel(i + 1) * GAUSS_KERNEL_RIGHT + GAUSS_KERNEL_TOPRIGHT; // right
		// ----------------------
		sum += image.getBWPixel(i + w - 1); // bottom left
		sum += image.getBWPixel(i + w + 1); // bottom right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the right column of the
	 * image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianRight(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_RIGHT);
		// ----------------------
		sum += image.getBWPixel(i - w) * (GAUSS_KERNEL_TOP + GAUSS_KERNEL_TOPRIGHT); // top
		sum += image.getBWPixel(i + w) * (GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMRIGHT); // bottom
		sum += image.getBWPixel(i - 1) * GAUSS_KERNEL_LEFT; // left
		// ----------------------
		sum += image.getBWPixel(i - w - 1); // top left
		sum += image.getBWPixel(i + w - 1); // bottom left
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the bottom row of the image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianBottom(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_BOTTOM);
		// ----------------------
		sum += image.getBWPixel(i - w) * GAUSS_KERNEL_TOP; // top
		sum += image.getBWPixel(i - 1) * (GAUSS_KERNEL_LEFT + GAUSS_KERNEL_BOTTOMLEFT); // left
		sum += image.getBWPixel(i + 1) * (GAUSS_KERNEL_RIGHT + GAUSS_KERNEL_BOTTOMRIGHT); // right
		// ----------------------
		sum += image.getBWPixel(i - w - 1); // top left
		sum += image.getBWPixel(i - w + 1); // top right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the left column of the
	 * image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianLeft(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_LEFT);
		// ----------------------
		sum += image.getBWPixel(i - w) * (GAUSS_KERNEL_TOP + GAUSS_KERNEL_TOPLEFT); // top
		sum += image.getBWPixel(i + w) * (GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMLEFT); // bottom
		sum += image.getBWPixel(i + 1) * GAUSS_KERNEL_RIGHT; // right
		// ----------------------
		sum += image.getBWPixel(i - w + 1); // top right
		sum += image.getBWPixel(i + w + 1); // bottom right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the top left corner of the
	 * image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianTopLeft(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_LEFT
				+ GAUSS_KERNEL_TOP + GAUSS_KERNEL_TOPLEFT);
		// ----------------------
		sum += image.getBWPixel(i + w) * (GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMLEFT); // bottom
		sum += image.getBWPixel(i + 1) * (GAUSS_KERNEL_RIGHT + GAUSS_KERNEL_TOPRIGHT); // right
		// ----------------------
		sum += image.getBWPixel(i + w + 1); // bottom right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the top right corner of the
	 * image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianTopRight(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_TOP
				+ GAUSS_KERNEL_RIGHT + GAUSS_KERNEL_TOPRIGHT);
		// ----------------------
		sum += image.getBWPixel(i + w) * (GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMRIGHT); // bottom
		sum += image.getBWPixel(i - 1) * (GAUSS_KERNEL_LEFT + GAUSS_KERNEL_TOPLEFT); // left
		// ----------------------
		sum += image.getBWPixel(i + w - 1); // bottom left
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the bottom left corner of
	 * the image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianBottomLeft(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_LEFT
				+ GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMLEFT);
		// ----------------------
		sum += image.getBWPixel(i - w) * (GAUSS_KERNEL_TOP + GAUSS_KERNEL_TOPLEFT); // top
		sum += image.getBWPixel(i + 1) * (GAUSS_KERNEL_BOTTOM + GAUSS_KERNEL_BOTTOMRIGHT); // right
		// ----------------------
		sum += image.getBWPixel(i - w + 1); // top right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is in the bottom right corner of
	 * the image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianBottomRight(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * (GAUSS_KERNEL_MIDDLE + GAUSS_KERNEL_BOTTOM
				+ GAUSS_KERNEL_RIGHT + GAUSS_KERNEL_BOTTOMRIGHT);
		// ----------------------
		sum += image.getBWPixel(i - w) * (GAUSS_KERNEL_TOP + GAUSS_KERNEL_TOPRIGHT); // top
		sum += image.getBWPixel(i - 1) * (GAUSS_KERNEL_LEFT + GAUSS_KERNEL_BOTTOMLEFT); // left
		// ----------------------
		sum += image.getBWPixel(i - w - 1); // top left
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}

	/**
	 * Calculates the blurred pixel at a given index if the pixel is not the in the border of the
	 * image.
	 * 
	 * @param image
	 *            image that will be blurred
	 * @param index
	 *            index of the pixel that will be blurred
	 * @return double returns the blurred pixel
	 */
	public static double gaussianPixel(TMBWImage image, int index) {
		int i = index;
		int w = image.getWidth();
		double sum = image.getBWPixel(i) * GAUSS_KERNEL_MIDDLE;
		// ----------------------
		sum += image.getBWPixel(i - w) * GAUSS_KERNEL_TOP; // top
		sum += image.getBWPixel(i + w) * GAUSS_KERNEL_BOTTOM; // bottom
		sum += image.getBWPixel(i - 1) * GAUSS_KERNEL_LEFT; // left
		sum += image.getBWPixel(i + 1) * GAUSS_KERNEL_RIGHT; // right
		// ----------------------
		sum += image.getBWPixel(i - w - 1); // top left
		sum += image.getBWPixel(i - w + 1); // top right
		sum += image.getBWPixel(i + w - 1); // bottom left
		sum += image.getBWPixel(i + w + 1); // bottom right
		// ----------------------
		return sum / GAUSS_KERNEL_SUM;
	}
}