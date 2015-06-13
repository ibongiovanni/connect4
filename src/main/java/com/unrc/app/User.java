package com.unrc.app;

import org.javalite.activejdbc.Model;

public class User extends Model {
  static {
    validatePresenceOf("first_name");
  }
  
  /*
  * It creates a new rank for a player gived - Just use it when you create new players -
  * @param player,  information player onez
  */
	public void newRank(){
		int i = this.getInteger("id");
		Rank r = new Rank();
		r.set("number",0,"user_id",i);
		r.saveIt();
	}

  /*
  * It allows update the score of the User increasing their score (adds "points" more)
  * @param points, value to add to actual score
  */
  public void updateRank(int points){
    int i = this.getInteger("id");
    Rank r = Rank.findFirst("user_id = ?", i);
    int score = r.getInteger("number");
    r.set("number", (score + points));
    r.saveIt();
  }
  
  public String firstName() {
    return this.getString("first_name"); 
  }

  public String lastName() {
    return this.getString("last_name"); 
  }

  public String idUser() {
    return this.getString("id"); 
  
  }

}
