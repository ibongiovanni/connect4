package com.unrc.app;

import org.javalite.activejdbc.Model;
/* This class represents a game board.
 *
 */
public class Grid extends Model {
  
	 //Cell [] [] board;	//represents a game board.
	
	/**
	 * This method indicates whether the column is full.
	 * @param colum to verify. 
	 * @return true if colum is full, otherwise return false.
	 */
	public boolean fullColum (int colum) {
		int id = this.getInteger("game_id");
		Cell c = Cell.findFirst("game_id = ? and x_coord = ? and y_coord = ?",id,colum,0);
		return c.getInteger("state") != 0;		
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
		int i = this.getInteger("height") - 1;
		int id = this.getInteger("game_id");
		while (!Cell.findFirst("game_id = ? and x_coord = ? and y_coord = ?",id,i,colum).isEmpty()) {
			i--;
		}
		(Cell.findFirst("game_id = ? and x_coord = ? and y_coord = ?",id,i,colum)).set("state", player).saveIt();
		return true;
	}
	// /**
	//  * @return a String object representing a grid of connect4.
	//  */
	// public String toString () {
	// 	String separator = "|";
	// 	StringBuffer result = new StringBuffer();
	// 	// iterate over the first dimension.
	// 	for (int i = 0; i < height; i++) {
	// 		result.append(separator);
	// 		// iterate over the second dimension.
	// 		for (int j = 0; j < width; j++) {
	// 			result.append(board[i][j].getCell());
	// 			result.append(separator);
	// 		}
	// 		// add a line break.
	// 		result.append("\n");			
	// 	}
	// 	return result.toString();
	// }	
}