package com.unrc.app;

import java.util.*;
/* This class represents a game board.
 *
 */
public class Grid {
  
	int [] [] board;	//represents a game board.
	int height;			//height of the game board. 
	int width;			//width of the game board.
	private int [] winCells = new int[4]; // each one of the cells that conform a win line

	/**
	 * Constructor.
	 */
	public Grid () {
		height = 6;
		width = 7;
		board = new int [height] [width];
		for (int i = 0; i < height ; i++) {
			for (int j = 0; j < width; j++) {
				board [i] [j] = 0;
			}			
		}
	}

	public void buildFromGame(int game_id){
		Game g = Game.findById(game_id);
		height = g.getInteger("height");
		width = g.getInteger("width");
		List<Play> list = Play.where("game_id = ?", game_id);
        int ord = 1;
        for (Play p : list) {
            int col = p.getInteger("col");
            dropAt(col, actualDisc()).getFirst();
            ord++;
        }
	}

	public void resetGrid(){
		for (int i = 0; i < height ; i++) {
			for (int j = 0; j < width; j++) {
				board [i] [j] = 0;
			}			
		}
	}

	/** 
	 *
	 */
	public int getValue(int i, int j) {
		return board[i][j];
	}

	/**
	 * This method indicates whether the column is full.
	 * @param colum to verify. 
	 * @return true if colum is full, otherwise return false.
	 */
	public boolean fullColumn (int column) {
		return board[0][column] != 0;
	}

	/**
	 * Allows one player to drop one disc on a column.
	 * @param column where I want to drop the disc.
	 * @param player to drop the disc.
	 * @return returns returns a pair with a boolean and an integer.
	 * The boolean is true if the play was successful and the integer indicates
	 * the row where the disc was placed, otherwise returns false and -1.
	 */
	public Pair dropAt (int column, int player) {
		Pair result = new Pair(); 
		if (fullColumn(column)) { // column full fail drop.
			result.setFirst(false);
			result.setSecond(-1);
			return result;
		}
		int row = height - 1;
		while (!(board[row][column] == 0)) { // looking for an empty place.
			row--;
		}
		board[row][column] = player; // successful drop.
		result.setFirst(true);
		result.setSecond(row);
		return result;
	}

	/**
	 * @return a String object representing a grid of connect4.
	 */
	public String toString () {
		String separator = "|";
		String separator1 = " |";
		String separator2 = "|";
		StringBuffer result = new StringBuffer();
		// iterate over the first dimension.
		for (int i = 0; i < height; i++) {
			result.append(separator);
			// iterate over the second dimension.
			for (int j = 0; j < width; j++) {
				if (board[i][j] == 0 ){
					result.append(" ");
					result.append(separator);
				};
				if (board[i][j] > 0 ) {
					result.append("X");
					result.append(separator);
				}
				else {
						if (board[i][j] < 0 ) {
						result.append("0");
						result.append(separator);
					}
				}
			}
			// add a line break.
			result.append("\n");			
		}
		return result.toString();
	}

	/**
	 * Checks whether a position is part of a line of four equal disks.
	 * @param row.
	 * @param column.
	 */
	public boolean checkCell(int row, int column) {
		// an empty place is not controlled.   	
	 	if (board[row][column] == 0) { return false; }
	 	
	 	// row control to the right.
	 	int sum = 0;
	 	for (int k = 0; k < 4 && column < width-3; k++) {
	 			sum += board[row][column+k];	
	 		}
	 	if (Math.abs(sum) == 4) { 
	 		winCells[0] = (1+column+7*row);
	 		winCells[1] = (1+(column+1)+7*row);
	 		winCells[2] = (1+(column+2)+7*row);
	 		winCells[3] = (1+(column+3)+7*row);
	 		return true; 
	 	}

	 	// column control up.
	 	sum = 0;
	 	for (int k = 0; k < 4 && row > 2; k++) {
	 			sum += board[row-k][column];
	 		}
	 	if (Math.abs(sum) == 4) { 
	 		winCells[0] = (1+column+7*row);
	 		winCells[1] = (1+(column)+7*(row-1));
	 		winCells[2] = (1+(column)+7*(row-2));
	 		winCells[3] = (1+(column)+7*(row-3));
	 		return true; 
	 	}

	 	// diagonal control to the right.
	 	sum = 0;
	 	for (int k = 0; k < 4 && row > 2 && column < width-3; k++) {
	 			sum += board[row-k][column+k];
	 		}
	 	if (Math.abs(sum) == 4) { 
	 		winCells[0] = (1+column+7*row);
	 		winCells[1] = (1+(column+1)+7*(row-1));
	 		winCells[2] = (1+(column+2)+7*(row-2));
	 		winCells[3] = (1+(column+3)+7*(row-3));
	 		return true; 
	 	}

	 	// diagonal control to the left.
	 	sum = 0;
	 	for (int k = 0; k < 4 && row > 2 && column > 2; k++) {
	 			sum += board[row-k][column-k];
	 		}
	 	if (Math.abs(sum) == 4) { 
	 		winCells[0] = (1+column+7*row);
	 		winCells[1] = (1+(column-1)+7*(row-1));
	 		winCells[2] = (1+(column-2)+7*(row-2));
	 		winCells[3] = (1+(column-3)+7*(row-3));
	 		return true; 
	 	}

	 	return false;
	}

	/**
	 * 
	 */
	public boolean checkWin() {
		for (int i = height-1; i >= 0; i--) {
			for (int j = 0; j < width; j++) {
				if(checkCell(i, j)) { return true; } 	
			}		
		}
		return false;	 	
	}

	/**
	 *	
	 */
	public int actualDisc() {
		int aux = 0;
		for (int i = 0; i < height ; i++) {
			for (int j = 0; j < width; j++) {
				aux += board [i][j];
			}			
		}
		if (aux == 0) { return 1; }
		else { return -1; }
	}

	public int[] getWinCells (){
		return winCells;
	}
}