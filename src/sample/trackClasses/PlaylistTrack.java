package sample.trackClasses;

/*
Class: PlaylistTrack

Author: George Ryan

Date: 04/11/2018

 */
public class PlaylistTrack extends Track {

    private Album trackAlbum;

    public PlaylistTrack() {
        this.trackAlbum = new Album();
    }

    public PlaylistTrack(Album trackAlbum, Track track) {
        this.trackAlbum = trackAlbum;
        trackDuration = track.getDuration();
        title = track.getTitle();
    }

    public void assignAlbum(Album anyAlbum) {
        this.trackAlbum = anyAlbum;

    }

    public Album getTrackAlbum() {
        return trackAlbum;
    }
}
