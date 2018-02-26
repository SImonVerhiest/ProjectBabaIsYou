package sokobanUtilities;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import game.*;

/**
* Some useful methods for the Sokoban's GUI and logic
*
* @author 	HUYLENBROECK Florent - VERHIEST Simon
*/
public class SokobanUtilities
{
	private static final double FRAME_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final double FRAME_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().getHeight();

	private static final double MAX_FRAME_WIDTH = FRAME_WIDTH*0.8;
	private static final double MAX_FRAME_HEIGHT = FRAME_HEIGHT*0.8;

	private static final double MIN_FRAME_WIDTH = FRAME_WIDTH*0.5;
	private static final double MIN_FRAME_HEIGHT = FRAME_HEIGHT*0.5;

	private static final double MENU_FRAME_WIDTH = FRAME_WIDTH*0.66;
	private static final double MENU_FRAME_HEIGHT = FRAME_HEIGHT*0.66;

	public static double getMinFrameWidth()
	{
		return MIN_FRAME_WIDTH;
	}

	public static double getMinFrameHeight()
	{
		return MIN_FRAME_HEIGHT;
	}

	public static double getMenuWidth()
	{
		return MENU_FRAME_WIDTH;
	}

	public static double getMenuHeight()
	{
		return MENU_FRAME_HEIGHT;
	}

	public static double getMaxFrameWidth()
	{
		return MAX_FRAME_WIDTH;
	}

	public static double getMaxFrameHeight()
	{
		return MAX_FRAME_HEIGHT;
	}

	public static String getPathToCode()
	{
		String absolutePath = new File(".").getAbsolutePath();

		int indexOfCode = 0;

		for (int i=0; i<absolutePath.length()-4; i++)
		{
			if (absolutePath.substring(i,i+4).equals("IEST"))
				indexOfCode=i;
		}

		return absolutePath.substring(0, indexOfCode+4);
	}

	/**
	* Resizes an image with a smooth scaling
	*
	* @param toResize 	BufferedImage to be resized
	* @param newWidth 	int, the new width of the image
	* @param newHeight 	int, the new height of the image
	* @return 			BufferedImage, the resized image
	*/
	public static BufferedImage resize(BufferedImage toResize, int newWidth, int newHeight) 
	{ 
	    Image temp = toResize.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
	    BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g2d = resizedImage.createGraphics();
	    g2d.drawImage(temp, 0, 0, null);
	    g2d.dispose();

	    return resizedImage;
	} 

	/**
	* Resizes an image with a smooth scaling
	*
	* @param toResize 	BufferedImage to be resized
	* @param newSide 	int, the new size of the image's side
	* @return 			BufferedImage, the resized image
	*/
	public static BufferedImage resize(BufferedImage toResize, int newSide) 
	{ 
		return resize(toResize, newSide, newSide);
	} 

	/**
	* Organizes the levelSelect
	*
	* @param i 	int, the number of level
	* @return 	Point, the number of rows and columns of JButton to create
	*/
	public static Point levelSelectOrganizer(int i)
	{
		double halfRatio = (MENU_FRAME_WIDTH / MENU_FRAME_HEIGHT)/2;

		double row = Math.sqrt(i)/halfRatio;
		double col = Math.sqrt(i)*halfRatio;

		if ((int)row*(int)col<i)
		{
			if(row-(int)row < col-(int)col)
				col = col+1;
			else
				row = row+1;
		}

		return new Point((int)col, (int)row);
	}

	/**
	* Finds the absolute path to the next level given the path of a level
	*
	* @param path 	String, the absolute path of the current level
	* @return 		String, the absolute path of the next level
	*/
	public static String nextLevelPath(String path)
	{
		int indexOfLevel = 0;
		int indexOfxsb = 0;

		for (int i=0; i<path.length()-6; i++)
		{
			if (path.substring(i,i+6).equals("level_"))
				indexOfLevel=i+6;
		}

		for(int i=0; i<path.length()-3; i++)
		{
			if (path.substring(i,i+4).equals(".xsb"))
				indexOfxsb=i;
		}

		if(indexOfLevel==0 || indexOfxsb==0)
			return "";

		String level = path.substring(indexOfLevel,indexOfxsb);

		Integer i = Integer.parseInt(level) +1;

		return path.substring(0,indexOfLevel)+i.toString()+".xsb";
	}

	/**
	* Finds the absolute path to the previous level given the path of a level
	*
	* @param path 	String, the absolute path of the current level
	* @return 		String, the absolute path of the previous level
	*/
	public static String prevLevelPath(String path)
	{
		int indexOfLevel = 0;
		int indexOfxsb = 0;

		for (int i=0; i<path.length()-6; i++)
		{
			if (path.substring(i,i+6).equals("level_"))
				indexOfLevel=i+6;
		}

		for(int i=0; i<path.length()-3; i++)
		{
			if (path.substring(i,i+4).equals(".xsb"))
				indexOfxsb=i;
		}

		if(indexOfLevel==0 || indexOfxsb==0)
			return "";

		String level = path.substring(indexOfLevel,indexOfxsb);

		Integer i = Integer.parseInt(level) -1;

		return path.substring(0,indexOfLevel)+i.toString()+".xsb";
	}

	/**
	* Replaces the "levels" repertory of a level path with "save"
	*
	* @param levelPath 	String, the path to a .xsb file
	* @return 			String, the path to the save file
	*/
	public static String createSavePath(String levelPath)
	{
		int indexOfLevel = 0;

		for (int i=0; i<levelPath.length()-6; i++)
		{
			if(levelPath.substring(i,i+6).equals("levels"))
				indexOfLevel=i;
		}
		
		return ""+levelPath.substring(0,indexOfLevel)+"saves"+levelPath.substring(indexOfLevel+6,levelPath.length()-4)+".mov";
	}

	/**
	* Loads a .mov file into a String
	*
	* @param path 			String, the absolute path leading to the .mov file
	* @throws IOException	Throws an exception if the .mov file is not found
	* @return 				String contained in the .mov file
	*/
	public static String loadMov(String path)
	throws IOException
	{
		String movString="";

		File mov = new File(path);
		FileReader movReader = new FileReader(mov);
		BufferedReader bufferedReader = new BufferedReader(movReader);
		String line;

		while ((line = bufferedReader.readLine()) != null) 
		{
			for(int i=0; i<line.length(); i++)
			{
				movString=movString+line.charAt(i);
			}
		}

		movReader.close();

		return movString;
	}

	/**
	* Creates a deep-copy of an array of Cells
	*
	* @param cellArray 	Cells[][], to copy
	* @return  			Cells[][], copied array of Cells
	*/
	public static Cells[][] copyCellArray(Cells[][] cellArray)
	{
		Cells[][] cellArrayCopy = new Cells[cellArray.length][cellArray[0].length];

		for (int i=0; i<cellArray.length; i++)
		{
			for (int j=0; j<cellArray[0].length; j++)
			{
				cellArrayCopy[i][j]=cellArray[i][j];
			}
		}

		return cellArrayCopy;
	}
}
