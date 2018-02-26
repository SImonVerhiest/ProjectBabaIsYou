package gui;

import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import sokobanUtilities.*;

/**
* JLabel extension, shows one cell of a Sokoban's level grid
*
* @author 	HUYLENBROECK Florent
*/
class CellLabel extends JLabel
{
	private int side;

	public CellLabel(BufferedImage image, int x, int y, int side)
	{
		this.side=side;

		this.putClientProperty("column", x);
		this.putClientProperty("row", y);

		changeImage(image);
	}

	/**
	* Changes the icon of the JLabel
	*
	* @param image 	BufferedImage, the new icon
	*/
	protected void changeImage(BufferedImage image)
	{
		this.setIcon(new ImageIcon(SokobanUtilities.resize(image, side, side)));
	}
}