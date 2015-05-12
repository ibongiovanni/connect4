package com.unrc.app;

import org.javalite.activejdbc.Model;

/* This class represents one space in a grid.
 */
public class Cell extends Model {

	/**
	 * @return true if space is empty, otherwise return false.
	 */ 
	public boolean isEmpty () {
		return this.getInteger("state") == 0;
 	}
}