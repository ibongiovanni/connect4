package com.unrc.app;

import org.javalite.activejdbc.Model;
/* This class represents a game board.
 *
 */
public class Grid extends Model {
  
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
	// 			result.append(board[i][j].get("state"));
	// 			result.append(separator);
	// 		}
	// 		// add a line break.
	// 		result.append("\n");			
	// 	}
	// 	return result.toString();
	// }	

	// public boolean check(int i, int j, int player){
	// 	int sum=0;
	// 	k controla desde la posicion siguiente tres lugares mas- y si (j=width-3) ya no controla
	// 	//control row
	// 	for (int k= j; k<j+4  && j<width-3 ; k++) {
	// 		 sum+= board[i][k].get("state");
	
	// 	}
	// 	if(Math.abs(sum)==4) return true;

	// 	//Control columnDown
	// 	sum=0;
	// 	for (int k= i; k<i+4  && i<height-3 ; k++) {
	// 		 sum+= board[k][j].get("state");
	// 	}
	// 	if(Math.abs(sum)==4) return true;



	// 	//control diagonal rightUP
	// 	sum=player;
	// 	for (int k= 1; k<3  && (i<height-3)  && (j<width-3) && (board[i+k][j+k].get("state")==player); k++) {
	// 		 sum+= board[i+k][j+k].get("state");
	// 	}
	// 	if(Math.abs(sum)==4) return true; 

	// 	//control diagonal leftUp
	// 	sum=0;
	// 	for (int k= 0; k<4  && i>2  && j>2; k++) {
	// 		 sum+= board[i+k][j-k].get("state");
	// 	}
	// 	if(Math.abs(sum)==4) return true; 

	//   return false;	
	// 	}
	// }
}