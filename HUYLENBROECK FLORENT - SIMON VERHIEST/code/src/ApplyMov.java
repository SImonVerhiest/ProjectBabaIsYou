import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import game.*;
import sokobanUtilities.*;

public class ApplyMov
{
	private static String movString;
	private static Board board;

	public static void main(String[] args)
	{

		try
		{
			board = new Board(args[0]);
		} catch (IOException e) {
			System.out.println("Error : Can't read input file");
		}

		try
		{
			movString=SokobanUtilities.loadMov(args[1]);
		} catch (IOException e) {
			System.out.println("Error : Can't read mov file");
		}

		board.applyMov(movString);

		board.saveStateAsXSB(args[2]);
	}
}