package game;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import sokobanUtilities.*;

/**
* Enum that contains every possible case in a Board, along with their 
* corresponding images
*
* @author	HUYLENBROECK Florent
*/
public enum Cells
{
	FLOOR("/code/resources/board/floor.png"),
	PLAYER("/code/resources/board/player.png"),
	PLAYER_ON_GOAL("/code/resources/board/player_on_goal.png"),
	WALL("/code/resources/board/wall.png"),
	CRATE("/code/resources/board/crate.png"),
	GOAL("/code/resources/board/goal.png"),
	CRATE_ON_GOAL("/code/resources/board/crate_on_goal.png"),
	VOID("/code/resources/board/void.png");

	private BufferedImage image;

	Cells(String path)
	{
		try
		{
			image = ImageIO.read(new File(SokobanUtilities.getPathToCode()+path));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public BufferedImage getImage()
	{
		return image;
	}

	/**
	* Figures out which Cells corresponds to a given xsb char
	*
	* @param xsb 	char, to be converted to a Cells
	* @return 		Cells, corresponding to the char
	*/
	public static Cells xsbToCells(char xsb)
	{
		Cells retValue=FLOOR;

		switch(xsb)
		{
			case ' '	: retValue=FLOOR;
						break;
			case '@'	: retValue=PLAYER;
						break;
			case '+'	: retValue=PLAYER_ON_GOAL;
						break;
			case '#'	: retValue=WALL;
						break;
			case '$'	: retValue=CRATE;
						break;
			case '.'	: retValue=GOAL;
						break;
			case '*'	: retValue=CRATE_ON_GOAL;
						break;
			default 	: break;
		}

		return retValue;
	}

	/**
	* Figures out which xsb char corresponds to a given Cells
	*
	* @param cell 	Cells, to be converted to a xsb char
	* @return 		char, corresponding to the Cells
	*/
	public static char cellsToxsb(Cells cell)
	{
		char retValue=' ';

		switch(cell)
		{
			case FLOOR			: retValue=' ';
								break;
			case PLAYER			: retValue='@';
								break;
			case PLAYER_ON_GOAL	: retValue='+';
								break;
			case WALL			: retValue='#';
								break;
			case CRATE			: retValue='$';
								break;
			case GOAL			: retValue='.';
								break;
			case CRATE_ON_GOAL	: retValue='*';
								break;
			case VOID 			: retValue='v';
			default 			: break;
		}

		return retValue;
	}
}