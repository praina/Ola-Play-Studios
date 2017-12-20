package com.example.jackhammer.olaplaystudios;

/**
 * Created by jackhammer on 16/12/17.
 */

public class DataObject {

    private String SongName;
    private String SongURL;
    private String SongArtists;
    private String CoverImageURL;

    public String getSongName() {
        return SongName;
    }

    public void setSongName(String songName) {
        SongName = songName;
    }

    public String getSongURL() {
        return SongURL;
    }

    public void setSongURL(String songURL) {
        SongURL = songURL;
    }

    public String getSongArtists() {
        return SongArtists;
    }

    public void setSongArtists(String songArtists) {
        SongArtists = songArtists;
    }

    public String getCoverImageURL() {
        return CoverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        CoverImageURL = coverImageURL;
    }

}
