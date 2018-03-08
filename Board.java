import java.io.*;

public class Board{

	public static void main(String[] args){
		String niveau = args[0];	
		Board jeu = new Board(niveau);	
		

	}
	//Constructor
	public Board(String level){
		try{
			loadLevel(level);
		} catch(IOException e){
			System.out.println("No file found." +e.getMessage());
		}
	}
	// this will read the specified file
	public static void loadLevel(String level) throws IOException{
		try{
			File file = new File(level);
			String lvlPath = file.getAbsolutePath();

			FileReader fr = new FileReader(lvlPath+".txt");
			BufferedReader br = new BufferedReader(fr);
			String line;
			line = br.readLine();
			String columnLength = line.substring(0,2);
			String rowLength = line.substring(3,5);
			
			int column = Integer.parseInt(columnLength) + 1;
			int row = Integer.parseInt(rowLength) + 1;
			String [][]board = new String[column][row];

			while((line= br.readLine()) != null){
				String pos =line.substring(line.indexOf(" "));
				String temp_i = pos.substring(pos.indexOf(" "), pos.lastIndexOf(" "));
				String temp_j = pos.substring(pos.lastIndexOf(" "));
				temp_i = temp_i.replaceAll("\\s","");
				temp_j = temp_j.replaceAll("\\s","");
				int i = Integer.parseInt(temp_i);  // col
				int j = Integer.parseInt(temp_j); // row
				board[i][j] = line.substring(0,line.indexOf(" "));
				
			}

			fr.close();

			int a= 0, b=0;
		for(String sousTab[] : board){
			a =0;
			for(String str : sousTab)
				{    
				    System.out.println("La valeur du tableau à l'indice ["+a+"]["+b+"] est : " + board[a][b]);
				    a++;
				 }
			b++;
		
		}
		}
		catch(IOException e){
			System.out.println("No file found." +e.getMessage());
		}
	}

	public String getElem(int i, int j){
		Board board = new Board(level_1);
		return board[i][j];
	}
	
}
