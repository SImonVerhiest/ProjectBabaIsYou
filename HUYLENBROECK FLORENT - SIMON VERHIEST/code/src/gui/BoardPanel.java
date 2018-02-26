package gui;

import game.*;
import java.util.Arrays;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import sokobanUtilities.*;

/**
* JPanel extension that contains a keyboard-playable level of Sokoban 
*
* @author 	HUYLENBROECK Florent
*/
public class BoardPanel extends JPanel
{
	private GameFrame parent;

	private Boolean addNextLevelButton=true;
	private Boolean addPrevLevelButton=true;

	private String path;
	private String nextLevelPath;
	private String prevLevelPath;

	private Board board;
	private GridBagConstraints levelgbc;
	private GridBagConstraints buttongbc;
	private GridBagConstraints gbc;

	private KeyListener _BKL = new BoardKeyListener();

	private JPanel levelPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();

	private JButton back = new JButton();
	private JButton undo = new JButton();
	private JButton next = new JButton();
	private JButton prev = new JButton();

	private final double MAX_WIDTH = SokobanUtilities.getMenuWidth();
	private final double MAX_HEIGHT = SokobanUtilities.getMenuHeight();

	private Boolean deadlocked = false;

	/**
	* Constructor
	*
	* @param parent 		GameFrame, Frame containing this JPanel
	* @param path 	String, absolute path to the .xsb file showing the level
	*/
	public BoardPanel(GameFrame parent, String path)
	{
		this.parent = parent; 

		try
		{
			board = new Board(path);
		} catch(IOException e) {
			System.out.println("..");
			throw new RuntimeException(e);
		}

		this.path=path;

		SokobanUtilities.createSavePath(path);

		levelPanel.setLayout(new GridBagLayout());
		levelgbc = new GridBagConstraints();

		int maxSize = (int)Math.min(MAX_WIDTH/board.getColumns(), MAX_HEIGHT/board.getRows());

		for (int i=0; i<board.getRows(); i++)
		{
			for (int j=0; j<board.getColumns(); j++)
			{
				levelgbc.gridx = j;
    			levelgbc.gridy = i;

    			CellLabel lab = new CellLabel(board.getBoard()[i][j].getImage(), j, i, maxSize);

    			levelPanel.add(lab, levelgbc);
			}
		}

		nextLevelPath = SokobanUtilities.nextLevelPath(path);
		prevLevelPath = SokobanUtilities.prevLevelPath(path);

		addNextLevelButton=(new File(nextLevelPath).exists() && !(nextLevelPath==""));
		addPrevLevelButton=(new File(prevLevelPath).exists() && !(prevLevelPath==""));

		Dimension buttonDimension = new Dimension((int)((maxSize*board.getColumns())/4), maxSize);

		prev = new JButton("<< PREVIOUS");
		prev.setPreferredSize(buttonDimension);
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BoardPanel.this.parent.dispose();
				BoardPanel.this.parent.getParent().launchGameFrame(prevLevelPath);
			}
		});

		back = new JButton("BACK");
		back.setPreferredSize(buttonDimension);
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BoardPanel.this.parent.dispose();
				BoardPanel.this.parent.getParent().setVisible(true);
			}
		});

		next = new JButton("NEXT >>");
		next.setPreferredSize(buttonDimension);
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				BoardPanel.this.parent.dispose();
				BoardPanel.this.parent.getParent().launchGameFrame(nextLevelPath);
			}
		});

		undo = new JButton("UNDO");
		undo.setPreferredSize(buttonDimension);
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				board.undo();
				update();

				KeyListener[] listeners = BoardPanel.this.getListeners(KeyListener.class);


				if(!(Arrays.asList(listeners).contains(_BKL)))
					BoardPanel.this.addKeyListener(_BKL);

				if(deadlocked)
					deadlocked=false;

				BoardPanel.this.requestFocus();
			}
		});

		buttonPanel.setPreferredSize(new Dimension((int)(maxSize*board.getColumns()), maxSize));
		buttonPanel.setLayout(new GridBagLayout());
		buttongbc = new GridBagConstraints();

		buttongbc.gridx=0;
		buttongbc.gridy=0;
		buttongbc.fill=GridBagConstraints.HORIZONTAL;
		if (addPrevLevelButton)
			buttonPanel.add(prev, buttongbc);

		buttongbc.gridx=1;
		buttonPanel.add(back, buttongbc);

		buttongbc.gridx=2;
		buttonPanel.add(undo, buttongbc);

		buttongbc.gridx=3;
		if (addNextLevelButton)
			buttonPanel.add(next, buttongbc);

		this.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();

		gbc.gridx=0;
		gbc.gridy=0;
		gbc.fill=GridBagConstraints.BOTH;
		this.add(levelPanel, gbc);

		gbc.gridx=0;
		gbc.gridy=1;
		gbc.fill=GridBagConstraints.NONE;
		gbc.anchor=GridBagConstraints.LINE_START;
		gbc.insets=new Insets(2,2,2,2);
		this.add(buttonPanel, gbc);

		this.addKeyListener(_BKL);

		this.revalidate();
		this.repaint();

		this.setFocusable(true);
	}

	/**
	* Constructor
	*
	* @param parent 	GameFrame, Frame containing this JPanel
	* @param save 		SokoSave, contains path to a .xsb file and a .mov file
	*/
	public BoardPanel(GameFrame parent, SokoSave save)
	{
		this(parent, save.getXsbPath());
		try
		{
			board.applyMov(SokobanUtilities.loadMov(save.getMovPath()));
		} catch (IOException e) {
			parent.dispose();
			JOptionPane ioerror = new JOptionPane();
			ioerror.showMessageDialog(this.parent.getParent(), "error loading level file", "error", JOptionPane.PLAIN_MESSAGE);
			throw new RuntimeException(e);
		}

		updateAll();
	}

	public Board getBoard()
	{
		return board;
	}

	/**
	* Replaces a component that's part of the gridBagLayout
	*
	* @param pos	int, position of the component in the gridBagLayout
	*/
	private void changeComponent(int pos)
	{
		CellLabel toChange = (CellLabel)levelPanel.getComponent(pos);

		int i = (int) toChange.getClientProperty("row");
		int j = (int) toChange.getClientProperty("column");

		toChange.changeImage(board.getBoard()[i][j].getImage());

		this.revalidate();
		this.repaint();
	}

	/**
	* Updates the board according to the list of to-replace cells it contains
	*/
	private void update()
	{
		while (!board.movedCells.isEmpty())
		{
			int x = (int)board.movedCells.get(0).getX();
			int y = (int)board.movedCells.get(0).getY();

			if (board.getDf().findDeadlocks(x, y))
				deadlocked=true;

			int pos=(y*board.getColumns()) + x;
			
			changeComponent(pos);
			board.movedCells.remove(0);
		}
	}

	/**
	* Updates every cells of the board
	*/
	private void updateAll()
	{
		for(int i=0; i<board.getRows(); i++)
		{
			for(int j=0; j<board.getColumns(); j++)
			{
				changeComponent(i*board.getColumns()+j);
			}
		}
	}

	/**
	* Finds out if the game has ended and reacts accordingly
	*/
	private void gameStage()
	{
		if (deadlocked)
		{
			JOptionPane gameOver = new JOptionPane();
			gameOver.showMessageDialog(this, "Une ou plusieurs caisses sont bloquees", "GAME OVER", JOptionPane.PLAIN_MESSAGE);
			this.removeKeyListener(_BKL);
		}

		if (board.isDone())
		{
			JOptionPane gameWon = new JOptionPane();
			gameWon.showMessageDialog(this, "Bravo !", "GAME WON", JOptionPane.PLAIN_MESSAGE);
			this.removeKeyListener(_BKL);
		}
	}

	/**
	* KeyListener implementation that moves the player in the Sokoban level show by the BoardPan
	*
	* @author 	Huylenbroeck Florent - Verhiest Simon
	*/
	private class BoardKeyListener implements KeyListener
	{
		@Override
		public void keyPressed(KeyEvent e) 
		{
			int keyCode = e.getKeyCode();

			switch (keyCode)
			{
				case 81 : ; // Q
				case 37 : board.move(Moves.LEFT, true); // LEFT arrow
						break;
				case 90 : ; // Z
				case 38 : board.move(Moves.UP, true); // UP arrow
						break;
				case 68 : ; // D 
				case 39 : board.move(Moves.RIGHT, true); // RIGHT arrow
						break;
				case 83 : ; // S
				case 40 : board.move(Moves.DOWN, true); // DOWN arrow
				default : break;
			}

			update();
			gameStage();
		}

		public void keyReleased(KeyEvent e) {}
		public void keyTyped(KeyEvent e) {}
	}
}