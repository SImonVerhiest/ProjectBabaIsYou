package game;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
* Serializable implementation that allows the save of a Sokoban level state
*
* @author 	HUYLENBROECK Florent
*/
public class SokoSave implements Serializable
{
	private String movPath;
	private String xsbPath;

	/**
	* Constructor
	*
	* @param xsbPath 	String, absolute path to the .xsb file showing the level
	* @param movPath 	String, absolute path to the .mov file showing the moves played on the level
	*/
	public SokoSave(String xsbPath, String movPath)
	{
		this.xsbPath=xsbPath;
		this.movPath=movPath;
	}

	public void readObject(ObjectInputStream ois)
	throws IOException, ClassNotFoundException 
	{
		this.xsbPath = (String)ois.readObject();
		this.movPath = (String)ois.readObject();
	}

	public void writeObject(ObjectOutputStream oos)
	throws IOException 
	{
		oos.writeObject(xsbPath);
		oos.writeObject(movPath);
	}

	public String getMovPath()
	{
		return movPath;
	}

	public String getXsbPath()
	{
		return xsbPath;
	}
}