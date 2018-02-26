package generator;

import game.*;
import sokobanUtilities.SokobanUtilities;
import finders.*;

import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import java.lang.ArrayIndexOutOfBoundsException;
import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

/**
* Generates Sokoban levels
*/
public class Generator
{
	private static int rows;
	private static int columns;
	private static int goals;
	private static Cells[][] board;
	private static int[][] connections;
	private static final Random rn = new Random();
	private static int corner = rn.nextInt(4);
	private static AStarPathFinder _PF;
	private static DeadlocksFinder _DF;
	private static int prow = 0;
	private static int pcolumn = 0;
	private static ArrayList<Point> playerPos = new ArrayList<Point>();

	/**
	* Generates Sokoban levels given a certain difficulty
	*
	* @param difficulty 	Difficulty, the difficulty of the level
	* @return 				String, the path leading to the .xsb file showing the level
	*/
	public static String generate(Difficulty difficulty)
	{
		Boolean allGoalsPlaced=false;
		Boolean allCratesPlaced=false;
		do
		{
			do
			{
				do
				{
					rows=difficulty.rows();
					columns=difficulty.columns();
					board=new Cells[rows][columns];

					for(int i=0; i<rows; i++)
						Arrays.fill(board[i], Cells.VOID);

					fillBorders();
					fillArea();

					_PF = new AStarPathFinder(board);

				} while(!checkForContinuousConnectivity());

				placePlayer();

				goals=difficulty.goals();
				
				allGoalsPlaced=placeGoals();
			} while(!allGoalsPlaced);

			playerPos = new ArrayList<Point>();
			allCratesPlaced=placeCrates();
		} while(!allCratesPlaced);

		String path = createLevelFile(difficulty.name());

		return path;
	}

	/**
	* Fills the borders of the newly created level
	*/
	private static void fillBorders()
	{
		for(int i=0; i<board.length; i++)
		{
			if(i==0 || i==board.length-1)
				Arrays.fill(board[i], Cells.WALL);
			else
			{
				board[i][0]=Cells.WALL;
				board[i][board[i].length-1]=Cells.WALL;
			}
		}
	}

	/**
	* Fills the area of the newly created level
	*/
	private static void fillArea()
	{
		connections=new int[board.length][board[1].length];

		while(!(isFilled()))
		{
			Templates temp = Templates.intToTemplates(rn.nextInt(Templates.values().length));
			temp.rotate(rn.nextInt(4));

			addTemplate(temp);
		}	
	}

	/**
	* Searches the board and tells wether it's already been filled
	* Using a loop is the fastest method according to this article : http://www.programcreek.com/2014/04/check-if-array-contains-a-value-java/
	*
	* @param board 	Cells[][], the array to search in
	* @return 		Boolean, true if the array is filled, false otherwise
	*/
	private static Boolean isFilled() 
	{
		for(Cells[] cellList : board)
		{
			for(Cells cell : cellList)
			{
				if(cell==Cells.VOID)
				{
					return false;
				}
			}
		}
		return true;
	}

	/**
	* Adds a template to the level's area
	*
	* @param template 	Templates to add
	*/
	private static void addTemplate(Templates template)
	{
		int row=0;
		int column=0;
		Boolean voidFound=false;

		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j]==Cells.VOID)
				{
					row=i;
					column=j;
					voidFound = true;
					break;
				}
			}
			if (voidFound)
				break;
		}

		int[][] tempConnections = new int[connections.length][connections[1].length];
		for(int i=0; i<tempConnections.length; i++)
			tempConnections[i]=connections[i].clone();

		String connectivity = template.getConnectivity();

		for(int i=0; i<connectivity.length(); i++)
		{
			if(Integer.parseInt(connectivity.substring(i,i+1))==1)
			{
				int rowToCheck = row;
				int columnToCheck = column;
				try
				{
					switch(i)
					{
						case 0	: rowToCheck--; columnToCheck--;
								break;
						case 1	: rowToCheck--;
								break;
						case 2	: rowToCheck--; columnToCheck++;
								break;
						case 3	: rowToCheck--; columnToCheck+=2;
								break;
						case 4	: rowToCheck--; columnToCheck+=3;
								break;
						case 5	: columnToCheck+=3;
								break;
						case 6	: rowToCheck++; columnToCheck+=3;
								break;
						case 7	: rowToCheck+=2; columnToCheck+=3;
								break;
						case 8	: rowToCheck+=3; columnToCheck+=3;
								break;
						case 9	: rowToCheck+=3; columnToCheck+=2;
								break;
						case 10	: rowToCheck+=3; columnToCheck++;
								break;
						case 11	: rowToCheck+=3;
								break;
						case 12	: rowToCheck+=3; columnToCheck--;
								break;
						case 13	: rowToCheck+=2; columnToCheck--;
								break;
						case 14	: rowToCheck++; columnToCheck--;
								break;
						case 15	: columnToCheck--;
						default	: break;

					}

					if(board[rowToCheck][columnToCheck]==Cells.VOID)
						tempConnections[rowToCheck][columnToCheck]=1;

					else if(!(board[rowToCheck][columnToCheck]==Cells.FLOOR))
						return;

				} catch (ArrayIndexOutOfBoundsException e) {
					return;
				}
			}
		}


		Cells[][] tempBoard = SokobanUtilities.copyCellArray(board);

		Cells[][] templateBoard = template.getTemplate();

		for(int i=0; i<templateBoard.length; i++)
		{
			for(int j=0; j<templateBoard[i].length; j++)
			{
				try
				{
					if(tempConnections[i+row][j+column]==1 && (templateBoard[i][j]!=Cells.FLOOR))
						return;

					if((i+row)>tempBoard.length-2 || (j+column)>tempBoard[i].length-2)
						break;

					tempBoard[i+row][j+column]=templateBoard[i][j];
				} catch(ArrayIndexOutOfBoundsException e) {
					continue;
				}
			}
		}

		connections=tempConnections;
		board=tempBoard;
	}

	/**
	* Checks if the level contains one continuous area of floor
	*
	* @return 	Boolean: true if the area contains one continuous area of floor, false otherwise
	*/
	private static Boolean checkForContinuousConnectivity()
	{
		int row=0;
		int column=0;
		Boolean floorFound=false;

		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j]==Cells.FLOOR)
				{
					row=i;
					column=j;
					floorFound = true;
					break;
				}
			}
			if (floorFound)
				break;
		}

		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j]==Cells.FLOOR && !(_PF.canFindAWay(column, row, j, i)))
				{
					return false;
				}
			}
		}

		return true;
	}

	/**
	* Places the player
	*/
	private static void placePlayer()
	{
		_PF = new AStarPathFinder(board);
		corner = rn.nextInt(4);

		int frow=0;
		int fcolumn=0;

		Boolean floorFound=false;

		for(int i=0; i<board.length; i++)
		{
			for(int j=0; j<board[i].length; j++)
			{
				if(board[i][j]==Cells.FLOOR)
				{
					frow=i;
					fcolumn=j;
					floorFound = true;
					break;
				}
			}
			if (floorFound)
				break;
		}

		int i=0;
		int j=0;

		if(corner==0 || corner==1)
			i=rows-1;
		else 
			i=0;

		if(corner==0 || corner==3)
			j=columns-1;
		else
			j=0;

		for(;i<rows && i>-1;)
		{
			for(;j<columns && j>-1;)
			{

				if(board[i][j]==Cells.FLOOR && !_PF.findAReversedWay(fcolumn, frow, j, i).isEmpty())
				{
					board[i][j]=Cells.PLAYER;
					pcolumn=j;
					prow=i;
					return;
				}

				if(corner==0 || corner==3)
					j--;
				else
					j++;
			}

			if(corner==0 || corner==1)
				i--;
			else 
				i++;

			if(corner==0 || corner==3)
				j=columns-1;
			else
				j=0;
		}
	}

	/**
	* Places all the goals
	*
	* @return 	Boolean: true if every goals has been placed, false otherwise
	*/
	private static Boolean placeGoals()
	{
		_PF=new AStarPathFinder(board);
		ArrayList<Point> goalList = new ArrayList<Point>();

		int tempGoals=goals;
		int goalsPlaced=0;

		int i;
		int j;

		if(corner==0 || corner==1)
			i=0;
		else 
			i=rows-1;

		if(corner==0 || corner==3)
			j=0;
		else
			j=columns-1;

		for(;i<rows && i>-1;)
		{
			for(;j<columns && j>-1;)
			{

				if(board[i][j]==Cells.FLOOR && !_PF.findAReversedWay(pcolumn, prow, j, i).isEmpty())
				{
					goalList.add(0, new Point(j, i));
					tempGoals--;
					goalsPlaced++;
				}

				if(tempGoals<1)
					break;

				else if((goalsPlaced*goalsPlaced) > goals)
				{
					goalsPlaced=0;
					break;
				}

				if(corner==0 || corner==3)
					j++;
				else
					j--;
			}

			if(tempGoals<1)
				break;

			goalsPlaced=0;

			if(corner==0 || corner==1)
				i++;
			else 
				i--;

			if(corner==0 || corner==3)
				j=0;
			else
				j=columns-1;
		}

		if(goalList.size()!=goals)
			return false;

		for(Point temp : goalList)
		{
			board[(int)temp.getY()][(int)temp.getX()]=Cells.GOAL;
		}

		return true;
	}

	/**
	* Places the crates
	*
	* @return 	Boolean: true if every crates has been placed, false otherwise
	*/
	private static Boolean placeCrates()
	{
		Cells[][] wallBoard = SokobanUtilities.copyCellArray(board);

		ArrayList<Point> crates = new ArrayList<Point>();

		for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				if(wallBoard[i][j]==Cells.GOAL)
				{
					_PF = new AStarPathFinder(wallBoard);
					Point temp = findFarthestState(wallBoard, j, i);
					crates.add(temp);

					wallBoard[(int)temp.getY()][(int)temp.getX()]=Cells.WALL;
				}
			}
		}

		if(crates.size()!=goals)
			return false;
		
		for(Point crate : crates)
		{
			if((int)crate.getY()==0 ||(int)crate.getX()==0)
				return false;
			else
				board[(int)crate.getY()][(int)crate.getX()]=Cells.CRATE;
		}

		return true;
	}

	/**
	* Randomly finds the farthest state from a crate
	*
	* @param wallBoard 	Cells[][] current level state but crate are replaced by walls
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the crate
	* @return 			Point, farthest state from the crate
	*/
	private static Point findFarthestState(Cells[][] wallBoard, int column, int row)
	{
		int tries = 100;
		int farthest = 0;
		int farthestX = 0;
		int farthestY = 0;
		Point farthestPos = new Point();

		_DF = new DeadlocksFinder(wallBoard);

		int[][] staticDLMap = _DF.getStaticDLMap();

		do
		{
			int i = rn.nextInt(rows);
			int j = rn.nextInt(columns);

			if(board[i][j]==Cells.FLOOR && staticDLMap[i][j]!=1 && canStillReachEveryCrates(wallBoard, j, i))
			{
				ArrayList<Point> path = _PF.findAReversedWay(column, row, j, i);
				int distance = path.size();

				if(distance==0)
					continue;

				Point ppos = new Point((int)path.get(path.size()-1).getX(), (int)path.get(path.size()-1).getY());

				if(distance > farthest)
				{
					farthestX=j;
					farthestY=i;
					farthest=distance;
					farthestPos=ppos;
				}
			}
		} while(tries-->0);
				


		/*for(int i=0; i<rows; i++)
		{
			for(int j=0; j<columns; j++)
			{
				if(board[i][j]==Cells.FLOOR && staticDLMap[i][j]!=1)// && canStillReachEveryCrates(wallBoard, j, i))
				{
					ArrayList<Point> path = _PF.findAReversedWay(column, row, j, i);
					int distance = path.size();

					if(distance==0)
						continue;

					Point ppos = new Point((int)path.get(path.size()-1).getX(), (int)path.get(path.size()-1).getY());

					if(distance > farthest)
					{
						farthestX=j;
						farthestY=i;
						farthest=distance;
						farthestPos=ppos;
					}
				}
			}
		}*/

		playerPos.add(farthestPos);
		return new Point(farthestX, farthestY);
	}

	/**
	* Checks wheter if the player can still reach every positions he needs to push the crates after a crate is placed
	*
	* @param wallBoard 	Cells[][] current level state but crate are replaced by walls
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the crate
	* @return 			Boolean: true if every position is reachable, false otherwise
	*/
	private static Boolean canStillReachEveryCrates(Cells[][] wallBoard, int column, int row)
	{
		if(playerPos.isEmpty())
			return true;

		Boolean ret = true;

		_PF = new AStarPathFinder(wallBoard);

		for(Point pos : playerPos)
		{
			ret = (true && _PF.canFindAWay(pcolumn, prow, (int)pos.getX(), (int)pos.getY()));
		}

		return ret;
	}

		/**
	* Creates the .xsb file showing the level
	*
	* @param difficultyName 	String, name of the difficulty, for naming purposes
	* @return 					String, the path to the .xsb file
	*/
	private static String createLevelFile(String difficultyName)
	{
		Integer tries=1;

		String levelPath = SokobanUtilities.getPathToCode()+"/code/levels/generated/generator_"+difficultyName.toLowerCase();
		String tempPath = levelPath+".xsb";
		File level = new File(levelPath+".xsb");

		while(level.exists())
		{
			tempPath = levelPath+"_"+tries.toString()+".xsb";
			level=new File(tempPath);
			tries++;
		}

		try
		{
			level.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

		PrintWriter out = null;
		try
		{
			out = new PrintWriter(level);
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

		levelPath=tempPath;

		return levelPath;
	}
}