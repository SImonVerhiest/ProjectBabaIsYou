package gui;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import sokobanUtilities.*;

/**
* Abstract class, JPanel extension that resizes the background image to the dimension of the panel
*
* @author 	HUYLENBROECK Florent
*/
abstract class ResizedBGPanel extends JPanel
{
	public SokobanFrame parent;

	public double width;
	public double height;

	public String path = SokobanUtilities.getPathToCode();

	public ResizedBGPanel(SokobanFrame parent)
	{
		this.parent=parent;

		width = parent.getContentPane().getSize().width;
		height = parent.getContentPane().getSize().height;
	}

	protected void paintComponent(Graphics g, String imagePath)
	{
		super.paintComponent(g);

		BufferedImage image=null;

		try
		{
			image = ImageIO.read(new File(imagePath));
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(image!=null)
			g.drawImage(SokobanUtilities.resize(image, (int)width, (int)height), 0, 0, null);
    }
}