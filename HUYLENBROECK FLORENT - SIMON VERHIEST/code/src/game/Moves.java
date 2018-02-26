package game;

/**
* Enum that contains every possible ways a player can move on the board
*
* @author 	Huylenbroeck Florent - Verhiest Simon
*/
public enum Moves
{
	UP('u','U'),
	RIGHT('r','R'),
	DOWN('d','D'),
	LEFT('l','L');

	private final char[] toMov;

	/**
	* Constructor
	*
	* @param move 		char corresponding to the Moves when only the player moves
	* @param moveCrate 	char corresponding to the Moves when the player moves a crate along with it
	*/
	Moves(char move, char moveCrate)
	{
		toMov=new char[2];
		toMov[0]=move;
		toMov[1]=moveCrate;
	}

	public char[] getToMov()
	{
		return toMov;
	}

	/**
	* Gives the Moves corresponding to a given char
	*
	* @param c 	char, to be converted to a Moves
	* @return 	Moves corresponding to the char
	*/
	public static Moves charToMove(char c)
	{
		Moves retValue=UP;

		switch(c)
		{
			case 'u' 	:;
			case 'U' 	: retValue=UP;
						break;
			case 'r' 	:;
			case 'R' 	: retValue=RIGHT;
						break;
			case 'd' 	:;
			case 'D' 	: retValue=DOWN;
						break;
			case 'l' 	:;
			case 'L' 	: retValue=LEFT;
						break;
		}

		return retValue;
	}
}