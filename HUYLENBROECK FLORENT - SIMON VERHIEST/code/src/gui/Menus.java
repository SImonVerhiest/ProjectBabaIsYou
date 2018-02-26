package gui;

import sokobanUtilities.*;

/**
* Enum containing the menu's names and the path to their background image
*
* @author 	HuUYLENBROECK Florent
*/
public enum Menus
{
	MAIN_MENU("/code/resources/menus/main_menu.png"),
	LEVEL_MENU("/code/resources/menus/level_menu.png"),
	PACK_MENU("/code/resources/menus/level_menu.png"),
	LEVEL_SELECT("/code/resources/menus/level_menu.png"),
	GENERATOR_MENU("/code/resources/menus/level_menu.png");

	private final String path;

	Menus(String path)
	{
		this.path = SokobanUtilities.getPathToCode()+path;
	}

	public String getPath()
	{
		return path;
	}
}