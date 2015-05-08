package com.unrc.app;
/* This class represents one space in a grid.
 *
 */
public class Cell {
	private int state; // 1 for player1, 2 for player2 and 0 is empty.
	private int x;
	private int y;

	/** 
	 * Constructor.
	 */
	public Cell (int grid_id, int x, int y) {
	   = grid_id;
		state = 0;
		this.x = x;
		this.y = y;
	}	

 	/**
	 * @return true if space is empty, otherwise return false.
 	 */ 
 	public boolean isEmpty () {
 		return state == 0;
 	}
}
