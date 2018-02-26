package gui;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import game.*;

/**
* JMenuBar extension that contain some option for the GameFrame component
*
* @author 	HUYLENBROECK Florent
*/
public class SokoMenuBar extends JMenuBar
{
	private GameFrame parent;
	private BoardPanel game;

	private JMenu files = new JMenu("Files");

	private JMenuItem save = new JMenuItem("Save");

	/**
	* Constructor
	*
	* @param parent 	GameFrame, parent containing this component
	*/
	public SokoMenuBar(GameFrame parent)
	{
		this.parent=parent;
		game = parent.getLevel();

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				SokoSave toSave = new SokoSave(game.getBoard().getPath(), game.getBoard().saveState());
				SokoMenuBar.this.parent.getParent().save(toSave);
			}            
		});

		files.add(save);

		this.add(files);
	}
}