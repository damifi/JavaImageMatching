package de.tuhh.ti5.swp.tm.io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Class to create an output file to write the results of the template matching algorithm into a the
 * file.
 * 
 * @author Daniel Fischer
 *
 */
public class TMResultWriter {

	private PrintWriter writer;
	private String outputPath;
	private String[] imagePaths;
	private HashMap<String, String> coordinates; // hashmap to store coordinates as string
	private HashMap<String, Double> values; // hashmap to store value from template matching
	private long time;

	/**
	 * Constructor of the TMResultWriter. Adds entries to the hash maps, depending on the
	 * information of the configuration file.
	 * 
	 * @param config
	 *            TMConfigReader object, that contains configuration settings
	 */
	public TMResultWriter(TMConfigReader config) {
		outputPath = config.getOutputPath();
		imagePaths = config.getImagePaths();
		coordinates = new HashMap<>();
		values = new HashMap<>();

		double threshold = config.getThreshold();
		for (String imagePath : imagePaths) {
			coordinates.put(imagePath, TMConfigReader.COORDINATES_NOTFOUND);
			values.put(imagePath, threshold);
		}
	}

	/**
	 * Method to set the time needed to complete all tasks.
	 * 
	 * @param time
	 *            time that was needed to complete all tasks
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Function to add or replace coordinates found by the template matching algorithm.
	 * 
	 * @param key
	 *            key to access hash map
	 * @param value
	 *            coordinates of the smallest value found
	 * 
	 */
	public void setCoordinates(String key, String value) {
		synchronized (coordinates) {
			coordinates.put(key, value);
		}
	}

	/**
	 * Function to return coordinates from the hash map.
	 * 
	 * @param key
	 *            key to access the hash map
	 * @return String the coordinates of the smallest value found
	 */
	public String getCoordinates(String key) {
		synchronized (coordinates) {
			return coordinates.get(key);
		}
	}

	/**
	 * Function to add or replace values found by the template matching algorithm.
	 * 
	 * @param key
	 *            key to access the hash map
	 * @param value
	 *            coordinates of the smallest value found
	 * 
	 */
	public void setValue(String key, double value) {
		synchronized (values) {
			values.put(key, value);
		}
	}

	/**
	 * Function to return values from the hash map.
	 * 
	 * @param key
	 *            key to access the hash map
	 * @return double values computed by the template matching algorithm for the images
	 */
	public double getValue(String key) {
		synchronized (values) {
			return values.get(key);
		}
	}

	/**
	 * Creates the output file at the location outputPath and writes the results from the template
	 * matching process into that file.
	 * 
	 */
	public void write() {
		try {
			writer = new PrintWriter(outputPath);
			for (String imagePath : imagePaths) {
				System.out.println("----" + imagePath + " " + coordinates.get(imagePath));
				writer.println(imagePath + " " + coordinates.get(imagePath));
			}
			writer.println(String.valueOf(time));
			writer.close();
		} catch (FileNotFoundException e) {
			System.out.println("Failed to write file");
			e.printStackTrace();
			System.exit(1);
		}

	}
}
