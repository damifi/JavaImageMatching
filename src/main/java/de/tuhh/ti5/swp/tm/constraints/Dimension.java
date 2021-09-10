package de.tuhh.ti5.swp.tm.constraints;

/**
 * 
 * @author Rustam Magomedov, Aleksej Davletcurin, Daniel Fischer
 *
 */
public class Dimension {

	private int width;
	private int height;

	/**
	 * @param width
	 *            width to constraint to
	 * @param height
	 *            height to constraint to
	 */
	public Dimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * @return int
	 */
	public int getHeight() {
		return height;
	}
}
