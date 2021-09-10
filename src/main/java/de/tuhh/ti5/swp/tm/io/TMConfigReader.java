package de.tuhh.ti5.swp.tm.io;

import java.io.FileInputStream;
import java.util.Properties;

import de.tuhh.ti5.swp.tm.constraints.Dimension;

/**
 * @author Aleksej Davletcurin
 */

public class TMConfigReader {

	/**
	 * 
	 * Konfigurationsdatei wird eingelesen. Analog zu der letzten Aufgabe wird Propertyklasse
	 * benutzt
	 */

	private Properties props;
	private int numberOfThreads;
	private int numberOfTasks;
	private String templatePath;
	private String outputPath;
	private String[] imagePaths;
	private Dimension constraint;
	private double thresholdFactor;

	/**
	 * @param COORDINATES_NOTFOUND
	 *            Temlate im Bild nicht gefunden
	 * @param COORDINATES_NOTFITTING
	 *            Template ist groesser als das Bild
	 * @param THRESHOLD_DEFAULT
	 *            Schwellenwert, mit dem beide Bilder Funktionieren
	 * @param TIME_FACTOR
	 *            Umrechnungsfaktor fuer Sekunden
	 */
	public static final String COORDINATES_NOTFOUND = "-1 -1";
	public static final String COORDINATES_NOTFITTING = "-2 -2";
	public static final int THRESHOLD_DEFAULT = 2000000;
	public static final long TIME_FACTOR = 1000;

	/**
	 * 
	 * @param separator
	 *            trennt die Beispielbilder
	 * @param path
	 *            Pfad zu der Konfigurationsdatei
	 */
	public TMConfigReader(String path, String separator) {
		props = new Properties();
		try {
			props.load(new FileInputStream(path));
			templatePath = props.getProperty("templateFileName");
			numberOfThreads = Integer.parseInt(props.getProperty("threads"));
			numberOfTasks = Integer.parseInt(props.getProperty("tasksPerProcessingStep"));
			outputPath = props.getProperty("output");
			imagePaths = props.getProperty("imageFileNames").split(separator);
		} catch (Exception e) {
			System.out.println("Die Konfigurationsdatei konnte nicht gelesen werden!");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * 
	 * @param path
	 *            Pfad zu der Konfigurationsdatei
	 * 
	 */
	public TMConfigReader(String path) {
		this(path, ",");
	}

	/**
	 * 
	 * @return int
	 */
	public int getNumberOfTasks() {
		return numberOfTasks;
	}

	/**
	 * @return int
	 */
	public int getNumberOfThreads() {
		return numberOfThreads;
	}

	/**
	 * @return int
	 */
	public String getTemplatePath() {
		return templatePath;
	}

	/**
	 * @return String
	 */
	public String getOutputPath() {
		return outputPath;
	}

	/**
	 * @return String[]
	 */
	public String[] getImagePaths() {
		return imagePaths;
	}

	/**
	 * @param constraint
	 *            dimension constraint can be optional
	 */
	public void setConstraint(Dimension constraint) {
		this.constraint = constraint;
	}

	/**
	 * @return Dimension constraint
	 */
	public Dimension getConstraint() {
		return constraint;
	}

	/**
	 * @param factor
	 *            Faktor mit dem man die Breite und Hoehe multipliziert um den Schwellenwert zu
	 *            erhalten
	 */
	public void setThresholdFactor(double factor) {
		thresholdFactor = factor;
	}

	/**
	 * @return double threshold
	 */
	public double getThreshold() {
		return thresholdFactor * THRESHOLD_DEFAULT;
	}
}
