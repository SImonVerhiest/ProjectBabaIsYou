package finders;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.awt.Point;

import game.*;

/**
* Finds paths within a Board
*
* @author 	Huylenbroeck Florent - Verhiest Simon
*/
public class IDAStarPathFinder
{
	private Node[][] boardNodes;
	private Board board;
	private Cells[][] noCrate;

	private Node node;
	private int costOfcurrentNode;
	private int estimatedCost;

	/**
	* Constructor
	*
	* @param board 	Board, showing the level
	*/
	public IDAStarPathFinder(Board board)
	{
		this.board=board;
		
		noCrate = removeCrates(board);

		boardNodes = new Node[noCrate.length][noCrate[0].length];

		for (int i=0 ; i<noCrate.length ; i++)
		{
			for (int j=0 ; j<noCrate[0].length ; j++)
			{
				boardNodes[i][j] = new Node(j, i);
			}
		}
	}

	/**
	* Removes the crates and the player from a Copy of a Board.board
	*
	* @param board 	Board, showing the levels
	* @return 		Cells[][], copy of the Board.board with no crates nor player in it
	*/
	public Cells[][] removeCrates(Board board)
	{
		Cells[][] noCrate = new Cells[board.getRows()][board.getColumns()];

		for (int i=0; i<board.getRows(); i++)
		{
			for (int j=0; j<board.getColumns(); j++)
			{
				if (board.getBoard()[i][j]==Cells.CRATE ||
					board.getBoard()[i][j]==Cells.PLAYER ||
					board.getBoard()[i][j]==Cells.CRATE_ON_GOAL)
					noCrate[i][j] = Cells.FLOOR;
				else 
					noCrate[i][j] = board.getBoard()[i][j];

			}
		}

		return noCrate;
	}

	/**
	* Finds out if a crate at a certain position can be pulled to a certain direction
	*
	* @param move 	Moves, the direction of the pull
	* @param row 	int, the row which the crate is pulled from
	* @param column int, the column which the crate is pulled from
	* @return 		Boolean: true if the crate can be pulled, false otherwise
	*/
	public Boolean canBePushed(Moves move, int row, int column)
	{
		int prow = row;
		int pcolumn = column;

		switch(move)
		{
			case UP		: row--; prow++;
						break;
			case RIGHT	: column++; pcolumn--;
						break;
			case DOWN	: row++; prow--;
						break;
			case LEFT 	: column--; pcolumn++;
						break;
			default 	: break;
		}

		Boolean retValue = false;

		try
		{
			retValue = (((noCrate[row][column]==Cells.FLOOR) || (noCrate[row][column]==Cells.GOAL)) &&
						((noCrate[prow][pcolumn]==Cells.FLOOR) || (noCrate[prow][pcolumn]==Cells.GOAL)));
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return retValue;
	}

	private Boolean isGoal(Node node)
	{
		return(board.getBoard()[node.getY()][node.getX()]==Cells.GOAL);
	}

	public void searchForAPath(int column, int row)
	{
		Node node = boardNodes[row][column];

		int bound = node.getHeuristic();
	}

	private void search()
	{

	}
}