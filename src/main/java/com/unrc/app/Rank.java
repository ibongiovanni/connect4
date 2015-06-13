package com.unrc.app;

import org.javalite.activejdbc.Model;

public class Rank extends Model {

	
  	public String points() {
    	return this.getString("number"); 
    }

}
