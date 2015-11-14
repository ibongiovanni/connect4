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

  public boolean is_correct_password(String pass){
    return this.getString("password").equals(pass);
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

  public String userName() {
    return this.getString("username"); 
  }

  public int wins(){
    return Math.toIntExact(Game.count("(winner = ?) and (player_1!=1 and player_2!=1)",this.getInteger("id")));
  }

  public int matches(){
    int id = this.getInteger("id");
    return Math.toIntExact(Game.count("(player_1=? or player_2=?) and (player_1!=1 and player_2!=1)", id, id));
  }

  public int draws(){
    int id = this.getInteger("id");
    return Math.toIntExact(Game.count("winner=0 and (player_1=? or player_2=?) and (player_1!=1 and player_2!=1)", id, id));
  }

  public int losses(){
    return (matches() - wins() - draws());
  }
}
