package de.tuhh.ti5.swp.tm.concurrent;

import java.util.concurrent.CountDownLatch;

import de.tuhh.ti5.swp.tm.image.TMBWImage;
import de.tuhh.ti5.swp.tm.io.TMConfigReader;
import de.tuhh.ti5.swp.tm.io.TMResultWriter;

/**
 * 
 * @author Rustam Magomedov
 *
 */
public class TMMatchingTask implements Runnable {

	private TMBWImage image;
	private TMBWImage template;
	private TMResultWriter result;
	private int start;
	private int end;
	private CountDownLatch latch;

	/**
	 * @param image
	 *            bigger image that should be searched for a template
	 * @param template
	 *            template image that should be searched for in the bigger image
	 * @param result
	 *            stores results with positions
	 * @param start
	 *            start index to look from in this particular task
	 * @param end
	 *            end index where the search ends in this particular task
	 * @param latch
	 *            counter to ensure that all tasks are finished
	 */
	public TMMatchingTask(TMBWImage image, TMBWImage template, TMResultWriter result, int start,
			int end, CountDownLatch latch) {
		this.image = image;
		this.template = template;
		this.result = result;
		this.start = start;
		this.end = end;
		this.latch = latch;
	}

	@Override
	public void run() {

		System.out.println("... " + image.getName() + " [" + image.getWidth() + "x"
				+ image.getHeight() + "] => " + image.getSize());

		System.out.println("... " + template.getName() + " [" + template.getWidth() + "x"
				+ template.getHeight() + "] => " + template.getSize());

		double threshold = TMConfigReader.THRESHOLD_DEFAULT;
		String coordinates = TMConfigReader.COORDINATES_NOTFOUND;
		double diff;
		double sum;
		int offset = start;

		int imageWidth = image.getWidth();
		int templateWidth = template.getWidth();
		int imageHeight = image.getHeight();
		int templateHeight = template.getHeight();
		int lastColumn = imageWidth - templateWidth;
		int lastRow = imageHeight - templateHeight;

		int x;
		int y;
		int row;
		int col;

		String imageName = image.getName();
		int templateSize = template.getSize();
		int bound = image.getSize() - templateSize;

		while (offset < end && offset <= bound) {
			sum = 0;
			row = offset / imageWidth;
			col = offset % imageWidth;
			if (row > lastRow) {
				break;
			}
			if (col > lastColumn) {
				offset++;
				continue;
			}
			for (int i = 0; i < templateSize; i++) {
				x = i % templateWidth;
				y = i / templateWidth;
				diff = image.getBWPixel(offset + y * imageWidth + x) - template.getBWPixel(i);
				sum += Math.pow(diff, 2);
			}
			if (sum < threshold) {
				threshold = sum;
				coordinates = col + " " + row;
			}
			offset++;
		}
		if (threshold < result.getValue(imageName)) {
			result.setValue(imageName, threshold);
			result.setCoordinates(imageName, coordinates);
		}
		latch.countDown();
		System.out
				.println("... " + image.getName() + " from: " + start + " to: " + end + " matched");
	}
}