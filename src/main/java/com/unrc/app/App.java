package com.unrc.app;

import com.unrc.app.User;
import org.javalite.activejdbc.Base;

/**
 * Hello world!
 *
 */
public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );

        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/connect4_development", "root", "root");

        // User u = new User();
        // u.set("first_name", "Ariel");
        // u.save();

        // User w = new User();
        // w.set("first_name", "Pedro");
        // w.save();
        

        Game g = new Game(1, 2, 6, 7);

        g.save();

        g.playOffLine(1,-1);
        


        Base.close();


    }
}
