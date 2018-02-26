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
public class AStarPathFinder
{
	private Board board;
	private Cells[][] noCrate;

	private ArrayList<Node> closedList = new ArrayList<Node>();
	private ArrayList<Node> openList = new ArrayList<Node>();

	private ArrayList<Node> start = new ArrayList<Node>();

	/**
	* Constructor
	*
	* @param board 	Board, showing the level
	*/
	public AStarPathFinder(Board board)
	{
		noCrate = removeCrates(board);
	}

	public AStarPathFinder(Cells[][] noCrate)
	{
		this.noCrate=noCrate;
	}

	/**
	* Initializes the searching by creating a new Board of Nodes
	*
	* @return 	Node[][], the new Board of Nodes
	*/
	private Node[][] initSearch()
	{
		Node[][] boardNodes = new Node[noCrate.length][noCrate[0].length];

		for (int i=0 ; i<noCrate.length ; i++)
		{
			for (int j=0 ; j<noCrate[0].length ; j++)
			{
				boardNodes[i][j] = new Node(j, i);
			}
		}

		return boardNodes;
	}

	/**
	* Looks at all goals: if there are multiple adjacent goals, only one is kept
	*/
	private void simplificateStart(Node[][] boardNodes)
	{
		Node toCheck;
		int x;
		int y;

		for(int i=0; i<start.size(); )
		{
			toCheck = start.get(i);
			x = toCheck.getX();
			y = toCheck.getY();

			if ((noCrate[y-1][x]==Cells.GOAL && start.contains(boardNodes[y-1][x])) ||
				(noCrate[y][x+1]==Cells.GOAL && start.contains(boardNodes[y][x+1])) ||
				(noCrate[y+1][x]==Cells.GOAL && start.contains(boardNodes[y+1][x])) ||
				(noCrate[y][x-1]==Cells.GOAL && start.contains(boardNodes[y][x-1])))
				start.remove(i);
			else
				i++;
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
	public Boolean canBePulled(Moves move, int column, int row)
	{
		int prow = row;
		int pcolumn = column;

		switch(move)
		{
			case UP		: row--; prow=prow-2;
						break;
			case RIGHT	: column++; pcolumn=pcolumn+2;
						break;
			case DOWN	: row++; prow=prow+2;
						break;
			case LEFT 	: column--; pcolumn=pcolumn-2;
						break;
			default 	: break;
		}

		try
		{
			if (noCrate[row][column]==Cells.WALL)
				return false;

			if (noCrate[row][column]==Cells.GOAL)
				return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		Boolean retValue = false;

		try
		{
			retValue = ((noCrate[row][column]==Cells.FLOOR) && 
						((noCrate[prow][pcolumn]==Cells.FLOOR) || (noCrate[prow][pcolumn]==Cells.GOAL)));
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return retValue;
	}

	/**
	* Finds out if a move from one cell to another adjacent one is allowed
	*
	* @param move 		Moves, the direction to take
	* @param column 	int, the starting column
	* @param row 		int, the starting row
	* @return 			Boolean: true if the move is allowed, false otherwise
	*/
	private Boolean canGo(Moves move, int column, int row)
	{
		switch(move)
		{
			case UP		: row--;
						break;
			case RIGHT	: column++;
						break;
			case DOWN	: row++;
						break;
			case LEFT 	: column--;
						break;
			default 	: break;
		}

		Boolean retValue = false;

		try
		{
			retValue=(noCrate[row][column]!=Cells.WALL);
		} catch (ArrayIndexOutOfBoundsException e) {
			return false;
		}

		return retValue;
	}

	/**
	* Finds out if a path to a certain Cells of the Board exists by pulling a crate
	*
	* @param column 	int, the column of the Cells to reach
	* @param row 		int, the row of the Cells to reach
	* @return 			ArrayList of Point, the path that leads to the destination, if it exists
	*/
	public Boolean canFindAReversedWay(int column, int row)
	{
		Node[][] boardNodes = initSearch();

		for (int i=0 ; i<noCrate.length ; i++)
		{
			for (int j=0 ; j<noCrate[0].length ; j++)
			{
				if (noCrate[i][j]==Cells.GOAL)
					start.add(boardNodes[i][j]);
			}
		}

		simplificateStart(boardNodes);

		Node dest = boardNodes[row][column];

		for(int i=0; i<start.size(); i++)
		{
			Node startNode = start.get(i);

			if ((dest.getX()==startNode.getX()) && (dest.getY()==startNode.getY()))
				return true;

			openList.clear();
			closedList.clear();
			openList.add(startNode);

			while(openList.size()>0)
			{
				Node node=openList.get(0);

				if(node.getX() == dest.getX() && node.getY() == dest.getY())
				{
					dest=node;
					break;
				}

				openList.remove(node);

				int nextNodeCost = node.getCost()+1;

				for(Moves move : Moves.values())
				{
					int y=node.getY();
					int x=node.getX();

					if(canBePulled(move, x, y))
					{
						switch(move)
						{
							case UP		: y--;
										break;
							case RIGHT	: x++;
										break;
							case DOWN	: y++;
										break;
							case LEFT 	: x--;
										break;
						}

						if (closedList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() <= nextNodeCost)
							continue;
						else if (closedList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() > nextNodeCost)
							closedList.remove(boardNodes[y][x]);
						if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() <= nextNodeCost)
							continue;
						else if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() > nextNodeCost)
							openList.remove(boardNodes[y][x]);

						boardNodes[y][x].setCost(nextNodeCost);

						int distance = (Math.abs(dest.getX()-x)) + (Math.abs(dest.getY()-y));

						boardNodes[y][x].setHeuristic(nextNodeCost + distance);

						if ((boardNodes[y][x].getPrev()==null) ||
							(boardNodes[y][x].getPrev().getHeuristic()>node.getHeuristic()))
							boardNodes[y][x].setPrev(node);

						openList.add(boardNodes[y][x]);

						Collections.sort(openList, new Comparator<Node>(){
							public int compare(Node node1, Node node2)
							{
								if (node1.getHeuristic() > node2.getHeuristic())
									return 1;
								else if (node1.getHeuristic() == node2.getHeuristic())
									return 0;
								else 
									return -1;
							}
						});

					}
				}
				closedList.add(node);
			}
			if(dest.getPrev()!=null)
					break;
		}
		return (dest.getPrev()!=null);
	}

	/**
	* Finds out if a path to a certain Cells of the Board exists by pulling a crate
	*
	* @param column 	int, the column of the Cells to reach
	* @param row 		int, the row of the Cells to reach
	* @return 			ArrayList of Point, the path that leads to the destination, if it exists
	*/
	/*public Boolean canFindAReversedWay(int startColumn, int startRow, int targetColumn, int targetRow)
	{
		Node[][] boardNodes = initSearch();

		Node dest = boardNodes[targetRow][targetColumn];
		Node startNode = boardNodes[startRow][startColumn];

		openList.clear();
		closedList.clear();
		openList.add(startNode);

		while(openList.size()>0)
		{
			Node node=openList.get(0);

			if(node==dest)
			{
				break;
			}

			openList.remove(node);

			int nextNodeCost = node.getCost()+1;

			for(Moves move : Moves.values())
			{
				int y=node.getY();
				int x=node.getX();

				if(canBePulled(move, x, y))
				{
					switch(move)
					{
						case UP		: y--;
									break;
						case RIGHT	: x++;
									break;
						case DOWN	: y++;
									break;
						case LEFT 	: x--;
									break;
					}

					if(closedList.contains(boardNodes[y][x]) && openList.contains(boardNodes[y][x]))
					{
						openList.remove(boardNodes[y][x]);
						continue;
					}
					if (closedList.contains(boardNodes[y][x]))
						continue;
					if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() <= nextNodeCost)
						continue;
					else if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() > nextNodeCost)
						openList.remove(boardNodes[y][x]);

					boardNodes[y][x].setCost(nextNodeCost);

					int distance = (Math.abs(dest.getX()-x)) + (Math.abs(dest.getY()-y));

					boardNodes[y][x].setHeuristic(nextNodeCost + distance);


					if ((boardNodes[y][x].getPrev()==null) ||
						(boardNodes[y][x].getPrev().getHeuristic()>node.getHeuristic()))
						boardNodes[y][x].setPrev(node);

					openList.add(boardNodes[y][x]);

					Collections.sort(openList, new Comparator<Node>(){
						public int compare(Node node1, Node node2)
						{
							if (node1.getHeuristic() > node2.getHeuristic())
								return 1;
							else if (node1.getHeuristic() == node2.getHeuristic())
								return 0;
							else 
								return -1;
						}
					});

				}
			}
			closedList.add(node);
		}
		return (dest.getPrev()!=null);
	}*/
	

	/**
	* Finds out if a path to a certain Cells of the Board exists by pulling a crate
	*
	* @param startColumn 	int, the column of the starting Cells
	* @param startRow 		int, the row of the starting Cells
	* @param targetColumn 	int, the column of the Cells to reach
	* @param targetRow 		int, the row of the Cells to reach
	* @return 				ArrayList of Point, the path that leads to the destination, if it exists
	*/
	public ArrayList<Point> findAReversedWay(int startColumn, int startRow, int targetColumn, int targetRow)
	{
		Node[][] boardNodes = initSearch();

		Node dest = boardNodes[targetRow][targetColumn];
		Node startNode = boardNodes[startRow][startColumn];

		openList.clear();
		closedList.clear();
		openList.add(startNode);

		while(openList.size()>0)
		{
			Node node=openList.get(0);

			if(node==dest)
				break;

			openList.remove(node);

			int nextNodeCost = node.getCost()+1;

			for(Moves move : Moves.values())
			{
				int y=node.getY();
				int x=node.getX();

				if(canBePulled(move, x, y))
				{
					switch(move)
					{
						case UP		: y--;
									break;
						case RIGHT	: x++;
									break;
						case DOWN	: y++;
									break;
						case LEFT 	: x--;
									break;
					}

					try
					{
						if(closedList.contains(boardNodes[y][x]) && openList.contains(boardNodes[y][x]))
						{
							openList.remove(boardNodes[y][x]);
							continue;
						}
						if (closedList.contains(boardNodes[y][x]))
							continue;
						if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() <= nextNodeCost)
							continue;
						else if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() > nextNodeCost)
							openList.remove(boardNodes[y][x]);

						boardNodes[y][x].setCost(nextNodeCost);
						boardNodes[y][x].setLastMove(move);

						int distance = (Math.abs(dest.getX()-x)) + (Math.abs(dest.getY()-y));

						boardNodes[y][x].setHeuristic(nextNodeCost + distance);

						if ((boardNodes[y][x].getPrev()==null) ||
							(boardNodes[y][x].getPrev().getHeuristic()>node.getHeuristic()))
							boardNodes[y][x].setPrev(node);

						openList.add(boardNodes[y][x]);
					} catch(ArrayIndexOutOfBoundsException e) {
						continue;
					}

					Collections.sort(openList, new Comparator<Node>(){
						public int compare(Node node1, Node node2)
						{
							if (node1.getHeuristic() > node2.getHeuristic())
								return 1;
							else if (node1.getHeuristic() == node2.getHeuristic())
								return 0;
							else 
								return -1;
						}
					});
				}
			}
			closedList.add(node);
		}

		ArrayList<Point> path = new ArrayList<Point>();

		Node pos = dest;

		if (dest.getPrev()==null)
			return path;

		path.add(new Point(dest.getPlayerX(), dest.getPlayerY()));

		while (pos!=startNode)
		{
			path.add(0, new Point(pos.getX(), pos.getY()));
			pos = pos.getPrev();
		}

		return path;
	}

	/**
	* Finds out if a path to a certain Cells of the Board exists
	*
	* @param startColumn 	int, the column of the starting Cells
	* @param startRow 		int, the row of the starting Cells
	* @param targetColumn 	int, the column of the Cells to reach
	* @param targetRow 		int, the row of the Cells to reach
	* @return 				ArrayList of Point, the path that leads to the destination, if it exists
	*/
	public Boolean canFindAWay(int startColumn, int startRow, int targetColumn, int targetRow)
	{
		if (startColumn==targetColumn && startRow==targetRow)
			return true;

		Node[][] boardNodes = initSearch();

		Node dest = boardNodes[targetRow][targetColumn];
		Node startNode = boardNodes[startRow][startColumn];

		openList.clear();
		closedList.clear();
		openList.add(startNode);

		while(openList.size()>0)
		{
			Node node=openList.get(0);

			if(node==dest)
				break;

			openList.remove(node);

			int nextNodeCost = node.getCost()+1;

			for(Moves move : Moves.values())
			{
				int y=node.getY();
				int x=node.getX();

				if(canGo(move, x, y))
				{
					switch(move)
					{
						case UP		: y--;
									break;
						case RIGHT	: x++;
									break;
						case DOWN	: y++;
									break;
						case LEFT 	: x--;
									break;
					}


					if (closedList.contains(boardNodes[y][x]))
						continue;

					if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() <= nextNodeCost)
						continue;
					else if (openList.contains(boardNodes[y][x]) && boardNodes[y][x].getCost() > nextNodeCost)
						openList.remove(boardNodes[y][x]);

					boardNodes[y][x].setCost(nextNodeCost);

					int distance = (Math.abs(dest.getX()-x)) + (Math.abs(dest.getY()-y));

					boardNodes[y][x].setHeuristic(nextNodeCost + distance);

					if ((boardNodes[y][x].getPrev()==null) ||
						(boardNodes[y][x].getPrev().getHeuristic()>node.getHeuristic()))
						boardNodes[y][x].setPrev(node);

					openList.add(boardNodes[y][x]);

					Collections.sort(openList, new Comparator<Node>(){
						public int compare(Node node1, Node node2)
						{
							if (node1.getHeuristic() > node2.getHeuristic())
								return 1;
							else if (node1.getHeuristic() == node2.getHeuristic())
								return 0;
							else 
								return -1;
						}
					});
				}
			}
			closedList.add(node);
		}
		return (dest.getPrev()!=null);
	}
}