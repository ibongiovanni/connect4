package com.unrc.app;
/* This class represents a game board.
 *
 */
public class Grid {
  
	Cell [] [] board;	//represents a game board.
	int height;			//height of the game board. 
	int width;			//width of the game board.

	/**
	 * Constructor.
	 */
	public Grid () {
		height = 6;
		width = 7;
		board = new Cell [height] [width];
		for (int i = 0; i < height ; i++) {
			for (int j = 0; j < width; j++) {
				board [i] [j] = new Cell();
			}			
		}
	}
	/**
	 * This method indicates whether the column is full.
	 * @param colum to verify. 
	 * @return true if colum is full, otherwise return false.
	 */
	public boolean fullColum (int colum) {
		return board[0][colum].getCell() != 0;
	}
	/**
	 * Allows one player to drop one disc on a column.
	 * @param colum where I want to drop the disc.
	 * @param player to drop the disc.
	 * @return returns true if the play was successful, otherwise returns false.
	 */
	public boolean dropAt (int colum, int player) {
		if (fullColum(colum)) {
			return false;
		}
		int i = height - 1;
		while (!board[i][colum].isEmpty()) {
			i--;
		}
		board[i][colum].setCell(player);
		return true;
	}
	/**
	 * @return a String object representing a grid of connect4.
	 */
	public String toString () {
		String separator = "|";
		StringBuffer result = new StringBuffer();
		// iterate over the first dimension.
		for (int i = 0; i < height; i++) {
			result.append(separator);
			// iterate over the second dimension.
			for (int j = 0; j < width; j++) {
				result.append(board[i][j].getCell());
				result.append(separator);
			}
			// add a line break.
			result.append("\n");			
		}
		return result.toString();
	}	

	public boolean check(int i, int j){
		int sum=0;
		//k controla desde la posicion siguiente tres lugares mas- y si (j=width-3) ya no controla
		//controla columna
		for (int k= j; k<j+4  && j<width-3 ; k++) {
			 sum+= board[i][k].getCell();
	
		}
		if(Math.abs(sum)==4) return true;

		//controla fila
		sum=0;
		for (int k= i; k<i+4  && i<height-3 ; k++) {
			 sum+= board[k][j].getCell();
			
		}
		if(Math.abs(sum)==4) return true;

		//controla diagonal derecha
		sum=0;
		for (int k= 0; k<4  && i<height-3  && j<width-3 ; k++) {
			 sum+= board[i+k][j+k].getCell();
		}
		if(Math.abs(sum)==4) return true; 

		//controla diagonal Izquierda
		sum=0;
		for (int k= 0; k<4  && i>2  && j>2; k++) {
			 sum+= board[i+k][j-k].getCell();
		}
		if(Math.abs(sum)==4) return true; 

	return false;	

	}


	
	public boolean checkWin(){
		for(int i=height-1; i>=0; i--)
			for (int j=0; j<width; j++ ) {
				if(chek(i,j)) return true;
				
			}

	}
}