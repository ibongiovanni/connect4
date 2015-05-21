package com.unrc.app;

import java.util.Scanner;
import org.javalite.activejdbc.Model;

public class Game extends Model {

	/**
	 * Constructor
	 */
	public Game() {

	}
	/**
	 * Constructor
	 * @param p1 is player_1.
	 * @param p2 is player_2.
	 * @param h is the board height.
	 * @param w is the board width.
	 */
	public Game(int p1, int p2, int h, int w) {
		set("player_1", p1);
		set("player_2", p2);
		set("height", h);
		set("width", w);
		set("winner", null);
	}
	/**
	 * 
	 */
	public void playOffLine(int player1, int player2) {
		int id = getInteger("id");// game id.
		Scanner in = new Scanner(System.in);
		int maxPlays = getInteger("height") * getInteger("width");// maximum number of plays.
		Grid board = new Grid();
		int ord = 0;// order of play.
		int column;
		int disc = 1;// game piece. 
		while (!board.checkWin() && (ord <= maxPlays)) {
			ord++;
			System.out.println("Turn Player " + ord % 2);
			System.out.println(board.toString());
			System.out.println("Enter the column where you want to play: ");
			column = in.nextInt();
			Pair drop = board.dropAt(column, disc);
			while(!drop.getFirst()){
				System.out.println("ERROR: The column is full choose another:");
				column = in.nextInt();
				drop = board.dropAt(column, disc);
			}
			disc *= -1;// piece exchange.
			Play p = new Play();// save play in DB.
			p.set("game_id", id, "ord", ord, "col", column, "row", drop.getSecond()); 
			p.saveIt();			
		}
		if (board.checkWin()) {
			System.out.println("Won player: " + ord % 2);
		}
		else {
			System.out.println("The game was a tie !!");
		}
		System.out.println(board.toString());
	}  
}