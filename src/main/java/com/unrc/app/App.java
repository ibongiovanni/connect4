package com.unrc.app;

import com.unrc.app.User;
import org.javalite.activejdbc.Base;
import static spark.Spark.*;
import spark.ModelAndView;
import java.util.*;
/**
 * 
 *
 */
public class App
{
    public static void main( String[] args )
    {
        staticFileLocation("/public");

        /**
         * Before-filters are evaluated before each request.
         * Class Base will open a new connection and attach it to the current thread.
         */
        before((request, response) -> {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/connect4_development", "root", "root");    
        });

        /**
         * After-filters are evaluated after each request.
         * Connection is closed and cleared from thread.
         */
        after((request, response) -> {
            Base.close();    
        });

        // Site style.
        get("/siteStyle", (req, res) -> {
            return new ModelAndView(null, "siteStyle.css");
        }, new MustacheTemplateEngine());  

        get("/", (req,res) -> {
            return new ModelAndView(null, "home.mustache");
        }, new MustacheTemplateEngine()); 
       
        get("/users", (req, res) -> {       

            Map<String, Object> attributes = new HashMap();

            List<Rank> ranks = Rank.findBySQL("SELECT ranks.* FROM ranks JOIN users ON ranks.user_id = users.id ORDER BY number DESC");
            List<User> users = User.findBySQL("SELECT users.* FROM ranks JOIN users ON ranks.user_id = users.id ORDER BY number DESC");
            int size = ranks.size();// This way, you will pre-populate your list.
            int size2= users.size();

            attributes.put("ranks", ranks);
            attributes.put("users", users);

            return new ModelAndView(attributes, "ranking.mustache");          
         
        }, new MustacheTemplateEngine());        

        get("/user_registration", (req, res) -> {
            return new ModelAndView(null, "user_registration.mustache");
        }, new MustacheTemplateEngine()); 

        post("/create_user", (req, res) -> {
            String [] user_data = new String [3];
            user_data[0] = req.queryParams("first_name");
            user_data[1] = req.queryParams("last_name");
            user_data[2] = req.queryParams("email");
            String message="That email is already used :(";
            String color = "red";
            User u = User.findFirst("email=?",user_data[2]);

            if(u==null){
                u = new User();
                u.set("first_name",user_data[0],"last_name",user_data[1],"email",user_data[2]);
                u.saveIt();
                u.newRank();
                message="User Created! :)";
                color="#06FF00";
            }
           
            Map map = new HashMap();
            map.put("message",message);
            map.put("color",color);
            return new ModelAndView(map, "home.mustache");
        }, new MustacheTemplateEngine());

        get("/play", (req,res) -> {

            Map<String, Object> attributes = new HashMap();

            List<User> users = User.findAll();
            int size = users.size();// This way, you will pre-populate your list.

            List<HashMap> order = new LinkedList<HashMap>();
            HashMap mp;
            for (User u : users ) {
                mp = new HashMap();
                mp.put("id",u.getString("id"));
                mp.put("itemName",u.getString("email"));
                order.add(mp);
            }

            Map<String, Object> map = new HashMap();
            map.put("order", order);

            return new ModelAndView(map, "play.mustache");
        }, new MustacheTemplateEngine());

        post("/create_game", (req, res) -> {
            int id_player_1 = Integer.parseInt(req.queryParams("player_1"));
            int id_player_2 = Integer.parseInt(req.queryParams("player_2"));

            Game g = Game.findFirst("(player_1=? and player_2=? and winner is null)or(winner is null and player_1=? and player_2=?)",id_player_1,id_player_2,id_player_2,id_player_1);

            if(g==null){
                g = new Game(id_player_1,id_player_2,6,7);
                g.saveIt();
            }

            int game_id = g.getInteger("id");
            int height = g.getInteger("height");
            int width = g.getInteger("width");
            
            User u = User.findById(id_player_1);
            String name_player1 = u.getString("first_name");
            
            String message = name_player1 + " plays";
            String color = "yellow";
            String sound = "music/ready.mp3";

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);
            map.put("colored",color);
            map.put("sound", sound);

            Grid grid = new Grid();

            List<Play> list = Play.where("game_id = ?", game_id);
            int ord = 1;
            for (Play p : list) {
                int col = p.getInteger("col");
                grid.dropAt(col, grid.actualDisc()).getFirst();
                ord++;
            }
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

        post("/drop", (req, res) -> {

            int game_id = Integer.parseInt(req.queryParams("game_id"));
            int column = Integer.parseInt(req.queryParams("column"));
            String message = "";
            String color = "";
            boolean finished = false;
            String sound = "";

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
                        if (ord % 2 != 0) { message = name_player2 + " plays"; color = "red"; sound= "music/point.mp3"; }
                        else { message = name_player1 + " plays"; color = "yellow"; sound= "music/point.mp3"; }
                    }
                    else {
                        if (ord % 2 != 0) { 
                            message = name_player1 + " won the game!";
                            color = "yellow";
                            finished = true;
                            sound = "music/winmario.mp3";
                            g.set("winner", id_player_1);
                            g.saveIt();
                            u.updateRank(3);
                        }
                        else {
                            message = name_player2 + " won the game!";
                            color = "red";
                            finished = true;
                            sound = "music/winmario.mp3";
                            g.set("winner", id_player_2);
                            g.saveIt();
                            v.updateRank(3);
                        }
                    }                    
                }
                else {
                    message = "The column is full, choose another!";
                    color = "maroon";
                    sound = "music/error.mp3";
                }
            }
            else {
                if (ord > maxPlays) { 
                message = "The game was a tie !!!";
                color = "#36FF36";
                finished = true;
                sound = "music/error.mp3";
                g.set("winner", 0);
                g.saveIt();
                u.updateRank(1);
                v.updateRank(1); 
                }
                else { message = "The game is over"; color = "#36FF36"; finished=true; }
                }

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);
            map.put("colored", color);
            map.put("finished", finished);
            map.put("sound", sound);
            map.put("p1id", id_player_1);
            map.put("p2id", id_player_2);

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

        get("/head2head", (req,res) -> {

            Map<String, Object> attributes = new HashMap();

            List<User> users = User.findAll();
            int size = users.size();// This way, you will pre-populate your list.

            List<HashMap> order = new LinkedList<HashMap>();
            HashMap mp;
            for (User u : users ) {
                mp = new HashMap();
                mp.put("id",u.getString("id"));
                mp.put("itemName",u.getString("email"));
                order.add(mp);
            }

            Map<String, Object> map = new HashMap();
            map.put("order", order);

            return new ModelAndView(map, "head2head.mustache");
        }, new MustacheTemplateEngine());

        post("/showrecords", (req, res) -> {
            int id_player_1 = Integer.parseInt(req.queryParams("player_1"));
            int id_player_2 = Integer.parseInt(req.queryParams("player_2"));

            List<Game> games = Game.where("(player_1=? and player_2=? and winner is not null)or(winner is not null and player_1=? and player_2=?)",id_player_1,id_player_2,id_player_2,id_player_1);
            games.size();

            int win1=0; //Contador de victorias player_1
            int win2=0; //Contador de victorias player_2
            int deu=0; //Contador de empates

            for (Game g : games ) {
                int r = g.getInteger("winner");
                if (r==0) deu++;
                else{
                    if(r==id_player_1) win1++;
                    else win2++;
                }
            }

            User u = User.findById(id_player_1);
            String name_player1 = u.getString("first_name");

            User v = User.findById(id_player_2);
            String name_player2 = v.getString("first_name");

            Map map = new HashMap();
            map.put("win1",win1);
            map.put("win2",win2);
            map.put("deu",deu);
            map.put("name_player1",name_player1);
            map.put("name_player2",name_player2);

            return new ModelAndView(map, "showrecords.mustache");
            
        }, new MustacheTemplateEngine());

    }
}
