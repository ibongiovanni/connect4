package com.unrc.app;

import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.unrc.app.*;

import static org.junit.Assert.assertEquals;
import static org.javalite.test.jspec.JSpec.the;


public class GridTest {
    @Before
    public void before(){
        Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/connect4_test", "root", "root");
        System.out.println("GridTest setup");
        Base.openTransaction();
    }

    @After
    public void after(){
        System.out.println("GridTest tearDown");
        Base.rollbackTransaction();
        Base.close();
    }

    @Test
    public void fullColum(){
        Grid g = new Grid();
        g.set("game_id", 1);
        Cell c = new Cell();
        c.set("game_id", 1);
        c.set("x_coord", 4);
        c.set("y_coord", 0);
        c.set("state", 1);
        c.save();
        assertEquals(g.fullColum(4),true);
    }

   
}