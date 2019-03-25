package sample.trackClasses;
/*
Class: Album

Author: George Ryan

Date: 01/11/2018

 */
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Album {

    private String name;
    private String artist;
    private ArrayList<Track> trackList;
    private Image albumArtwork;

    public Album() {
        this.name = "no name";
        this.artist = "unknown artist";
        this.trackList = new ArrayList<>();
        this.albumArtwork = null;
    }

    public Album(String artist, String name, ArrayList<Track> trackList) {
        this.name = name;
        this.artist = artist;
        this.trackList = trackList;
    }
    
    public Album(String artist, String name) {
        this.name = name;
        this.artist = artist;
        this.trackList = new ArrayList<>();
    }

    public void addToAlbum(Track newTrack) {
        trackList.add(newTrack);
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Image getAlbumArtwork() {
        return albumArtwork;
    }

    public void setAlbumArtwork(Image albumArtwork) {
        this.albumArtwork = albumArtwork;
    }

    public ArrayList<Track> getTrackList()
    {
        return trackList;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTrackList(ArrayList<Track> trackList) {
        this.trackList = trackList;
    }
}
