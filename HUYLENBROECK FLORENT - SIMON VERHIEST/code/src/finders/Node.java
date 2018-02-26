package finders;

import game.Moves;

public class Node
{
	private int x;
	private int y;
	private int cost;
	private int heuristic;
	private Node prev;
	private Moves lastMove;
	private int playerX;
	private int playerY;

	public Node(int x, int y)
	{
		this.x=x;
		this.y=y;
		prev=null;
		cost=0;
		heuristic=0;
		lastMove=null;
		playerX=x;
		playerY=y;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getCost()
	{
		return cost;
	}

	public int getHeuristic()
	{
		return heuristic;
	}

	public Node getPrev()
	{
		return prev;
	}

	public int getPlayerX()
	{
		return playerX;
	}

	public int getPlayerY()
	{
		return playerY;
	}

	public void setCost(int cost)
	{
		this.cost=cost;
	}

	public void setLastMove(Moves move)
	{
		this.lastMove=move;
		figureOutPlayerPos();
	}

	public void setHeuristic(int heuristic)
	{
		this.heuristic=heuristic;
	}

	public void setPrev(Node prev)
	{
		this.prev=prev;
	}

	private void figureOutPlayerPos()
	{
		int px = x;
		int py = y;

		switch(lastMove)
		{
			case UP		: py--;
						break;
			case RIGHT	: px++;
						break;
			case DOWN	: py++;
						break;
			case LEFT 	: px--;
						break;
		}

		playerX=px;
		playerY=py;
	}
}