
/*
Class: Duration 

Author: George Ryan

Date: 01/11/2018

 */

package sample.trackClasses;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Duration implements Serializable {

    private int seconds;
    private int minutes;
    private int hours;

    private long serialVersionUID = 4L;

    public Duration() {
        this.seconds = 0;
        this.minutes = 0;
        this.hours = 0;
    }

    public Duration(int hours, int minutes, int seconds) {
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
    }

    public Duration(String timeString) {
        String[] timeArray = timeString.split(":");
        this.hours = Integer.parseInt(timeArray[0]);
        this.minutes = Integer.parseInt(timeArray[1]);
        this.seconds = Integer.parseInt(timeArray[2]);

    }

    public Duration(int seconds) {
        int hours = seconds/3600;
        int minutes = (seconds%3600)/60;
        int sec= seconds%60;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = sec;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getHours() {
        return hours;
    }

    @Override
    public String toString() {
        return String.format("%2d:%02d:%02d", hours, minutes, seconds);
    }
    
    //as no tracks in the provided file are over 1hr long, this allows for 
    //cleaner presentation 
    public String toMinuteString() {
        return String.format("%02d:%02d",minutes,seconds);
    }

    public void printDuration() {
        System.out.print(String.format("%2d:%02d:%02d",
                hours, minutes, seconds));
    }
    
    //convert to seconds, add, convert back to h/n/s format
    public Duration addDuration(Duration dur1) {
        int time3;
        Duration dur3 = new Duration();
        int time1 = this.hours * (3600) + this.minutes * 60 + this.seconds;
        int time2 = dur1.hours * (3600) + dur1.minutes * 60 + dur1.seconds;
        time3 = time1 + time2;
        dur3.hours = time3 / 3600;
        dur3.minutes = (time3 % 3600) / 60;
        dur3.seconds = (time3 % 60);
        return dur3;

    }
    //used for making keeping track of longest tracks easier
    public int getTotalSeconds() {
        int totalSeconds = this.hours * 3600 + this.minutes * 60 + this.seconds;
        return totalSeconds;
    }


    public static void main(String[] args) {
        Duration duration = new Duration(1000);
        duration.printDuration();
    }

    public int extractDuration(File file) {
        float totalLength = 0;
        try (AudioInputStream streaminput = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format;
            format = streaminput.getFormat();
            long size = file.length();
            int frameSize = format.getFrameSize();
            float frameRate = format.getFrameRate();
             totalLength = (size / (frameSize * frameRate));
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
        return (int)totalLength;
    }


}
