package generator;

public class GeneratorThread extends Thread
{
	private String path;

	private Difficulty difficulty=Difficulty.EASY;

	public GeneratorThread(Difficulty difficulty)
	{
		this.difficulty=difficulty;
	}

	public void run(Difficulty difficulty)
	{
		this.path =  Generator.generate(difficulty);
	}

	public String getPath()
	{
		return path;
	}
}