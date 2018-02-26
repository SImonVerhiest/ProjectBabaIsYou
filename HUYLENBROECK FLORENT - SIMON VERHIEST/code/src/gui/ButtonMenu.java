package gui;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
* ResizeRGBPanel extension that creates a menu based on a list of JButton
*
* @author 	HUYLENROECK Florent
*/
class ButtonMenu extends ResizedBGPanel
{
	private String imagePath;

	private JPanel buttonPanel;

	private int nButton;
	private Dimension buttonDimension;

	/**
	* Constructor
	*
	* @param parent 	SokobanFrame that is containing this panel
	* @param imagePath 	String, absolute path to the background image
	* @param buttons 	ArrayList of JButton to add to the menu
	*/
	public ButtonMenu(SokobanFrame parent, String imagePath, ArrayList<JButton> buttons)
	{
		super(parent);

		this.imagePath = imagePath;

		nButton = buttons.size();

		buttonDimension = new Dimension((int)(width*0.3),(int)((height*0.54)/nButton));

		buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);

		buttonPanel.setSize((int)(width*0.3), (int)(height*0.6));
		buttonPanel.setLayout(new GridLayout(5,1,0,(int)(height*0.02)));

		while(!buttons.isEmpty())
		{
			JButton temp = buttons.get(0);
			temp.setSize(buttonDimension);
			buttonPanel.add(temp);
			buttons.remove(0);
		}
		this.setLayout(null);

		this.add(buttonPanel);

		buttonPanel.setLocation((int)((width*0.35)), (int)(height*0.35));

		this.revalidate();
		this.repaint();
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g, imagePath);
	}
}