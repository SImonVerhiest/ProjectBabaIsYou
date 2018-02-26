package generator;

import java.util.Random;

public enum Difficulty
{
	EASY(9,2),
	MEDIUM(12,4),
	HARD(15,6),
	ELITE(18,10);

	private final int minSize;
	private final int minGoals;

	private Random rn = new Random();

	Difficulty(int minSize, int minGoals)
	{
		this.minSize=minSize;
		this.minGoals=minGoals;
	}

	public int rows()
	{
		return (rn.nextInt(4))+minSize;
	}

	public int columns()
	{
		return (rn.nextInt(4))+minSize;
	}

	public int goals()
	{
		return (int)(rn.nextInt(minGoals)+minGoals/2);
	}
}