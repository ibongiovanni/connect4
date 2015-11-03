package com.unrc.app;

import com.unrc.app.User;
import com.unrc.app.ai.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.javalite.activejdbc.Base;
import static spark.Spark.*;
import spark.ModelAndView;
import java.util.*;

import com.google.gson.Gson;

/**
 * 
 *
 */
public class App
{
    //private Gson gson = new Gson();

    public static void main( String[] args )
    {
        Gson gson = new Gson();
        staticFileLocation("/public");

        /**
         * Before-filters are evaluated before each request.
         * Class Base will open a new connection and attach it to the current thread.
         */
        before((request, response) -> {
            Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/connect4_development", "root", "root");
            // System.out.println(request.uri());
            // boolean authenticated = false;
            // String id_session = request.session().attribute("ID_SESSION");
            // if (id_session != null || request.uri().equals("/")) {
            //     authenticated = true;
            // }
            // if (!authenticated) {
            //     response.redirect("/");
            // }   
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
            String id_session = req.session().attribute("ID_SESSION");
            if (id_session != null) {
                return new ModelAndView(null, "home.mustache");
            }
            else {
                return new ModelAndView(null, "init.mustache");
            } 
        }, new MustacheTemplateEngine());

        get("/login",(req,res) -> {
            String id_session = req.session().attribute("ID_SESSION");
            if (id_session != null) {
                return new ModelAndView(null, "home.mustache");
            }
            else {
                return new ModelAndView(null, "login.mustache");
            }
        }, new MustacheTemplateEngine());

        post("/login_user",(req,res) -> {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            String message;
            String color;
            Map attributes = new HashMap();                           
            User user = User.findFirst("username=?",username);
            if (user == null) {
                message="Wrong user !!";
                color = "red";
                attributes.put("message",message);
                attributes.put("color",color);
                return new ModelAndView(attributes, "init.mustache");                
            }
            else {
                if (user.is_correct_password(password)) {
                    String id_session = user.getString("id");
                    req.session().attribute("ID_SESSION",id_session);
                    message="Welcome " + username + "!!";
                    color = "#06FF00"; 
                    attributes.put("message",message);
                    attributes.put("color",color);
                    return new ModelAndView(attributes, "home.mustache");
                }
                else {
                    message="Wrong password !!";
                    color = "red";
                    attributes.put("message",message);
                    attributes.put("color",color);
                    return new ModelAndView(attributes, "init.mustache");
                }                
            }            
        }, new MustacheTemplateEngine()); 

        get("/logout",(req,res) -> {
            String id_session = req.session().attribute("ID_SESSION");
            if (id_session != null) {
                req.session().removeAttribute("ID_SESSION");
            }
            return new ModelAndView(null, "init.mustache");
        }, new MustacheTemplateEngine());

        get("/credits", (req,res) -> {
            return new ModelAndView(null, "credits.mustache");
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
            String [] user_data = new String [5];
            user_data[0] = req.queryParams("first_name");
            user_data[1] = req.queryParams("last_name");
            user_data[2] = req.queryParams("email");
            user_data[3] = req.queryParams("username");
            user_data[4] = req.queryParams("password");
            String message="The username or email are already used :(";
            String color = "red";
            User u = User.findFirst("username=?",user_data[3]);
            User u1 = User.findFirst("email=?",user_data[2]);
            Map map = new HashMap();
            if((u==null)&&(u1==null)){
                
                u = new User();
                u.set("first_name",user_data[0],"last_name",user_data[1],"email",user_data[2],"username",user_data[3],"password",user_data[4]);
                u.saveIt();
                u.newRank();
                message="User Created! :)";
                color="#06FF00";    
            }
            else{
                map.put("message",message);
                return new ModelAndView(map, "user_registration.mustache");
            }

            map.put("message",message);
            map.put("color",color);
            return new ModelAndView(map, "home.mustache");
        }, new MustacheTemplateEngine());


        get("/play", (req,res) -> {

            Map<String, Object> attributes = new HashMap();

            List<User> users = User.where("username != 'CPU'");
            int size = users.size();// This way, you will pre-populate your list.

            List<HashMap> order = new LinkedList<HashMap>();
            HashMap mp;
            for (User u : users ) {
                mp = new HashMap();
                mp.put("id",u.getString("id"));
                mp.put("itemName",u.getString("username"));
                order.add(mp);
            }

            Map<String, Object> map = new HashMap();
            map.put("order", order);

            return new ModelAndView(map, "play.mustache");
        }, new MustacheTemplateEngine());


        post("/game/create", (req, res) -> {
            int id_player_1 = Integer.parseInt(req.queryParams("player_1"));
            int id_player_2 = Integer.parseInt(req.queryParams("player_2"));

            Game g = Game.findFirst("(player_1=? and player_2=? and winner is null)or(winner is null and player_1=? and player_2=?)",id_player_1,id_player_2,id_player_2,id_player_1);

            if(g==null){
                g = new Game(id_player_1,id_player_2,6,7);
                g.saveIt();
            }

            int game_id = g.getInteger("id");

            res.redirect("/game/"+game_id);
            return false;
        });

        get("/game/:id", (req,res) -> {
            System.out.println(req.params(":id"));
            int game_id = Integer.parseInt(req.params(":id"));
            Game g = Game.findById(game_id);


            int id_player_1 = g.getInteger("player_1");
            int id_player_2 = g.getInteger("player_2");
            int height = g.getInteger("height");
            int width = g.getInteger("width");
            
            User u = User.findById(id_player_1);
            String name_player1 = u.getString("username");
            
            String message = name_player1 + " plays";
            String color = "yellow";
            String sound = "music/ready.mp3";

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);
            map.put("colored",color);
            map.put("sound", sound);
            map.put("p1id", id_player_1);
            map.put("p2id", id_player_2);

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

            String method = "post";
            String action = "/create_game";
            int valuep1 = id_player_2;
            int valuep2 = id_player_1;

            map.put("method",method);
            map.put("action",action);
            map.put("valuep1",valuep1);
            map.put("valuep2",valuep2);

            return new ModelAndView(map, "game.mustache");
            
        }, new MustacheTemplateEngine());

        post("/drop2", (req, res) -> {

            int game_id = Integer.parseInt(req.queryParams("game_id"));
            int column = Integer.parseInt(req.queryParams("column"));
            Map map = new HashMap();
            String message = "";
            String color = "";
            boolean finished = false;
            String sound = "";
            int cellNumber = 0;
            char coinValue = ' ';
            boolean successful = false;

            Game g = Game.findById(game_id);
            int height = g.getInteger("height");
            int width = g.getInteger("width");
            int maxPlays = height * width;    

            int id_player_1 = g.getInteger("player_1");
            int id_player_2 = g.getInteger("player_2");

            User u = User.findById(id_player_1);
            String name_player1 = u.getString("username");

            User v = User.findById(id_player_2);
            String name_player2 = v.getString("username");

            Grid grid = new Grid();

            //Rebuild the game board
            List<Play> list = Play.where("game_id = ?", game_id);
            int ord = 1;
            for (Play p : list) {
                int col = p.getInteger("col");
                grid.dropAt(col, grid.actualDisc()).getFirst();
                ord++;
            }


            if (!grid.checkWin() && ord <= maxPlays) {
                //Make the move
                Pair drop = grid.dropAt(column, grid.actualDisc());
                //drop.First == Was the play successful?
                //drop.Second == In that case, in wich row stay the last move?

                successful = drop.getFirst();
                //If drop.First is True then the play was successful
                if (drop.getFirst()) {
                    Play p = new Play();// save play in DB.
                    p.set("game_id", game_id, "ord", ord, "col", column, "row", drop.getSecond()); 
                    p.saveIt();
                    cellNumber = (1+column+7*drop.getSecond());
                    if (!grid.checkWin()) {
                        if (ord==maxPlays) {
                            coinValue= 'O'; 
                            message = "The game was a tie !!!";
                            color = "#36FF36";
                            finished = true;
                            sound = "music/error.mp3";
                            g.set("winner", 0);
                            g.saveIt();
                            u.updateRank(15);
                            v.updateRank(15); 
                        }
                        else{
                            if (ord % 2 != 0) { 
                                message = name_player2 + " plays"; 
                                color = "red"; 
                                sound= "music/point.mp3"; 
                                coinValue= 'X';
                            }
                            else { 
                                message = name_player1 + " plays"; 
                                color = "yellow"; 
                                sound= "music/point.mp3";
                                coinValue= 'O'; 
                            }
                        }
                    }
                    else {
                        if (ord % 2 != 0) { 
                            message = name_player1 + " won the game!";
                            coinValue = 'X';
                            color = "yellow";
                            finished = true;
                            sound = "music/user_win.mp3";
                            map.put("winner", "1");
                            map.put("winCells",grid.getWinCells());
                            g.set("winner", id_player_1);
                            g.saveIt();
                            u.updateRank((Integer)5000/ord);
                            v.updateRank(ord);
                        }
                        else {
                            message = name_player2 + " won the game!";
                            coinValue = 'O';
                            color = "red";
                            finished = true;
                            map.put("winner", "2");
                            map.put("winCells",grid.getWinCells());
                            sound = "music/user_win.mp3";
                            g.set("winner", id_player_2);
                            g.saveIt();
                            v.updateRank((Integer)5000/ord);
                            u.updateRank(ord);
                        }
                    }                    
                }
                else {
                    message = "The column is full, choose another!";
                    color = (ord%2!=0)? "yellow":"red";
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
                    u.updateRank(15);
                    v.updateRank(15); 
                }
                else { message = "The game is over"; color = "#36FF36"; finished=true; }
                }

            
            map.put("game_id", game_id);
            map.put("cell", cellNumber);
            map.put("coin", coinValue);
            map.put("message", message);
            map.put("colored", color);
            map.put("finished", finished);
            map.put("successful",successful);
            map.put("sound", sound);
            map.put("p1id", id_player_1);
            map.put("p2id", id_player_2);

            return map;
        }, gson::toJson);

        /**********************************************************************
        *   AI methods
        *   
        *
        **/
        Board b = new Board();
        Connect4AI ai = new Connect4AI(b); 
        Grid grid = new Grid();

        get("/playai",(req,res) -> {
            String id_session = req.session().attribute("ID_SESSION");
            if (id_session == null) {
                Map attributes = new HashMap();
                attributes.put("color","red");
                attributes.put("message","you must be logged in");
                return new ModelAndView(attributes, "init.mustache");
            }
            int id_player_1 = Integer.parseInt(id_session);// Integer.parseInt(req.params(":id"));
            int id_player_2 = 3;

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
            String sound = "music/AI/ready.mp3";

            Map map = new HashMap();
            map.put("game_id", game_id);
            map.put("message", message);
            map.put("colored",color);
            map.put("sound", sound);
            map.put("p1id", id_player_1);
            map.put("p2id", id_player_2);

            grid.resetGrid();
            b.resetBoard();

            List<Play> list = Play.where("game_id = ?", game_id);
            int ord = 1;
            for (Play p : list) {
                int col = p.getInteger("col");
                grid.dropAt(col, grid.actualDisc()).getFirst();
                b.placeMove(col,(ord%2==0)?1:2);
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
            b.displayBoard();

            String method = "get";
            String action = "/playai";
            int valuep1 = id_player_1;
            int valuep2 = id_player_2;

            map.put("method",method);
            map.put("action",action);
            map.put("valuep1",valuep1);
            map.put("valuep2",valuep2);

            return new ModelAndView(map, "gameai.mustache");
        }, new MustacheTemplateEngine());

        post("/dropai", (req, res) -> {

            int game_id = Integer.parseInt(req.queryParams("game_id"));
            int column = Integer.parseInt(req.queryParams("column"));
            Map map = new HashMap();
            String message = "";
            String color = "";
            boolean finished = false;
            String sound = "";
            int cellNumber = 0;
            int cellAi = 0;
            char coinValue = ' ';
            boolean successful = false;
            int moveAi = 0;

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

            

            //Rebuild the game board
            List<Play> list = Play.where("game_id = ?", game_id);
            int ord = list.size();
            // for (Play p : list) {
            //     int col = p.getInteger("col");
            //     grid.dropAt(col, grid.actualDisc()).getFirst();
            //     ord++;
            // }
           
            if (!grid.checkWin() && ord <= maxPlays) {
                //Make the move
                Pair drop = grid.dropAt(column, grid.actualDisc());
                //drop.First == Was the play successful?
                //drop.Second == In that case, in wich row stay the last move?

                successful = drop.getFirst();
                //If drop.First is True then the play was successful
                if (drop.getFirst()) {
                    ord++;
                    b.placeMove(column,2);
                    Play p = new Play();// save play in DB.
                    p.set("game_id", game_id, "ord", ord, "col", column, "row", drop.getSecond()); 
                    p.saveIt();
                    cellNumber = (1+column+7*drop.getSecond());
                    if (!grid.checkWin()) {
                        message = name_player1 + " plays"; 
                        color = "yellow"; 
                        sound= "music/point.mp3";
                        coinValue= 'X';
                    }
                    if (ord==maxPlays) {
                            coinValue= 'O'; 
                            message = "The game was a tie !!!";
                            color = "#36FF36";
                            finished = true;
                            sound = "music/error.mp3";
                            g.set("winner", 0);
                            g.saveIt();
                    }  
                    if (grid.checkWin()){
                        if (ord % 2 != 0) { 
                            message = name_player1 + " won the game!";
                            coinValue = 'X';
                            color = "yellow";
                            finished = true;
                            map.put("winner", "1");
                            map.put("winCells",grid.getWinCells());
                            sound = "music/user_win.mp3";
                            g.set("winner", id_player_1);
                            g.saveIt();
                        }
                        else {
                            message = name_player2 + " won the game!";
                            coinValue = 'X';
                            color = "red";
                            finished = true;
                            sound = "music/user_win.mp3.mp3";
                            g.set("winner", id_player_2);
                            g.saveIt();
                        }
                    }                    
                }
                else {
                    message = "The column is full, choose another!";
                    color = "yellow";
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
                }
                else { message = "The game is over"; color = "#36FF36"; finished=true; }
            }

            
            map.put("game_id", game_id);
            map.put("cell", cellNumber);
            map.put("coin", coinValue);
            map.put("message", message);
            map.put("colored", color);
            map.put("finished", finished);
            map.put("successful",successful);
            map.put("sound", sound);
            map.put("p1id", id_player_1);
            map.put("p2id", id_player_2);
            map.put("ord",ord);

            return map;
        }, gson::toJson);

        post("/moveai", (req,res) -> {
            int ord = Integer.parseInt(req.queryParams("ord"));
            int game_id = Integer.parseInt(req.queryParams("game_id"));
            Map map = new HashMap();
            boolean finished =false;
            String message = "";
            String color = "";

            int moveAi = ai.getAIMove(ThreadLocalRandom.current().nextInt(3, 9));    //get the AI move (with a random MaxDepth)
            try{ // Wait some time
                TimeUnit.MILLISECONDS.sleep(400);
            } catch(InterruptedException ex) { Thread.currentThread().interrupt(); }
            if (moveAi==-1) { //sometimes getAIMove throws -1
                do{ //so in that case I get a random value ¯\_(ツ)_/¯
                    moveAi = ThreadLocalRandom.current().nextInt(0, 6 + 1);
                } 
                while(grid.fullColumn(moveAi));
            }
            System.out.println("moveAI= "+moveAi);
            b.placeMove(moveAi,1);      //make move on AI-board
            Pair drop = grid.dropAt(moveAi,-1);     //make move in our board
            b.displayBoard();
            System.out.println("Game Result= "+ai.gameResult(b));
            System.out.println(grid.toString());
            ord++;
            int cellAi = (1+moveAi+7*drop.getSecond());
            Play p = new Play();// save play in DB.
            p.set("game_id", game_id, "ord", ord, "col", moveAi, "row", drop.getSecond());
            p.saveIt();
            String sound= randomSound();
            if (grid.checkWin()) {
                message = "CPU won the game!";
                color = "red";
                finished = true;
                sound = "music/AI/win.mp3";
                map.put("winner", "2");
                map.put("winCells",grid.getWinCells());
                Game g = Game.findById(game_id);
                g.set("winner", 3);
                g.saveIt();
            }
            if (ord==42) {
                message = "The game was a tie !!!";
                color = "#36FF36";
                finished = true;
                sound = "music/error.mp3";
                map.put("sound",sound);
                Game g = Game.findById(game_id);
                User v = User.findById(3);
                g.set("winner", 0);
                g.saveIt();
            }
            map.put("sound",sound);
            map.put("message",message);
            map.put("colored",color);
            map.put("finished",finished);
            map.put("cellAi",cellAi);
            return map;
        }, gson::toJson);
   
        

        /* End of AI methods
        **********************************************************************/

        get("/head2head", (req,res) -> {

            Map<String, Object> attributes = new HashMap();
            String id_session = req.session().attribute("ID_SESSION");
            if (id_session == null) {
                attributes.put("color","red");
                attributes.put("message","you must be logged in");
                return new ModelAndView(attributes, "init.mustache");
            }
            int id_player = Integer.parseInt(id_session);

            List<User> users = User.where("id != ?",id_player);
            int size = users.size();// This way, you will pre-populate your list.
            User v = User.findById(id_player);
            String username = v.getString("username");
            List<HashMap> order = new LinkedList<HashMap>();
            HashMap mp;
            for (User u : users ) {
                mp = new HashMap();
                mp.put("id",u.getString("id"));
                mp.put("itemName",u.getString("username"));
                order.add(mp);
            }

            Map<String, Object> map = new HashMap();
            map.put("order", order);
            map.put("username",username);
            map.put("id_player",id_player);

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
            String name_player1 = u.getString("username");

            User v = User.findById(id_player_2);
            String name_player2 = v.getString("username");

            Map map = new HashMap();
            map.put("win1",win1);
            map.put("win2",win2);
            map.put("deu",deu);
            map.put("name_player1",name_player1);
            map.put("name_player2",name_player2);

            return new ModelAndView(map, "showrecords.mustache");
            
        }, new MustacheTemplateEngine());

    }

    public static String randomSound (){
        int n = ThreadLocalRandom.current().nextInt(1, 30);
        String sound;
        switch (n) {
            case 1:  sound = "music/AI/random.mp3";
                     break;
            case 2:  sound = "music/AI/random1.mp3";
                     break;
            case 3:  sound = "music/AI/random2.mp3";
                     break;
            case 4:  sound = "music/AI/random3.mp3";
                     break;
            case 5:  sound = "music/AI/random4.mp3";
                     break;
            case 6:  sound = "music/AI/random5.mp3";
                     break;
            case 7:  sound = "music/AI/random6.mp3";
                     break;
            case 8:  sound = "music/AI/random7.mp3";
                     break;
            case 9:  sound = "music/AI/random8.mp3";
                     break;         
            default: sound = "music/point.mp3";
                     break;
        }
        return sound;
    }    

}
