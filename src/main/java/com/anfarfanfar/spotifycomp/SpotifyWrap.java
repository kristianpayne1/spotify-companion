/*
 * Includes all methods to communicate spotifys server
 * Uses Michael Thelins Spotify-Web-Api-Java library to connect to Spotify 
 */
package com.anfarfanfar.spotifycomp;

import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.requests.data.player.GetInformationAboutUsersCurrentPlaybackRequest;
import com.wrapper.spotify.requests.data.player.GetUsersAvailableDevicesRequest;
import com.wrapper.spotify.requests.data.player.PauseUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SetVolumeForUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToNextTrackRequest;
import com.wrapper.spotify.requests.data.player.SkipUsersPlaybackToPreviousTrackRequest;
import com.wrapper.spotify.requests.data.player.StartResumeUsersPlaybackRequest;
import com.wrapper.spotify.requests.data.player.ToggleShuffleForUsersPlaybackRequest;


public class SpotifyWrap {

    // https://developer.spotify.com/web-api/console/get-user-player/ get token from here
    // Tick user-read-playback-state, user-read-recently-player, user-modify-playback, user-read-currently-playing
    // Access token from spotifys site to gain access to certain user
    private final String ACCESSTOKEN = "BQB4q6ZVom_bOieb4s3NOObx0ynQr-7DbjyaR_5gDMUrSJ0FjX1RlViNdawvKRVpP2AtTe4ia3PYfLb5jZNL2a4X_ZlO9CLXQEFeINTgEs1qDcYBud24IZv3vNMobZ5THEEFPgdPBK3UJvIooUqQZ0QE";
    private final String CLIENTID = "b47e58639cc543d7812d44994a9dbe67";
    private final String CLIENTSECRET = "3a421699f2a4449588cf2b456e91ccdf";
    private String deviceID = "";

    //Spotifyapi builder for all the requests
    private SpotifyApi SPOTIFYAPI;

    public SpotifyWrap() {
        SPOTIFYAPI = new SpotifyApi.Builder()
                .setAccessToken(ACCESSTOKEN)
                .setClientId(CLIENTID)
                .setClientSecret(CLIENTSECRET)
                .build();

        initialize();
    }

    public final void initialize() {
        deviceID = getCurrentDevice().getId();
    }

    /**
     * Skips to next song on the queue
     */
    public void skipUsersPlaybackToNextTrack_Sync() {
        SkipUsersPlaybackToNextTrackRequest skipUsersPlaybackToNextTrackRequest = SPOTIFYAPI
                .skipUsersPlaybackToNextTrack()
                .device_id(deviceID)
                .build();
        try {
            final String string = skipUsersPlaybackToNextTrackRequest.execute();
        }catch (Exception e) {
            //expected, do nothing
        }
    }

    /**
     * Skips to previously played song
     */
    public void skipUsersPlaybackToPreviousTrack_Sync() {
        SkipUsersPlaybackToPreviousTrackRequest skipUsersPlaybackToPreviousTrackRequest = SPOTIFYAPI
                .skipUsersPlaybackToPreviousTrack()
                .device_id(deviceID)
                .build();
        try {
            final String string = skipUsersPlaybackToPreviousTrackRequest.execute();

        }catch (Exception e) {
            //expected, do nothing
        }
    }

    /**
     * Pauses playback
     */
    public void pause() {
        PauseUsersPlaybackRequest pauseUsersPlaybackRequest = SPOTIFYAPI.pauseUsersPlayback()
                .device_id(deviceID)
                .build();
        try {
            final String string = pauseUsersPlaybackRequest.execute();
        }catch (Exception e) {
            //expected, do nothing
        }
    }

    /**
     * Continues playback after pause
     */
    public void play() {
        

        String currentUri = getPlayingContext().getContext().getUri();
        

        StartResumeUsersPlaybackRequest startResumeUsersPlaybackRequest = SPOTIFYAPI
                .startResumeUsersPlayback()
                .context_uri(currentUri)
                .device_id(deviceID)
                .build();
        try {
            final String string = startResumeUsersPlaybackRequest.execute();

        }catch (Exception e) {
            //expected, do nothing
        }
    }
    
    public boolean getShuffleStatus(){
        CurrentlyPlayingContext currentContext = getPlayingContext();
        return currentContext.getShuffle_state();
    }
    
    public void setShuffleStatus(boolean shufStat){
        ToggleShuffleForUsersPlaybackRequest shuffleRequest = SPOTIFYAPI.toggleShuffleForUsersPlayback(shufStat)
                .device_id(deviceID)
                .build();
        try {
            shuffleRequest.execute();
        } catch (Exception e) {
            //System.out.println("Error: " + e.getMessage());
        }
    }
    
    public CurrentlyPlayingContext getPlayingContext(){
        GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest
                = SPOTIFYAPI.getInformationAboutUsersCurrentPlayback()
                        .market(CountryCode.SE)
                        .build();

        CurrentlyPlayingContext currentlyPlayingContext = null;
        try {
            currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
        } catch (Exception e) {
            //System.out.println("Error: " + e.getMessage());
        }
        return currentlyPlayingContext;
    }

    /**
     * Returns currently active device.
     *
     * @return Returns currently active device.
     */
    public Device getCurrentDevice() {
        GetUsersAvailableDevicesRequest getUsersAvailableDevicesRequest = SPOTIFYAPI
                .getUsersAvailableDevices()
                .build();

        Device myDevice = null;
        try {
            Device[] devices = getUsersAvailableDevicesRequest.execute();
            for (Device devi : devices) {
                if (devi.getIs_active()) {
                    myDevice = devi;
                }
            }
        }catch (Exception e) {
            System.out.println(e);
        }
        return myDevice;
    }

    /**
     * Returns boolean value if spotify is currently playing a song.
     *
     * @return Returns boolean value if spotify is currently playing a song.
     */
    public boolean getIsPlaying() {
        GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest
                = SPOTIFYAPI.getInformationAboutUsersCurrentPlayback()
                        .market(CountryCode.SE)
                        .build();

        boolean isPlaying = false;
        try {
            isPlaying = getInformationAboutUsersCurrentPlaybackRequest.execute().getIs_playing();
        }catch (Exception e) {
            //expected, do nothing
        }
        return isPlaying;
    }

    /**
     * Returns String array where in the first position is artist and second is
     * track name
     *
     * @return Returns String array where in the first position is artist and
     * second is track name
     */
    public String[] getTrack() {
        GetInformationAboutUsersCurrentPlaybackRequest getInformationAboutUsersCurrentPlaybackRequest
                = SPOTIFYAPI.getInformationAboutUsersCurrentPlayback()
                        .market(CountryCode.SE)
                        .build();

        CurrentlyPlayingContext currentlyPlayingContext = null;
        try {
            currentlyPlayingContext = getInformationAboutUsersCurrentPlaybackRequest.execute();
            String[] returnArray = new String[2];
            returnArray[0] = currentlyPlayingContext.getItem().getArtists()[0].getName();
            returnArray[1] = currentlyPlayingContext.getItem().getName();
            return returnArray;
        }catch (Exception e) {
            //expected, do nothing
            return null;
        }
    }

    /**
     * Sets the volume inside spotify to the given int value (0-100)
     *
     * @param volumePercentage the percentage amount as int for the volume
     */
    public void setVolume(int volumePercentage) {
        SetVolumeForUsersPlaybackRequest setVolumeForUsersPlaybackRequest = SPOTIFYAPI
                .setVolumeForUsersPlayback(volumePercentage)
                .device_id(deviceID)
                .build();

        try {
            final String string = setVolumeForUsersPlaybackRequest.execute();

            System.out.println("Null: " + string);
        }catch (Exception e) {
            //expected, do nothing
        }
    }
}
