package sample.trackClasses;
/*
Class: Album

Author: George Ryan

Date: 01/11/2018

 */
import javafx.scene.image.Image;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Album implements Serializable {

    private String name;
    private String artist;
    private ArrayList<Track> trackList;
    private File albumArtwork;
    private long serialVersionUID = 7L;

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
        this.albumArtwork=null;
    }
    
    public Album(String artist, String name) {
        this.name = name;
        this.artist = artist;
        this.trackList = new ArrayList<>();
        this.albumArtwork = null;
    }


    //not ideal for now i do not think, but its okay.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Album)) return false;
        Album album = (Album) o;
        return Objects.equals(name, album.name) &&
                Objects.equals(artist, album.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, artist);
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

    public File getAlbumArtwork() {
        return albumArtwork;
    }

    public void setAlbumArtwork(File albumArtwork) {
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
