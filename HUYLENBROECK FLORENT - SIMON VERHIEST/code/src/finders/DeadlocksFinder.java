package finders;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.ListIterator;
import java.awt.Point;

import sokobanUtilities.SokobanUtilities;
import game.*;

/**
* Finds static and freeze deadlocks within a level.
* Corral, bipartite and closed diagonals type deadlocks aren't covered
*
* @author 	HUYLENBROECK Florent
*/
public class DeadlocksFinder
{
	private AStarPathFinder _PF;
	private Board board;
	private Cells[][] cellArray;
	private int[][] staticDLMap;

	/**
	* Constructor
	*
	* @param board 	Board, shows the level where to find deadlocks
	*/
	public DeadlocksFinder(Board board)
	{
		this.board=board;
		cellArray=board.getBoard();

		_PF = new AStarPathFinder(board);

		drawstaticDLMap();
	}

	/**
	* Constructor
	*
	* @param cellArray 	Cells[][], array of Cells
	*/
	public DeadlocksFinder(Cells[][] cellArray)
	{
		this.cellArray=cellArray;

		_PF=new AStarPathFinder(cellArray);

		drawstaticDLMap();
	}

	public int[][] getStaticDLMap()
	{
		return staticDLMap;
	}

	/**
	* Draws a map containing the static deadlocks within the level, 
	* deadlock-Cells are marked 1,
	* non-deadlock Cells are marked 0
	*/
	private void drawstaticDLMap()
	{
		staticDLMap = new int[cellArray.length][cellArray[0].length];

		for (int i=0; i<staticDLMap.length; i++)
		{
			Arrays.fill(staticDLMap[i], 1);

			for (int j=0; j<staticDLMap[0].length; j++)
			{
				if (_PF.canFindAReversedWay(j,i))
					staticDLMap[i][j]=0;
			}
		}
	}

	/**
	* Finds static and freeze deadlocks
	*
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the column
	* @return 			Boolean : true if a deadlock is found, false otherwise
	*/
	public Boolean findDeadlocks(int column, int row)
	{
		if(cellArray[row][column]!=Cells.CRATE)
			return false;

		return(findStaticDeadlocks(column, row) || 
			findFreezeDeadlocks(column, row));
	}

	/**
	* Finds if a given crate is on a static deadlock
	*
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the crate
	* @return 			Boolean: true if the crate is on a deadlock, false otherwise
	*/
	private Boolean findStaticDeadlocks(int column, int row)
	{
		return(staticDLMap[row][column]==1);
	}

	/**
	* Finds if a group of crate is frozen, thus creating a freeze deadlock
	*
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the crate
	* @return 			Boolean: true if the crate is part of a freeze deadlock, false otherwise
	*/
	private Boolean findFreezeDeadlocks(int column, int row)
	{
		if(isFrozen(cellArray, column, row))
		{
			ArrayList<Point> adjCrates = new ArrayList<Point>();
			int[][] crateMap = new int[board.getRows()][board.getColumns()];

			adjacentCrates(column, row, adjCrates, crateMap);
			Cells[][] map = SokobanUtilities.copyCellArray(cellArray);

			return (peelCrates(adjCrates, map));
		}

		return false;
	}

	/**
	* Finds a freeze deadlocks within a group of adjacent crates,
	* if one crate can move, it's removed from the group and the method is reapplied to
	* figure out if every crate is unlockable from its frozen state
	*
	* @param adjCrates 	Arraylist of Point, the list of adjacent crates
	* @return  			Boolean: 	true if some crate are still in freeze deadlock,
	*								false if every  crates are unlockable
	*/
	private Boolean peelCrates(ArrayList<Point> adjCrates, Cells[][] map)
	{
		Boolean crateRemoved = false;

		for (int i=0; i<adjCrates.size(); i++)
		{
			int x = (int)adjCrates.get(i).getX();
			int y = (int)adjCrates.get(i).getY();

			if(!isFrozen(map, x, y))
			{
				map[y][x]=Cells.FLOOR;
				crateRemoved=true;
				adjCrates.remove(i);
			}
		}

		if (crateRemoved)
			peelCrates(adjCrates, map);

		return (!adjCrates.isEmpty());
	}

	/**
	* Fills an ArrayList of Points with a group of adjacent crates
	*
	* @param column 	int, the column of one crate
	* @param row 		int, the row of one crate
	* @param adjCrates 	ArrayList of Point, the list containing the adjacent crates
	* @param crateMap 	int[][], map to remember which crate were already checked, since the method is recursive
	*/
	private void adjacentCrates(int column, int row, ArrayList<Point> adjCrates, int[][] crateMap)
	{
		adjCrates.add(new Point(column, row));
		crateMap[row][column]=1;

		if(cellArray[row-1][column]==Cells.CRATE && crateMap[row-1][column]!=1)
			adjacentCrates(column, row-1, adjCrates, crateMap);

		if(cellArray[row][column+1]==Cells.CRATE && crateMap[row][column+1]!=1)
			adjacentCrates(column+1, row, adjCrates, crateMap);

		if(cellArray[row+1][column]==Cells.CRATE && crateMap[row+1][column]!=1)
			adjacentCrates(column, row+1, adjCrates, crateMap);

		if(cellArray[row][column-1]==Cells.CRATE && crateMap[row][column-1]!=1)
			adjacentCrates(column-1, row, adjCrates, crateMap);
	}

	/**
	* Finds if a crate is frozen
	*
	* @param column 	int, the column of the crate
	* @param row 		int, the row of the crate
	* @return 			Boolean: true if the crate is frozen, false otherwise
	*/
	private Boolean isFrozen(Cells[][] map, int column, int row)
	{
		Boolean xAxisLocked = false;
		Boolean yAxisLocked = false;

		if(	map[row-1][column]==Cells.CRATE ||
			map[row-1][column]==Cells.WALL ||
			map[row+1][column]==Cells.CRATE ||
			map[row+1][column]==Cells.WALL )
			yAxisLocked=true;

		if( map[row][column-1]==Cells.CRATE ||
			map[row][column-1]==Cells.WALL ||
			map[row][column+1]==Cells.CRATE ||
			map[row][column+1]==Cells.WALL )
			xAxisLocked=true;

		return(xAxisLocked && yAxisLocked);
	}
}