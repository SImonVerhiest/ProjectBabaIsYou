package gui;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.*;
import java.awt.HeadlessException;

import sokobanUtilities.*;
import game.*;

/**
* JFileChooser extension that allows saving, loading and importing sokoban level
*
* @author 	HUYLENBROECK Florent
*/
public class SokoFileChooser extends JFileChooser
{
	private SokobanFrame parent; 
	private String path;
	private SokoSave save;
	private final String XSB = "xsb";
	private final String SAV = "sav";
	private FileNameExtensionFilter xsb = new FileNameExtensionFilter("XSB files", XSB);
	private FileNameExtensionFilter sav = new FileNameExtensionFilter("SAV files", SAV);

	/**
	* Constructor
	*
	* @param parent 	SokobanFrame that is the parent component of this 
	*/
	public SokoFileChooser(SokobanFrame parent)
	{
		super();
		this.parent=parent;
	}

	/**
	* Allows the importation of Sokoban level
	*
	* @return 	String, the aboslute path to the imported .xsb file
	*/
	protected String _import()
	{
		this.setFileFilter(xsb);
		this.setCurrentDirectory(new File(SokobanUtilities.getPathToCode()+"/code/levels/imported"));

		int action = 0;
		try
		{
			action = this.showOpenDialog(this);
		} catch (HeadlessException e) {
			throw new IllegalStateException(e);
		}

		File importedFile = this.getSelectedFile();

		if(importedFile==null)
			return null;

		File movedToImported = new File(moveToImported(importedFile));

		try	
		{
			if(!(movedToImported.exists()))
				movedToImported.createNewFile();

			Files.copy(Paths.get(importedFile.getAbsolutePath()), Paths.get(movedToImported.getAbsolutePath()), REPLACE_EXISTING);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}

		if(!compareExtension(importedFile, XSB))
		{
			JOptionPane extensionError = new JOptionPane();
			extensionError.showMessageDialog(this, "Invalid file, please select a .xsb file", "extension error", JOptionPane.PLAIN_MESSAGE);
			return null;
		}

	    if (action == JFileChooser.APPROVE_OPTION)
		    path = movedToImported.getAbsolutePath();
		else
			return null;

		return path;
	}

	/**
	* Allows to load one's level progress 
	*
	* @return 	SokoSave, the save being loaded
	*/
	protected SokoSave load()
	{
		this.setFileFilter(sav);
		this.setCurrentDirectory(new File(SokobanUtilities.getPathToCode()+"/code/saves"));
		
		int action = 0;
		try
		{
			action = this.showOpenDialog(this);
		} catch (HeadlessException e) {
			throw new IllegalStateException(e);
		}

		File loadFile = this.getSelectedFile();

		if(loadFile==null)
			return null;

		if(!compareExtension(loadFile, SAV))
		{
			JOptionPane extensionError = new JOptionPane();
			extensionError.showMessageDialog(this, "Invalid file, please select a .sav file", "extension error", JOptionPane.PLAIN_MESSAGE);
			return null;
		}

	    if (action == JFileChooser.APPROVE_OPTION)
	    {
	    	ObjectInputStream loadOIS=null;

	    	try
	    	{
		    	FileInputStream loadFIS = new FileInputStream(loadFile);
		    	loadOIS = new ObjectInputStream(loadFIS);
		    	save = (SokoSave)loadOIS.readObject();
		    	loadOIS.close();
	    	} catch (IOException e) {
	    		throw new RuntimeException(e);
	    	} catch (ClassNotFoundException i) {
	    		throw new RuntimeException(i);
	    	}
		}

		else
			return null;

		return save;
	}


	/**
	* Allows to save one's level progress 
	*
	* @param toSave 	SokoSave, the save being saved
	*/
	protected void save(SokoSave toSave)
	{
		this.setFileFilter(sav);
		this.setCurrentDirectory(new File(SokobanUtilities.getPathToCode()+"/code/saves"));

		int action = 0;
		try
		{
			action = this.showSaveDialog(this);
		} catch (HeadlessException e) {
			throw new IllegalStateException(e);
		}

		File saveFile = this.getSelectedFile();

		if(saveFile==null)
			return;
		
		if(!compareExtension(saveFile, SAV))
			saveFile = new File(adaptExtension(saveFile, SAV));

	    if (action == JFileChooser.APPROVE_OPTION)
	    {
	    	ObjectOutputStream saveOOS=null;

	    	try
	    	{
			    if(!(saveFile.exists()))
			    	saveFile.createNewFile();

		    	FileOutputStream saveFOS = new FileOutputStream(saveFile);
		    	saveOOS = new ObjectOutputStream(saveFOS);
		    	saveOOS.writeObject(toSave);
		    	saveOOS.close();
	    	} catch (IOException e) {
	    		throw new RuntimeException(e);
	    	}
		}

		else
			return;
	}

	/**
	* Compares the extension of a given File to a given extension
	*
	* @param file 		File whose extension is checked
	* @param extension 	String, extension to compare the file's extension to
	* @return  			Boolean: true if the extension are the same, false otherwise
	*/
	private boolean compareExtension(File file, String extension)
	{
		String fileName = file.getAbsolutePath();
		int indexOfDot = fileName.lastIndexOf(".");
		int indexOfSlash = fileName.lastIndexOf("/");

		if (indexOfDot==-1 || indexOfDot<indexOfSlash)
			indexOfDot=fileName.length()-1;

		String fileExtension = fileName.substring(indexOfDot+1, fileName.length());

		return(fileExtension.equals(extension));
	}

	/**
	* Replaces the extension of a given File with a given extension
	* This does not create a new file
	*
	* @param file 		File whose extension is replaced
	* @param extension 	String, extension to replace the file's extension by
	* @return  			String, absolute path to a File with the correct extension (not created by this method)
	*/
	private String adaptExtension(File file, String extension)
	{
		String fileName = file.getAbsolutePath();
		int indexOfDot = fileName.lastIndexOf(".");
		int indexOfSlash = fileName.lastIndexOf("/");


		if (indexOfDot==-1 || indexOfDot<indexOfSlash)
			indexOfDot=fileName.length();

		return fileName.substring(0, indexOfDot)+"."+extension;
	}

	/**
	* Creates a new path for a file to place it in the code/levels/imported directory
	*
	* @param file 	File, the file to move
	* @return 		String, the new path of the file, within the imported directory
	*/
	private String moveToImported(File file)
	{
		String fileName = file.getAbsolutePath();
		int indexOfSlash = fileName.lastIndexOf("/");

		if (indexOfSlash==-1)
			indexOfSlash=0;

		return SokobanUtilities.getPathToCode()+"/code/levels/imported/"+fileName.substring(indexOfSlash+1, fileName.length());
	}
}
