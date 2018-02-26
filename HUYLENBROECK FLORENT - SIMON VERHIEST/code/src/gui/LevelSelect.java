package gui;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import sokobanUtilities.*;

/**
* ResizedRGBPanel extension, showing the level select menu
*
* @author 	HUYLENBROECK Florent
*/
public class LevelSelect extends ResizedBGPanel
{
	private String pack;
	private String imagePath = path+"/code/resources/menus/level_menu.png";

	private int levelCount;
	private int maxRow;
	private int maxCol;

	private Dimension buttonDimension;

	private ButtonListener _BL = new ButtonListener();

	private JPanel levelPanel;

	public LevelSelect(SokobanFrame parent, String pack)
	{
		super(parent);

		this.pack=pack;

		File dir = new File(path+"/code/levels/base/"+pack+"/");
		int levelCount = countFiles(dir);
		Point orgDim = SokobanUtilities.levelSelectOrganizer(levelCount);
		maxRow = (int)orgDim.getY();
		maxCol = (int)orgDim.getX();

		buttonDimension = new Dimension((int)((width*0.81)/maxCol), (int)((height*0.72)/maxRow));

		levelPanel = new JPanel();
		levelPanel.setOpaque(false);
		levelPanel.setSize((int)(width*0.9), (int)(height*0.8));
		levelPanel.setLayout(new GridLayout(maxRow, maxCol, (int)((width*0.09)/maxCol), (int)((height*0.08)/maxRow)));

		for(Integer i = 1; i<=levelCount; i++)
		{
			JButton temp = new JButton(i.toString());
			temp.setSize(buttonDimension);
			temp.putClientProperty("path", ""+path+"/code/levels/base/"+pack+"/level_"+i.toString()+".xsb");
			temp.addActionListener(_BL);
			levelPanel.add(temp);
		}

		JButton back = new JButton("BACK");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				LevelSelect.this.parent.initPackSelect();
			}
		});
		back.setSize((int)(width*0.2), (int)(height*0.1));

		this.setLayout(null);
		this.add(levelPanel);
		levelPanel.setLocation((int)(width*0.05), (int)(height*0.05));

		this.add(back);
		back.setLocation((int)(width*0.78), (int)(height*0.87));

		this.revalidate();
		this.repaint();
	}

	/**
	* Counts files in a directory (subdirectories excluded)
	*
	* @param dir 	File, the directory in which to count files
	* @return 		int, the total count of file in the directory
	*/
	private int countFiles(File dir)
	{
		int totalFiles = 0;

		File[] listFiles = dir.listFiles();
		if (listFiles != null && listFiles.length > 0)
		{
			for (File file : listFiles)
			{
				if (file.isFile())
					totalFiles++;
			}  
		}	
		return totalFiles;  
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g, imagePath);
    }

    /**
    * Actionlistener implementation that listens to the level select JButtons
    *
    * @author 	HUYLENBROECK FLorent
    */
    private class ButtonListener implements ActionListener
    {
    	@Override
    	public void actionPerformed(ActionEvent e)
    	{
    		JButton source = (JButton)e.getSource();
    		LevelSelect.this.parent.launchGameFrame((String)source.getClientProperty("path"));
    	}
    }
}