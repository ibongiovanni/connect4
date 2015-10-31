package com.unrc.app.ai; 
import java.util.Scanner;

public class Board{
    byte[][] board = new byte[6][7];
    
    public Board(){
        board = new byte[][]{
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},    
        };
    } 
    
    public boolean isLegalMove(int column){
        return board[0][column]==0;
    }
    
    //Placing a Move on the board
    public boolean placeMove(int column, int player){ 
        if(!isLegalMove(column)) {System.out.println("Illegal move!"); return false;}
        for(int i=5;i>=0;--i){
            if(board[i][column] == 0) {
                board[i][column] = (byte)player;
                return true;
            }
        }
        return false;
    }
    
    public void undoMove(int column){
        for(int i=0;i<=5;++i){
            if(board[i][column] != 0) {
                board[i][column] = 0;
                break;
            }
        }        
    }
    //Printing the board
    public void displayBoard(){
        System.out.println();
        for(int i=0;i<=5;++i){
            for(int j=0;j<=6;++j){
                System.out.print(board[i][j]+" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public void resetBoard(){
        board = new byte[][]{
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},
            {0,0,0,0,0,0,0,},    
        };
    }
}
