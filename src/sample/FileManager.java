package sample;

import javafx.stage.DirectoryChooser;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.trackClasses.Track;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FileManager {
    private Pattern regex = Pattern.compile( "([^\\s]+(\\.(?i)(wav|mp3))$)");


    public FileManager() {

    }

    public void loadAlbum(File file) {
            System.out.println(file.canRead());
            Album newAlbum;
            if (file.toString().contains("-")) {
                String[] albumDetails = file.toString().split("-");
                newAlbum = new Album(albumDetails[0], albumDetails[1]);
            } else {
                newAlbum = new Album(file.toString(), file.toString());
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath())) {
                for (Path streamPath : stream) {


                    String[] check = streamPath.toFile().toString().split("[.]");
                    if (check[1].equals("wav")) {
                        String[] details = streamPath.toString().split("-");
                        String trackName = details[details.length - 1];
                        Duration duration = new Duration();
                        AudioFormat format;
                        try (AudioInputStream streaminput = AudioSystem.getAudioInputStream(streamPath.toFile())) {
                            format = streaminput.getFormat();
                            long size = streamPath.toFile().length();
                            int frameSize = format.getFrameSize();
                            float frameRate = format.getFrameRate();
                            float totalLength = (size / (frameSize * frameRate));
                            duration = new Duration((int) totalLength);
                            Track newTrack = new Track(trackName, duration, streamPath.toFile());
                            newAlbum.addToAlbum(newTrack);
                            DataStore.getInstance().addTrack(newTrack);
                            for (String detail : details) {
                                System.out.println("\t" + detail);
                            }
                        }
                    } else if (check[1].equals("png") || check[1].equals("jpg")) {
                        System.out.println("image file detected");
                        newAlbum.setAlbumArtwork(streamPath.toFile());
                    } else {
                        System.out.println("junk file detected");
                        System.out.println(check[1]);
                        continue;
                    }
                }
                DataStore.getInstance().addAlbum(newAlbum);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    //like above, but takes a single wav file and returns a track object. again, works properly only when files re in
    //the bandcamp wav file naming format
    //need to change this so that it is more tolerant of different file types
    public Track loadTrack( File file) {

        AudioFormat format;
        Track track = new Track();
        try (AudioInputStream stream = AudioSystem.getAudioInputStream(file)) {
//            String[] details = file.getName().split("-");
//            String name = details[3];
            String[] details = file.getName().split("[.]");
            String name = details[0];

//            System.out.println("Name = " + name);
//            String[] namesplit = name.split(".");

//            for (String test: namesplit) {
//                System.out.println("test: " + namesplit);
//            }
            Duration duration;

            format = stream.getFormat();

            long length = file.length();
            float frameRate = format.getFrameRate();
            float frameSize = format.getFrameSize();
            float totalSeconds = (length / (frameRate * frameSize));

            duration = new Duration((int) totalSeconds);

            track = new Track(name, duration, file);

        } catch (IOException | UnsupportedAudioFileException ie) {
            ie.printStackTrace();
        }
        return track;
    }




    public static void main(String[] args) {


    }


}
