package com.unrc.app;

import com.unrc.app.User;
import org.javalite.activejdbc.Base;
import static spark.Spark.*;
import spark.ModelAndView;
import java.util.*;
/**
 * Hello world!
 *
 */
public class App
{
    /**
     *
     */
    public static void baseOpen(){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/connect4_development", "root", "root");
    }

    public static void main( String[] args )
    {
        get("/", (req,res) -> {
            return new ModelAndView(null, "home.mustache");
        }, new MustacheTemplateEngine()); 
       
        get("/users", (req, res) -> {  // NO FUNCIONA BIEN !!         

            baseOpen();           

            Map<String, Object> attributes = new HashMap();

            List<User> users = User.findAll();

            int size = users.size();// This way, you will pre-populate your list.

            Base.close();

            attributes.put("users", users);

            return new ModelAndView(attributes, "users.mustache");          
         
        }, new MustacheTemplateEngine());        

        get("/user_registration", (req, res) -> {
            return new ModelAndView(null, "user_registration.mustache");
        }, new MustacheTemplateEngine()); 

        post("/create_user", (req, res) -> {
            String [] user_data = new String [3];
            user_data[0] = req.queryParams("first_name");
            user_data[1] = req.queryParams("last_name");
            user_data[2] = req.queryParams("email");
            
            baseOpen();
            
            User u = new User();
            u.set("first_name",user_data[0],"last_name",user_data[1],"email",user_data[2]);
            u.saveIt();
            
            Base.close();

            return "Usuario creado !";
        });

        get("/play", (req,res) -> {
            return new ModelAndView(null, "play.mustache");
        }, new MustacheTemplateEngine());

        post("/create_game", (req, res) -> {
            int id_player_1 = Integer.parseInt(req.queryParams("player_1"));
            int id_player_2 = Integer.parseInt(req.queryParams("player_2"));

            baseOpen();

            Game g = new Game(id_player_1,id_player_2,6,7);
            g.saveIt();

            int game_id = g.getInteger("id");
            int height = g.getInteger("height");
            int width = g.getInteger("width");
            
            User u = User.findById(id_player_1);
            String name_player1 = u.getString("first_name");
            
            Base.close();

            String message = name_player1 + " plays";

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);

            int k = 0;
            for (int i = 0; i < height ; i++) {
                for (int j = 0; j < width; j++) {
                    k++;
                    map.put("celda"+k, " ");        
                }
            }

            return new ModelAndView(map, "game.mustache");
            
        }, new MustacheTemplateEngine());

        post("/drop", (req, res) -> {

            int game_id = Integer.parseInt(req.queryParams("game_id"));
            int column = Integer.parseInt(req.queryParams("column"));
            String message = "";

            baseOpen();

            Game g = Game.findById(game_id);
            int height = g.getInteger("height");
            int width = g.getInteger("width");
            int maxPlays = height * width;    

            int id_player_1 = g.getInteger("player_1");
            int id_player_2 = g.getInteger("player_2");

            User u = User.findById(id_player_1);
            String name_player1 = u.getString("first_name");

            User v = User.findById(id_player_2);
            String name_player2 = v.getString("first_name");

            Grid grid = new Grid();

            List<Play> list = Play.where("game_id = ?", game_id);
            int ord = 1;
            for (Play p : list) {
                int col = p.getInteger("col");
                grid.dropAt(col, grid.actualDisc()).getFirst();
                ord++;
            }

            if (!grid.checkWin() && ord <= maxPlays) {

                Pair drop = grid.dropAt(column, grid.actualDisc());

                if (drop.getFirst()) {
                    Play p = new Play();// save play in DB.
                    p.set("game_id", game_id, "ord", ord, "col", column, "row", drop.getSecond()); 
                    p.saveIt();

                    if (!grid.checkWin()) {
                        if (ord % 2 != 0) { message = name_player2 + " plays"; }
                        else { message = name_player1 + " plays"; }
                    }
                    else {
                        if (ord % 2 != 0) { 
                            message = name_player1 + " won the game!";
                            g.set("winner", id_player_1);
                            g.saveIt();
                        }
                        else {
                            message = name_player2 + " won the game!";
                            g.set("winner", id_player_2);
                            g.saveIt();
                        }
                    }                    
                }
                else {
                    message = "ERROR: The column is full choose another!";
                }
            }
            else {
                if (ord == maxPlays) { message = "The game was a tie !!!"; }
                else { message = "The game is over"; }
            }

            Base.close();

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);

            int k = 1;
            for (int i = 0; i < 6 ; i++) {
                for (int j = 0; j < 7; j++) {
                    if (grid.getValue(i,j) == 1) {
                        map.put("celda"+k, "X");
                    }
                    else {
                        if (grid.getValue(i,j) == -1) {
                            map.put("celda"+k, "O");    
                        }
                        else {
                            map.put("celda"+k, " ");
                        }
                    } 
                    k++;
                }           
            }
            return new ModelAndView(map, "game.mustache");

        }, new MustacheTemplateEngine());
    }
}
