package de.tuhh.ti5.swp.tm.concurrent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import de.tuhh.ti5.swp.tm.constraints.Dimension;
import de.tuhh.ti5.swp.tm.image.TMRGBImage;

/**
 * 
 * @author Aleksej Davletcurin
 *
 */
public class TMReadImageTask implements Runnable {

	private String path;
	private List<TMRGBImage> images;
	private Dimension constraint;
	private CountDownLatch latch;

	/**
	 * 
	 * @param path
	 *            Pfad zu dem Bild
	 * @param constraint
	 *            Minimal erlaubte Dimension
	 * @param images
	 *            Array von RGB Images
	 * @param latch
	 *            Sicherstellung, dass alle Tasks beendet sind
	 */
	public TMReadImageTask(String path, List<TMRGBImage> images, Dimension constraint,
			CountDownLatch latch) {
		this.path = path;
		this.images = images;
		this.latch = latch;
	}

	@Override
	public void run() {
		TMRGBImage image = null;
		try {
			image = new TMRGBImage(path, constraint);
		} catch (IOException e) {
			System.out.println(path + " konnte nicht richtig gelesen werden!");
			e.printStackTrace();
			System.exit(1);
		}
		synchronized (images) {
			images.add(image);
		}
		latch.countDown();
		System.out.println("************************************************");
		System.out.println("... " + path + " loaded");
		System.out.println("************************************************");
	}
}
