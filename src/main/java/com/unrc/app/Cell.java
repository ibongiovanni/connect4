package com.unrc.app;
/* This class represents one space in a grid.
 *
 */
public class Cell {

	private int state; // 1 for player1, 2 for player2 and 0 is empty.

	/** 
	 * Constructor.
	 */
	public Cell () {
		state = 0;
	}	
	/**
	 * Get and set.
	 */
 	public void setCell (int s) {
 		state = s;
 	}
 	public int getCell () {
 		return state;
 	}
 	/**
	 * @return true if space is empty, otherwise return false.
 	 */ 
 	public boolean isEmpty () {
 		return state == 0;
 	}
}