/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anfarfanfar.spotifycomp;

import java.util.logging.Level;
import java.util.logging.Logger;
import shed.mbed.Accelerometer;
import shed.mbed.Button;
import shed.mbed.LCD;
import shed.mbed.LED;
import shed.mbed.LEDColor;
import shed.mbed.MBed;
import shed.mbed.MBedUtils;
import shed.mbed.Note;
import shed.mbed.Piezo;
import shed.mbed.Potentiometer;

/**
 * @author Anfar
 */
public class Program {

    private MBed myMBed;
    private Button skipForward;
    private Button skipBackward;
    private Button playPause;
    private Button closeButton;
    private Button refreshButton;
    private Potentiometer volumeDial;
    private int volume = 0;
    private boolean change = true;
    private LCD lcd1;
    private String[] arr;
    private SpotifyWrap wrapper;
    private LED led1;
    private LED led2;
    private Accelerometer acc;
    private boolean hasShaked = false;
    private boolean shuffleOn = false;
    private int scrollPlacementTrack = 0;
    private boolean scroll = false;
    private boolean scrollBack = false;
    private Piezo speaker;

    /**
     * Constructor for the program
     */
    public Program() {
        initialize();
    }

    private void initialize() {
        myMBed = MBedUtils.getMBed();
        lcd1 = myMBed.getLCD();
        skipForward = myMBed.getSwitch2();
        skipBackward = myMBed.getSwitch3();
        playPause = myMBed.getJoystickFire();
        volumeDial = myMBed.getPotentiometer1();
        closeButton = myMBed.getJoystickUp();
        refreshButton = myMBed.getJoystickDown();
        led1 = myMBed.getLEDBoard();
        led2 = myMBed.getLEDShield();
        acc = myMBed.getAccelerometerBoard();
        wrapper = new SpotifyWrap();
        shuffleOn = wrapper.getShuffleStatus();
        speaker = myMBed.getPiezo();
        buttonListeners();
        changeCurrentSong();
    }

    /**
     * Changes the now playing song on the screen
     */
    public void changeCurrentSong() {
        if (change) {
            if (arr == null) {
                arr = new String[2];
                arr[0] = "";
                arr[1] = "";
            }
            String[] arr2 = wrapper.getTrack();

            //check Arrays.equals or collections
            if (!arr[1].equals(arr2[1]) && !arr[0].equals(arr2[0])) {
                arr = arr2;
                lcd1.clear();
                printToScreen(arr[0], arr[1]);
                change = false;
                scroll = (arr[1].length() > 20);
            }
        }
        if (scroll) {
            printToScreen(arr[0], arr[1]);
        }
    }

    public String chopToSize(String stringToChop) {
        int howLongString = 20;
        if (stringToChop.length() > howLongString) {
            String returnString = "";
            for (int i = 0; i < howLongString; i++) {
                char c = stringToChop.charAt(i);
                returnString += c;
            }
            return returnString;
        } else {
            return stringToChop;
        }
    }

    public String scrollToNextPositionTrack(String stringToScroll) {
        if (scroll) {
            String returnString = "";
            for (int i = scrollPlacementTrack; i < stringToScroll.length(); i++) {
                char c = stringToScroll.charAt(i);
                returnString += c;
            }
            scrollPlacementTrack++;
            if (returnString.length() <= 0) {
                scrollPlacementTrack = 0;
            }
            return chopToSize(returnString);
        }
        return chopToSize(stringToScroll);
    }

    /**
     * Prints strings to the devices screen. Also checking if the string size is
     * good
     *
     * @param artist
     * @param track
     */
    public void printToScreen(String artist, String track) {
        lcd1.clear();
        lcd1.print(0, 0, "Now playing:");
        lcd1.print(0, 10, chopToSize(artist));
        lcd1.print(0, 20, scrollToNextPositionTrack(track));
    }

    public void playSomeSounds() {
        speaker.playNote(Note.C6);
        try {
            Thread.sleep(50);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        speaker.silence();
    }

    /**
     * Sets shuffle on if device is shaken
     */
    public void shake() {
        double howHard = 2.0;
        if (acc.getAcceleration().getMagnitude() > howHard) {
            hasShaked = true;
        }
        if (acc.getAcceleration().getMagnitude() < 1.3 && hasShaked) {
            hasShaked = false;
            shuffleOn = !shuffleOn;
            wrapper.setShuffleStatus(shuffleOn);
        }
        if (shuffleOn) {
            led2.setColor(LEDColor.GREEN);
        } else {
            led2.setColor(LEDColor.BLACK);
        }
    }

    /**
     * Adds all the listeners for all buttons and dials
     */
    public void buttonListeners() {
        skipForward.addListener(isPressed -> {
            if (isPressed) {
                change = true;
                playSomeSounds();
                wrapper.skipUsersPlaybackToNextTrack_Sync();
            }
        });
        skipBackward.addListener(isPressed -> {
            if (isPressed) {
                change = true;
                playSomeSounds();
                wrapper.skipUsersPlaybackToPreviousTrack_Sync();
            }
        });
        closeButton.addListener(isPressed -> {
            if (isPressed) {
                playSomeSounds();
                close();
            }
        });
        refreshButton.addListener(isPressed -> {
            if (isPressed) {
                change = true;
                playSomeSounds();
            }
        });
        playPause.addListener(isPressed -> {
            playSomeSounds();
            if (isPressed) {
                if (wrapper.getIsPlaying()) {
                    wrapper.pause();
                    led1.setColor(LEDColor.WHITE);
                } else {
                    wrapper.play();
                    led1.setColor(LEDColor.GREEN);
                }
            }
        });
        volumeDial.addListener((double value) -> {
            double volumeD = value * 100;
            int volume2 = (int) volumeD;
            if ((volume2 + 1) < volume || (volume2 - 1) > volume) {
                volume = volume2;
                wrapper.setVolume(volume);
            }
        });
    }

    /**
     * Returns the MBed object
     *
     * @return Returns the MBed object
     */
    public MBed getMBed() {
        return myMBed;
    }

    /**
     * Closes the connection to the MBed
     */
    public void close() {
        myMBed.close();
    }
}
