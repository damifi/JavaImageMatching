package de.tuhh.ti5.swp.tm.mapreduce;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import de.tuhh.ti5.swp.tm.concurrent.TMBlurringTask;
import de.tuhh.ti5.swp.tm.concurrent.TMGrayingTask;
import de.tuhh.ti5.swp.tm.concurrent.TMMatchingTask;
import de.tuhh.ti5.swp.tm.concurrent.TMReadImageTask;
import de.tuhh.ti5.swp.tm.constraints.Dimension;
import de.tuhh.ti5.swp.tm.image.TMBWImage;
import de.tuhh.ti5.swp.tm.image.TMRGBImage;
import de.tuhh.ti5.swp.tm.io.TMConfigReader;
import de.tuhh.ti5.swp.tm.io.TMResultWriter;

/**
 * maps images to tasks.
 * 
 * @author Rustam Magomedov
 *
 */

public class TMMapper {

	private CountDownLatch latch;
	private ExecutorService executor;
	private int numOfTasks;
	private String[] imagePaths;
	private Dimension constraint;

	private int totalNumOfTasks;
	private int[] bounds;

	private static final int START_INDEX = 0;
	private static final int END_INDEX = 1;

	/**
	 * @param config
	 *            configuration object to get parameters from
	 */
	public TMMapper(TMConfigReader config) {
		numOfTasks = config.getNumberOfTasks();
		imagePaths = config.getImagePaths();
		constraint = config.getConstraint();
	}

	// /**
	// * @param images
	// * configuration object to get parameters from
	// * @param config
	// * configuration object to get parameters from
	// */
	// public TMMapper(List<TMBWImage> images, TMConfigReader config) {
	// this(config);
	//
	// totalNumOfTasks = numOfTasks * imagePaths.length;
	//
	// int start;
	// int end;
	// int size;
	// int taskUnit;
	//
	// for (TMBWImage image : images) {
	// size = image.getSize();
	// taskUnit = size / numOfTasks;
	// for (int i = 0; i < numOfTasks; i++) {
	// start = i * taskUnit;
	// if (i + 1 < numOfTasks) {
	// end = start + taskUnit;
	// } else {
	// end = size;
	// }
	// }
	// }
	//
	// }

	/**
	 * @param images
	 *            images to load from the hard drive
	 * @return ArrayList<TMRGBImage> loaded images
	 */
	public List<TMRGBImage> read(List<TMRGBImage> images, ExecutorService executor) {
		latch = new CountDownLatch(imagePaths.length);
		System.out.println("Einlesen: ");
		for (String path : imagePaths) {
			executor.execute(new TMReadImageTask(path, images, constraint, latch));
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			interruptedExceptionHandler("inside read");
		}
		return images;
	}

	/**
	 * @param images
	 *            rgb images to convert to black & white to
	 * @param bwImages
	 *            black & white images to convert to from rgb
	 * @return ArrayList<TMBWImage> list of black & white images
	 */
	public List<TMBWImage> convert(List<TMRGBImage> images, List<TMBWImage> bwImages,
			ExecutorService executor) {

		// ################################
		TMBWImage bwImage;
		// ################################

		int start;
		int end;
		int size;
		int taskUnit;
		latch = new CountDownLatch(numOfTasks * images.size());
		System.out.println("Grauumwandlung ...");
		for (TMRGBImage image : images) {
			size = image.getSize();
			taskUnit = size / numOfTasks;

			// ################################
			bwImage = new TMBWImage(image);
			// ################################

			System.out.println(image.getName());
			for (int i = 0; i < numOfTasks; i++) {
				start = i * taskUnit;
				if (i + 1 < numOfTasks) {
					end = start + taskUnit;
				} else {
					end = size;
				}
				executor.execute(new TMGrayingTask(image, bwImage, start, end, latch));
			}

			// ################################
			bwImages.add(bwImage);
			// ################################
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			interruptedExceptionHandler("inside convert method");
		}
		return bwImages;
	}

	/**
	 * @param images
	 *            black & white images to apply gaussian blur to
	 * @return ArrayList<TMBWImage> list of blurred images
	 */
	public List<TMBWImage> blur(List<TMBWImage> images, ExecutorService executor) {
		int start;
		int end;
		int size;
		int taskUnit;
		latch = new CountDownLatch(numOfTasks * images.size());
		System.out.println("Weichzeichnen ...");
		for (TMBWImage image : images) {
			size = image.getSize();
			taskUnit = size / numOfTasks;
			for (int i = 0; i < numOfTasks; i++) {
				start = i * taskUnit;
				if (i + 1 < numOfTasks) {
					end = start + taskUnit;
				} else {
					end = size;
				}
				executor.execute(new TMBlurringTask(image, start, end, latch));
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			interruptedExceptionHandler("inside blur method");
		}
		return images;
	}

	// List<TMBWImage> convert(List<TMBWImage> bwImages) + List<TMRGBImage> images
	// List<TMBWImage> match(List<TMBWImage> images) + TMBWImage template, TMResultWriter result
	// List<TMBWImage> blur(List<TMBWImage> images)

	/**
	 * @param images
	 *            black & white images to search for template
	 * @param template
	 *            imaged that is searched for
	 * @param result
	 *            result writer instance gives access to shared variable
	 * @return ArrayList<TMBWImage> list of matched images
	 */
	public List<TMBWImage> match(List<TMBWImage> images, TMBWImage template, TMResultWriter result,
			ExecutorService executor) {
		int start;
		int end;
		int size;
		int taskUnit;
		latch = new CountDownLatch(numOfTasks * images.size());
		System.out.println("Matching ...");
		for (TMBWImage image : images) {
			size = image.getSize();
			taskUnit = size / numOfTasks;
			for (int i = 0; i < numOfTasks; i++) {
				start = i * taskUnit;
				if (i + 1 < numOfTasks) {
					end = start + taskUnit;
				} else {
					end = size;
				}
				executor.execute(new TMMatchingTask(image, template, result, start, end, latch));
			}
		}
		try {
			latch.await();
		} catch (InterruptedException e) {
			interruptedExceptionHandler("inside match method");
		}
		return images;
	}

	/**
	 * @param images
	 *            black & white images to search for template
	 * @param task
	 *            runnable task, could be graying, blurring, matching
	 * @param step
	 *            console output for debugging
	 * @return ArrayList<TMBWImage> list of matched images
	 */

	// public List<TMBWImage> match(List<TMBWImage> images, Runnable task, String step) {
	// int start;
	// int end;
	// int size;
	// int taskUnit;
	// latch = new CountDownLatch(totalNumOfTasks);
	// System.out.println(step);
	// for (TMBWImage image : images) {
	// size = image.getSize();
	// taskUnit = size / numOfTasks;
	// for (int i = 0; i < numOfTasks; i++) {
	// start = i * taskUnit;
	// if (i + 1 < numOfTasks) {
	// end = start + taskUnit;
	// } else {
	// end = size;
	// }
	// executor.execute(task);
	// }
	// }
	// try {
	// latch.await();
	// } catch (InterruptedException e) {
	// interruptedExceptionHandler("inside match method");
	// }
	// return images;
	// }

	/**
	 * @param message
	 *            customized message for method recognition
	 */
	private static void interruptedExceptionHandler(String message) {
		System.out.println("Exception was called: " + message);
	}
}
