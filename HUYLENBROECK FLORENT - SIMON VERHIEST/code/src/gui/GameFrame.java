package gui;

import javax.swing.JFrame;
import java.awt.Container;
import game.*;

/**
* JFrame extension that contains the level of Sokoban chosen by the user
*
* @author 	HUYLENBROECK Forent
*/
public class GameFrame extends JFrame
{
	/**
	* Parent of the GameFrame
	*/
	private SokobanFrame parent;
	private SokoMenuBar sokoMenuBar;
	private BoardPanel level;

	/**
	* Constructor
	*
	* @param parent 	SokobanFrame from which this frame is launched
	* @param path 		String, absolute path to the .xbs file showing the level chosen by the user
	*/
	public GameFrame(SokobanFrame parent, String path)
	{
		this.parent = parent;

		this.setTitle("Sokoban - By HUYLENBROECK Florent & VERHIEST Simon");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		level = new BoardPanel(this, path);
		this.setContentPane(level);
		this.setJMenuBar(new SokoMenuBar(this));

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setFocusable(true);
		this.setVisible(true);
		this.getContentPane().requestFocus();
	}

	/**
	* Constructor
	*
	* @param parent 	SokobanFrame from which this frame is launched
	* @param save 		SokoSave, contains path to a .xsb file and a .mov file
	*/
	public GameFrame(SokobanFrame parent, SokoSave save)
	{
		this.parent = parent;

		this.setTitle("Sokoban - By HUYLENBROECK Florent & VERHIEST Simon");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		level = new BoardPanel(this, save);
		this.setContentPane(level);
		this.setJMenuBar(new SokoMenuBar(this));

		this.pack();
		this.setLocationRelativeTo(parent);
		this.setFocusable(true);
		this.setVisible(true);
		this.getContentPane().requestFocus();
	}

	public SokobanFrame getParent()
	{
		return parent;
	}

	public BoardPanel getLevel()
	{
		return level;
	}
}