package sample.trackClasses;
/*
Class: Playlist

Author: George Ryan

Date: 01/11/2018

 */
import unused.PlaylistTrack;

import java.util.ArrayList;

public class Playlist {

    private AlbumCollection theCollection;
    private ArrayList<PlaylistTrack> playlistTrackList;

    public Playlist() {
        theCollection = new AlbumCollection();
        playlistTrackList = new ArrayList<>();
    }

    public Playlist(AlbumCollection theCollection,
            ArrayList<PlaylistTrack> playlistTrackList) {
        this.theCollection = theCollection;
        this.playlistTrackList = playlistTrackList;
    }

    public void addTrack(PlaylistTrack track) {
        playlistTrackList.add(track);
    }

    public AlbumCollection getTheCollection() {
        return theCollection;
    }

    public ArrayList<PlaylistTrack> getPlaylistTrackList() {
        return playlistTrackList;
    }

}
