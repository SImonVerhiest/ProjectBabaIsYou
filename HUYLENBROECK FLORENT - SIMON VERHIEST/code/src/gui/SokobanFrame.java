package gui;

import java.util.Arrays;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BorderLayout;
import java.awt.Dimension;

import sokobanUtilities.*;
import game.SokoSave;
import generator.*;

/**
* JFrame extension that shows the menu's of Sokoban
*
* @author 	HUYLENBROECK Florent
*/
public class SokobanFrame extends JFrame
{
	private int width = (int)SokobanUtilities.getMenuWidth();
	private int height = (int)SokobanUtilities.getMenuHeight();

	private JButton play;
	private JButton packs;
	private JButton generator;
	private JButton _import;
	private JButton original;
	private JButton extra;
	private JButton load;
	private JButton exit;
	private JButton back;
	private JButton easy;
	private JButton medium;
	private JButton hard;
	private JButton elite;

	private Menus current = Menus.MAIN_MENU;

	private ArrayList<JButton> buttonList = new ArrayList<JButton>();

	private String path=SokobanUtilities.getPathToCode();

	/**
	* File chooser that is used to load, save and import levels
	*/
	private SokoFileChooser _SFC = new SokoFileChooser(this);

	public SokobanFrame()
	{
		this.setTitle("Sokoban - By HUYLENBROECK Florent & VERHIEST Simon");
		this.setResizable(false);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(width, height));
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.setFocusable(true);
		this.path=SokobanUtilities.getPathToCode();

		play = new JButton("PLAY");
		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				initLevelMenu();
			}
		}); 

		packs = new JButton("PACKS");
		packs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				initPackSelect();
			}
		});

		generator = new JButton("GENERATOR");
		generator.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				initGeneratorMenu();
			}
		});

		_import = new JButton("IMPORT");
		_import.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				String importedPath = _SFC._import();
				if(importedPath!=null)
					SokobanFrame.this.launchGameFrame(importedPath);
			}
		}); 

		original = new JButton("ORIGINAL");
		original.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				initLevelSelect("original");
			}
		});

		extra = new JButton("EXTRA");
		extra.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				initLevelSelect("extra");
			}
		});

		load = new JButton("LOAD");
		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				SokobanFrame.this.load();
			}
		}); 

		back = new JButton("BACK");
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				switch(current)
				{
					case LEVEL_MENU 	: initMainMenu();
										break;
					case PACK_MENU 		: initLevelMenu();
										break;
					case LEVEL_SELECT 	: initPackSelect();
										break;
					case GENERATOR_MENU	: initLevelMenu();
										break;
				}
			}
		}); 
		         
		exit = new JButton("EXIT");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				int quit = new JOptionPane().showConfirmDialog(SokobanFrame.this, "Quit ?", "Quitting", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);

				if (quit==JOptionPane.OK_OPTION)
					System.exit(0);
			}
		}); 

		easy = new JButton("EASY");
		easy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				launchGameFrame(Generator.generate(Difficulty.EASY));
			}
		}); 

		medium = new JButton("MEDIUM");
		medium.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				launchGameFrame(Generator.generate(Difficulty.MEDIUM));
			}
		}); 

		hard = new JButton("HARD");
		hard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				/*GeneratorThread thread = new GeneratorThread(Difficulty.HARD);
				try
				{
					thread.join();
				} catch(InterruptedException i) {
					//
				}
				String levelPath = thread.getPath();
				System.out.println(levelPath);

				launchGameFrame(levelPath);*/

				launchGameFrame(Generator.generate(Difficulty.HARD));
			}
		}); 

		elite = new JButton("ELITE");
		elite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				launchGameFrame(Generator.generate(Difficulty.ELITE));
			}
		}); 

		initMainMenu();
	}

	public SokoFileChooser getSFC()
	{
		return _SFC;
	}

	/**
	* Initializes the main menu
	*/
	protected void initMainMenu()
	{
		current = Menus.MAIN_MENU;

		buttonList.clear();
		buttonList.add(play);
		buttonList.add(load);
		buttonList.add(exit);
		createMenu();
	}	

	/**
	* Initializes the level menu
	*/
	public void initLevelMenu()
	{
		current = Menus.LEVEL_MENU;

		buttonList.clear();
		buttonList.add(packs);
		buttonList.add(_import);
		buttonList.add(generator);
		buttonList.add(back);
		createMenu();
	}

	/**
	* Initializes the pack select
	*/
	public void initPackSelect()
	{
		current = Menus.PACK_MENU;

		buttonList.clear();
		buttonList.add(original);
		buttonList.add(extra);
		buttonList.add(back);
		createMenu();
	}

	/**
	* Initializes the generator menu
	*/
	public void initGeneratorMenu()
	{
		current = Menus.GENERATOR_MENU;

		buttonList.clear();
		buttonList.add(easy);
		buttonList.add(medium);
		buttonList.add(hard);
		buttonList.add(elite);
		buttonList.add(back);
		createMenu();
	}

	/**
	* Initializes the level select
	*
	* @param pack 	String, the name of the pack of level
	*/
	public void initLevelSelect(String pack)
	{
		current = Menus.LEVEL_SELECT;

		this.getContentPane().removeAll();
		this.setContentPane(new LevelSelect(this, pack));
		this.revalidate();
	}

	/**
	* Creates a ButtonMenu with the elements in the ButttonList
	*/
	public void createMenu()
	{
		this.getContentPane().removeAll();
		this.setContentPane(new ButtonMenu(this, current.getPath(), buttonList));
		this.pack();
	}

	/**
	* Launches a GameFrame from an absolute path
	*
	* @param path 	String, absolute path to the .xsb file showing the level
	*/
	public void launchGameFrame(String path)
	{
		this.setVisible(false);
		GameFrame levelPlayed = new GameFrame(this, path);
	}

	/**
	* Launches a GameFrame from a SokoSave
	*
	* @param save 		SokoSave, contains path to a .xsb file and a .mov file
	*/
	public void launchGameFrame(SokoSave save)
	{
		this.setVisible(false);
		GameFrame levelPlayed = new GameFrame(this, save);
	}

	/**
	* Loads a level
	*/
	public void load()
	{
		SokoSave save = _SFC.load();
		if(save!=null)
			launchGameFrame(save);
	}

	/**
	* Allows to save the current state of a level
	*
	* @param toSave 	SokobanSave to save
	*/
	public void save(SokoSave toSave)
	{
		_SFC.save(toSave);
	}
}