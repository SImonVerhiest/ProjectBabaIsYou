package game;

import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.io.File;

import sokobanUtilities.*;

public class BoardTest
{
	@Test
	public void loading_xsb_and_mov()
	{
		Board board=null;
		String mov="";
		Boolean loading = true;
		try
		{
			board = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/loading/level.xsb");
			mov = SokobanUtilities.loadMov(SokobanUtilities.getPathToCode()+"/code/resources/tests/loading/mov.mov");
		} catch (IOException e) {
			e.printStackTrace();
			loading = false;
		}



		Assert.assertTrue("Echec de lecture du fichier", loading==true);
		Assert.assertEquals("Les plateaux ne sont pas identiques",
			new Cells[][] {{Cells.WALL,Cells.WALL,Cells.WALL,Cells.WALL,Cells.WALL},
							{Cells.WALL,Cells.PLAYER,Cells.CRATE,Cells.GOAL,Cells.WALL},
							{Cells.WALL,Cells.WALL,Cells.WALL,Cells.WALL,Cells.WALL}},
			board.getBoard());
	}

	@Test
	public void moving_on_the_board()
	{
		Board board=null;
		String mov="";
		Board expected=null;

		try
		{
			board = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_regular/level.xsb");
			mov= SokobanUtilities.loadMov(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_regular/mov.mov");
			expected = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_regular/expected.xsb");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		board.applyMov(mov);

		Assert.assertEquals("Erreur lors d'un deplacement simple", board.getBoard(), expected.getBoard());

	}

	@Test
	public void moving_against_wall()
	{
		Board board=null;
		String mov="";
		Board expected=null;

		try
		{
			board = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_against_wall/level.xsb");
			mov= SokobanUtilities.loadMov(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_against_wall/mov.mov");
			expected = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_against_wall/expected.xsb");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		board.applyMov(mov);

		Assert.assertEquals("Erreur lors d'un deplacement contre un mur", board.getBoard(), expected.getBoard());

	}

	@Test
	public void moving_crate()
	{
		Board board=null;
		String mov="";
		Board expected=null;

		try
		{
			board = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_crate/level.xsb");
			mov= SokobanUtilities.loadMov(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_crate/mov.mov");
			expected = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_crate/expected.xsb");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		board.applyMov(mov);

		Assert.assertEquals("Erreur lors du deplacement d'une caisse", board.getBoard(), expected.getBoard());

	}

	@Test
	public void moving_on_goal()
	{
		Board board=null;
		String mov="";
		Board expected=null;

		try
		{
			board = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_goal/level.xsb");
			mov= SokobanUtilities.loadMov(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_goal/mov.mov");
			expected = new Board(SokobanUtilities.getPathToCode()+"/code/resources/tests/move_goal/expected.xsb");
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		board.applyMov(mov);

		Assert.assertEquals("Erreur lors du deplacement d'une caisse sur un goal", board.getBoard(), expected.getBoard());

	}

}