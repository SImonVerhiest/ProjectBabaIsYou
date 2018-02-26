package generator;

import game.Cells;
import sokobanUtilities.SokobanUtilities;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
* Enum that holds different template used to build a Sokoban level
*
* @author 	HUYLENBROECK Florent
*/
public enum Templates
{
	EMPTY(0,"empty"),
	SINGLE_WALL_CORNER(1,"single_wall_corner"),
	TWO_WALL_ONE_SIDE(2,"two_wall_one_side"),
	THREE_WALL_ONE_SIDE(3,"three_wall_one_side"),
	FIVE_WALL_ONE_CORNER(4,"five_wall_one_corner"),
	TWO_WALL_OPPOSITE_CORNER(5,"two_wall_opposite_corner"),
	TWO_WALL_SYMETRIC_CORNER(6,"two_wall_symetric_corner"),
	THREE_WALL_CORNER(7,"three_wall_corner"),
	FOUR_WALL_CORNER(8,"four_wall_corner"),
	SIX_WALL_TWO_SIDE_ONE_CORNER(9,"six_wall_two_side_one_corner"),
	SIX_WALL_SYMETRIC_SIDE(10,"six_wall_symetric_side"),
	SINGLE_WALL_CENTER(11,"single_wall_center"),
	FULL(12,"full"),
	FOUR_WALL_HALF_CORNER(13,"four_wall_half_corner"),
	TWO_WALL_SYMETRIC_CENTER(14,"two_wall_symetric_center"),
	SIX_WALL_TWO_ROW(15,"six_wall_two_row"),
	FOUR_WALL_TSHAPE(16,"four_wall_tshape");

	private int number;
	private Cells[][] _template;
	private String connectivity;

	private File level;
	private FileReader levelReader;
	private BufferedReader bufferedReader;

	/**
	* @param number 		int, corresponding number for random generation
	* @param path 	String, path to a .xsb file containing the template and a binary number used to connect templates
	* 						this number's first digit corresponds to upper left corner and it goes clockwise around the Templates
	*
	*						01234
	*							5
	*						    6
	*                           7
	*						 ..98
	*
	*						0 means non-restricted connectivity					
	*						1 means floor-only connectable
	*/
	Templates(int number, String path)
	{
		this.number=number;

		_template = new Cells[3][3];

		try
		{
			level = new File(SokobanUtilities.getPathToCode()+"/code/resources/generator/templates/"+path+".xsb");
			levelReader = new FileReader(level);
			bufferedReader = new BufferedReader(levelReader);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		String line;

		try
		{
			int rowsFilled=0;
			while ((line=bufferedReader.readLine()) != null)
			{
				

				if(line.charAt(0)==';')
					connectivity=line.substring(1,line.length());

				else
				{
					for (int i=0; i<3; i++)
					{
						_template[rowsFilled][i]=Cells.xsbToCells(line.charAt(i));
					}
					rowsFilled++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public String getConnectivity()
	{
		return connectivity;
	}

	public Cells[][] getTemplate()
	{
		return _template;
	}

	/**
	* Rotates a template 90 degrees clockwise a certain number of time by transposing the Array then reversing each rows
	*
	* @param nTimes  int, number of 90 degree rotation to apply
	*/
	public void rotate(int nTimes)
	{	
		nTimes %= 4;

		if(number==0 || number==12 || number==8 || nTimes==0)
			return;

		while(nTimes<0)
		{
			connectivity=connectivity.substring(12,16)+connectivity.substring(0,12);

			Cells[][] tmp = new Cells[3][3];

			for(int i=0; i<3; i++)
			{
				for( int j=0; j<3; j++)
				{
					tmp[i][j] = _template[j][i];
				}
			}

			for(int i=0; i<3; i++)
			{
				for( int j=0; j<3; j++)
				{
					_template[i][j] = tmp[2-i][j];
				}
			}

			nTimes--;
		}
	}

	/**
	* Gives the Templates corresponding to an int
	*
	* @param i 		int, the number corresponding to a template
	* @return 		Templates corresponding to the int
	*/
	public static Templates intToTemplates(int i)
	{
		Templates ret = EMPTY;

		for(Templates temp : Templates.values())
		{
			if(temp.number==i)
				ret=temp;
		}

		return ret;
	}
}