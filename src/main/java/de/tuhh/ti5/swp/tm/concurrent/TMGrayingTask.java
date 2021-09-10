package de.tuhh.ti5.swp.tm.concurrent;

import java.util.concurrent.CountDownLatch;

import de.tuhh.ti5.swp.tm.image.TMBWImage;
import de.tuhh.ti5.swp.tm.image.TMRGBImage;

/**
 * 
 * @author Aleksej Davletcurin
 *
 */
public class TMGrayingTask implements Runnable {

	private TMRGBImage rgb;
	private TMBWImage bw;
	private int start;
	private int end;
	private CountDownLatch latch;

	/**
	 * 
	 * @param rgb
	 *            RGB Bild
	 * @param bw
	 *            Graustufenbild
	 * @param start
	 *            Start des Tasks
	 * @param end
	 *            Ende des Tasks
	 * @param latch
	 *            Sicherstellung, dass der Task beendet wird
	 */
	public TMGrayingTask(TMRGBImage rgb, TMBWImage bw, int start, int end,
			CountDownLatch latch) {
		this.rgb = rgb;
		this.bw = bw;
		this.start = start;
		this.end = end;
		this.latch = latch;
	}

	@Override
	public void run() {
		for (int i = start; i < end; i++) {
			bw.setBWPixel(i, rgb.getBWPixel(i));
		}
		latch.countDown();
		System.out
				.println("... " + rgb.getName() + " from: " + start + " to: " + end + " converted");
	}
}
