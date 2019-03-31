
/*
Class: Track

Author: George Ryan

Date: 01/11/2018

 */
package sample.trackClasses;

import java.io.File;
import java.io.Serializable;

public class Track implements Serializable {

    protected String title;
    protected Duration trackDuration;
    protected File file;
    protected Album album;

    private long serialVersionUID = 5L;



    public String getTitle() {
        return title;
    }

    public Duration getDuration() {
        return trackDuration;
    }

    public File getFile() {
        return file;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public Album getAlbum() {
        return this.album;
    }

    public void setTrackDuration(Duration trackDuration) {
        this.trackDuration = trackDuration;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Track() {
        this.title = "no title ";
        this.trackDuration = trackDuration;
    }

    public Track(String title, Duration trackDuration, File file) {
        this.title = title;
        this.trackDuration = trackDuration;
        this.file = file;
        this.album = null;
    }
    public Track(String title, Duration trackDuration, File file, Album album) {
        this.title = title;
        this.trackDuration = trackDuration;
        this.file = file;
        this.album = album;
    }


}
