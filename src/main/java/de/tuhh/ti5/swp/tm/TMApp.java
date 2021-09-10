package de.tuhh.ti5.swp.tm;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.tuhh.ti5.swp.tm.constraints.Dimension;
import de.tuhh.ti5.swp.tm.image.TMBWImage;
import de.tuhh.ti5.swp.tm.image.TMRGBImage;
import de.tuhh.ti5.swp.tm.io.TMConfigReader;
import de.tuhh.ti5.swp.tm.io.TMResultWriter;
import de.tuhh.ti5.swp.tm.mapreduce.TMMapper;

/**
 * Hello world!
 */
public class TMApp {

	private static final int WINDOW_WIDTH = 640;
	private static final int WINDOW_HEIGHT = 480;

	/**
	 * This is the main.
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		TMConfigReader config = new TMConfigReader("res/testConf.properties");
		TMRGBImage rgbTemplate = null;
		// TMBWImage bwImage;
		TMBWImage bwTemplate;
		LinkedList<TMRGBImage> rgbImages = new LinkedList<>();
		LinkedList<TMBWImage> bwImages = new LinkedList<>();
		String templatePath = config.getTemplatePath();
		String outputPath = config.getOutputPath();
		config.setThresholdFactor(1);

		try {
			rgbTemplate = new TMRGBImage(templatePath);
		} catch (IOException e) {
			System.out.println(templatePath + " konnte nicht richtig gelesen werden!");
			e.printStackTrace();
			System.exit(1);
		}
		config.setConstraint(new Dimension(rgbTemplate.getWidth(), rgbTemplate.getHeight()));

		ExecutorService executor = Executors.newFixedThreadPool(config.getNumberOfThreads());

		TMMapper mapper = new TMMapper(config);

		mapper.read(rgbImages, executor);
		rgbImages.addFirst(rgbTemplate);
		mapper.convert(rgbImages, bwImages, executor);
		mapper.blur(bwImages, executor);

		bwTemplate = ((LinkedList<TMBWImage>) bwImages).removeFirst();
		TMResultWriter result = new TMResultWriter(config);
		mapper.match(bwImages, bwTemplate, result, executor);

		long stopTime = (System.currentTimeMillis() - startTime) / TMConfigReader.TIME_FACTOR;
		result.setTime(stopTime);

		executor.shutdown();

		result.write();
		System.out.println(stopTime);

		// EventQueue.invokeLater(new Runnable() {
		// @Override
		// public void run() {
		// window(bwTemplate);
		// }
		// });
	}

	// /**
	// * @param image
	// * gets image to test
	// */
	// public static void window(TMBWImage image) {
	// BufferedImage bufferedImage = new BufferedImage(image.getWidth(), image.getHeight(),
	// BufferedImage.TYPE_BYTE_GRAY);
	// WritableRaster raster = (WritableRaster) bufferedImage.getData();
	// raster.setPixels(0, 0, image.getWidth(), image.getHeight(), image.getBWPixels());
	// bufferedImage.setData(raster);
	//
	// JFrame frame = new JFrame();
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	// frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	// frame.setVisible(true);
	// frame.setLocationRelativeTo(null);
	// frame.setTitle("Image Test");
	// JLabel label = new JLabel();
	// label.setIcon(new ImageIcon(bufferedImage));
	// frame.add(label);
	// }
}