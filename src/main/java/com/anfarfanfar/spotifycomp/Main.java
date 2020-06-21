/*
 * Main method used to start the program
 */
package com.anfarfanfar.spotifycomp;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Anfar
 */
public class Main {

    public static void main(String[] args){
            
        Program myProg = new Program();
        
        while (myProg.getMBed().isOpen()) {
            
            myProg.changeCurrentSong();
            myProg.shake();
                        
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        System.exit(0);
    }

    
}
