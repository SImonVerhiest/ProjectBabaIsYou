
package game;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;
import java.lang.StringIndexOutOfBoundsException;

import sokobanUtilities.*;
import finders.*;

/**
* Object built from an .xsb file to show one level of the game.
*
* @author	Huylenbroeck Florent - Verhiest Simon
*/
public class Board
{
	/**
	* Absolute path to the .xsb file showing the level
	*/
	private String path;

	/**
	* Two-dimensionnal array containing the level's cells
	*/
	private Cells[][] board;

	/**
	* Object that finds deadlocks in the board
	*/
	private DeadlocksFinder df;

	/**
	* Number of columns of the board
	*/
	private int columns;

	/**
	* Number of rows of the board
	*/
	private int rows;

	/**
	* String keeping track of the moves that occured
	*/
	private StringBuffer movString = new StringBuffer();

	private int playerColumn;
	private int playerRow;
	public ArrayList<Point> movedCells = new ArrayList<Point>();

	/**
	* Constructor
	*
	* @param path 			String, relative path to the .xsb file that the Board will show
	* @throws IOException 	If the xsb file is not found
	*/
	public Board(String path)
	throws IOException
	{
		this.path=path;

		columns=0;
		rows=0;

		File level = new File(path);
		FileReader levelReader = new FileReader(level);
		BufferedReader bufferedReader = new BufferedReader(levelReader);
		String line;

		while ((line = bufferedReader.readLine()) != null) 
		{
			rows++;
			if (line.length() > columns)
				columns = line.length();
		}

		levelReader.close();

		board = new Cells[rows][columns];

		for(int i=0; i<rows; i++)
			Arrays.fill(board[i], Cells.FLOOR);
	
		levelReader = new FileReader(level);
		bufferedReader = new BufferedReader(levelReader);

		int boardColumn=0;
		int boardRow=0;

		while ((line=bufferedReader.readLine()) != null)
		{
			for (int i=0; i<columns; i++)
			{
				try
				{
					if (line.charAt(i)=='@')
					{
						playerColumn=boardColumn;
						playerRow=boardRow;
					}
					board[boardRow][boardColumn] = Cells.xsbToCells(line.charAt(i));
					boardColumn = (boardColumn+1) % (columns);
				} catch (StringIndexOutOfBoundsException e) {
					break;
				}
			}
			boardColumn = 0;
			boardRow = (boardRow+1) % (rows);
		}

		df = new DeadlocksFinder(this);
	}

	/**
	* Constructor
	*
	* @param save 			SokoSave from which to build the level
	* @throws IOException 	.mov file not found
	*/
	public Board(SokoSave save)
	throws IOException
	{
		this(save.getXsbPath());
		applyMov(SokobanUtilities.loadMov(save.getMovPath()));
	}

	public Cells[][] getBoard()
	{
		return board;
	}

	public int getColumns()
	{
		return columns;
	}

	public int getRows()
	{
		return rows;
	}

	public String getPath()
	{
		return path;
	}

	public DeadlocksFinder getDf()
	{
		return df;
	}

	public String getMovString()
	{
		return movString.toString();
	}


	/**
	* Tries to move a player
	*
	* @param move 	Moves, tells which direction a player wishes to move
	* @param save 	Boolean, true to save the move to the movString, 
	*						false otherwise
	* @return 		int, -1 if the move is denied,
	*					 0 if the player moves alone,
	*					 1 if the player moves with a crate,
	*					 2 if the player moves with a crate and places it on a goal
	*/
	public int move(Moves move, Boolean save)
	{
		int destColumn=playerColumn;
		int destRow=playerRow;

		switch(move)
		{
			case UP 	: destRow=playerRow-1;
						break;
			case RIGHT 	: destColumn=playerColumn+1;
						break;
			case DOWN 	: destRow=playerRow+1;
						break;
			case LEFT	: destColumn=playerColumn-1;
						break;
		}

		if (board[destRow][destColumn]==Cells.FLOOR ||
			board[destRow][destColumn]==Cells.GOAL)
		{
			board[playerRow][playerColumn]=departCell(playerRow, playerColumn);
			board[destRow][destColumn]=destCell(Cells.PLAYER, destRow, destColumn);

			movedCells.add(new Point(playerColumn, playerRow));
			movedCells.add(new Point(destColumn, destRow));

			playerColumn=destColumn;
			playerRow=destRow;

			if (save)
				movString.append(move.getToMov()[0]);

			return 0;
		}

		else if (board[destRow][destColumn]==Cells.CRATE ||
			board[destRow][destColumn]==Cells.CRATE_ON_GOAL)
		{
			int crateRow=destRow;
			int crateColumn=destColumn;

			destRow=destRow+(destRow-playerRow);
			destColumn=destColumn+(destColumn-playerColumn);

			try
			{
				if (board[destRow][destColumn]==Cells.FLOOR ||
					board[destRow][destColumn]==Cells.GOAL)
				{
					board[destRow][destColumn]=destCell(Cells.CRATE, destRow, destColumn);
					board[crateRow][crateColumn]=departCell(crateRow, crateColumn);
					board[playerRow][playerColumn]=departCell(playerRow, playerColumn);          
					movedCells.add(new Point(playerColumn, playerRow));
					movedCells.add(new Point(crateColumn, crateRow));
					movedCells.add(new Point(destColumn, destRow));

					playerColumn=crateColumn;
					playerRow=crateRow;

					if (save)
						movString.append(move.getToMov()[1]);

					if (board[destRow][destColumn]==Cells.CRATE_ON_GOAL)
						return 2;

					return 1;
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				return -1;
			}
		}
		return -1;
	}

	/**
	* Replaces a Cells of the board after it has moved
	*
	* @param column 	int, the column of the Cells to replace
	* @param row 		int, the row of the Cells to replace
	*/
	private Cells departCell(int row, int column)
	{
		Cells retValue=Cells.FLOOR;

		switch(board[row][column])
		{
			case PLAYER 		: retValue=Cells.FLOOR;
								break;
			case PLAYER_ON_GOAL : retValue=Cells.GOAL;
								break;
			case CRATE 			: retValue=Cells.PLAYER;
								break;
			case CRATE_ON_GOAL 	: retValue=Cells.PLAYER_ON_GOAL;
								break;
		}

		return retValue;
	}

	/**
	* Places and adapt a Cells on his destination after a move
	*
	* @param moved 	Cells, the type of Cells moved
	* @param column int, the column where the Cells is moved to
	* @param row 	int, the row where the Cells is moved to
	*/
	private Cells destCell(Cells moved, int row, int column)
	{
		Cells retValue=Cells.FLOOR;

		if (moved==Cells.PLAYER || moved==Cells.PLAYER_ON_GOAL)
		{
			switch(board[row][column])
			{
				case FLOOR			: ;
				case CRATE 			: retValue=Cells.PLAYER;
									break;
				case GOAL 			: ;
				case CRATE_ON_GOAL 	: retValue=Cells.PLAYER_ON_GOAL;
									break;
			}
		}

		else if (moved==Cells.CRATE || moved==Cells.CRATE_ON_GOAL)
		{
			switch(board[row][column])
			{
				case FLOOR	: retValue=Cells.CRATE;
							break;
				case GOAL 	: retValue=Cells.CRATE_ON_GOAL;
							break;
			}
		}

		return retValue;
	}

	/**
	* Undos a move based on the movString
	*/
	public void undo()
	{
		if (movString.length()<1)
			return;

		int prow=playerRow;
		int pcol=playerColumn;

		switch (movString.charAt(movString.length()-1))
		{
			case 'u' 	: move(Moves.DOWN, false);
						break;
			case 'r' 	: move(Moves.LEFT, false);
						break;
			case 'd' 	: move(Moves.UP, false);
						break;
			case 'l' 	: move(Moves.RIGHT, false);
						break;
			case 'U' 	: move(Moves.DOWN, false);
						pullCrate(prow, pcol, Moves.DOWN);
						break;
			case 'R' 	: move(Moves.LEFT, false);
						pullCrate(prow, pcol, Moves.LEFT);
						break;
			case 'D' 	: move(Moves.UP, false);
						pullCrate(prow, pcol, Moves.UP);
						break;
			case 'L' 	: move(Moves.RIGHT, false);
						pullCrate(prow, pcol, Moves.RIGHT);
						break;
			default 	: break;
		}

		movString.deleteCharAt(movString.length()-1);
	}

	private void pullCrate(int row, int column, Moves move)
	{
		int crateRow = row;
		int crateColumn = column;

		switch(move)
		{
			case UP 	: crateRow++;
						break;
			case RIGHT 	: crateColumn--;
						break;
			case DOWN 	: crateRow--;
						break;
			case LEFT 	: crateColumn++;
						break;
			default 	: break;
		}

		if (board[row][column]==Cells.GOAL)
			board[row][column]=Cells.CRATE_ON_GOAL;
		else
			board[row][column]=Cells.CRATE;

		if (board[crateRow][crateColumn]==Cells.CRATE_ON_GOAL)
			board[crateRow][crateColumn]=Cells.GOAL;
		else
			board[crateRow][crateColumn]=Cells.FLOOR;

		movedCells.add(new Point(column, row));
		movedCells.add(new Point(crateColumn, crateRow));
	}

	/**
	* Finds out if the game has reached an end point
	*
	* @return 	Boolean: 	true if the game is over,
	*						false otherwise
	*/
	public Boolean isDone()
	{
		for (int i=0; i<rows; i++)
		{
			for (int j=0; j<columns; j++)
			{
				if (board[i][j]==Cells.CRATE)
					return false;
			}
		}

		return true;
	}

	/**
	* Apply some simplifications to the movString, before saving
	*/
	public void simplificateMovString()
	{
		String toCheck="";

		for(int i=0; i<movString.length()-2; )
		{
			switch(toCheck = movString.substring(i,i+2))
			{
				case "ud" 	: ;
				case "du" 	: ;
				case "lr"	: ;
				case "rl" 	: movString.delete(i, i+2);
							  if(i>0)
							  	i--;
							break;
				default 	: i++;
			}
		}

		for(int i=0; i<movString.length()-3; )
		{
			switch(toCheck = movString.substring(i,i+3))
			{
				case "uld" 	: ;
				case "urd" 	: ;
				case "dlu" 	: ;
				case "dru" 	: ;
				case "lur"	: ;
				case "ldr"	: ;
				case "rul" 	: ;
				case "rdl" 	: movString.deleteCharAt(i+2);
							movString.deleteCharAt(i);
							  if(i>0)
							  	i--;
							break;
				default 	: i++;
			}
		}
	}

	/**
	* Saves the currents state of the Board as a .mov file
	* Initial code for this method was found at http://stackoverflow.com/questions/1053467/how-do-i-save-a-string-to-a-text-file-using-java
	*
	* @param savePath 	String, absolute path to the .mov file
	* @return 			String, absolute path to the .mov file
	*/
	public String saveState(String savePath)
	{
		simplificateMovString();

		File save = new File(savePath);

		if (!save.getParentFile().exists()) 
			save.getParentFile().mkdirs();

		try
		{
			save.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		PrintWriter out = null;
		try
		{
			out = new PrintWriter(save);
			out.print(movString);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			out.close();
		}

		return savePath;
	}

	/**
	* Saves the currents state of the Board as a .mov file
	*
	* @return 	String, absolute path to the .mov file
	*/
	public String saveState()
	{
		String savePath = SokobanUtilities.createSavePath(path);
		
		return saveState(savePath);
	}

	/**
	* Saves the currents state of the Board as a .xsb file
	*
	* @param savePath 	String, absolute path to the .xsb file
	*/
	public void saveStateAsXSB(String savePath)
	{
		File save = new File(savePath);

		if (!save.getParentFile().exists()) 
			save.getParentFile().mkdirs();

		try
		{
			save.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		PrintWriter out = null;
		try
		{
			out = new PrintWriter(save);
			for(int i=0; i<rows; i++)
			{
				for(int j=0; j<columns; j++)
				{
					out.print(Cells.cellsToxsb(board[i][j]));
				}
				out.println();
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} finally {
			out.close();
		}
	}

	/**
	* Applies the content of a .mov file to the board
	*
	* @param movString 	String, content of a .mov file showing a set of moves
	*/
	public void applyMov(String movString)
	{
		for(int i=0; i<movString.length(); i++)
		{
			switch(movString.charAt(i))
			{
				case 'u' 	: ;
				case 'U' 	: move(Moves.UP, true);
							break;
				case 'r' 	: ;
				case 'R' 	: move(Moves.RIGHT, true);
							break;
				case 'd' 	: ;
				case 'D' 	: move(Moves.DOWN, true);
							break;
				case 'l' 	: ;
				case 'L' 	: move(Moves.LEFT, true);
				default 	: break;
			}
		}
	}
}