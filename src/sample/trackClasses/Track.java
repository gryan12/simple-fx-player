
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

    public Track() {
        this.title = "no title ";
        this.trackDuration = trackDuration;
    }

    public Track(String title, Duration trackDuration, File file) {
        this.title = title;
        this.trackDuration = trackDuration;
        this.file = file;
    }


}
