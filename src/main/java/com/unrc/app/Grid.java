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
		return board [0] [colum] != 0;
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
	public String toString () {
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				System.out.print(board[i][j] + " ");
			}
			System.out.println();			
		}
	}

}