package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.trackClasses.Playlist;
import sample.trackClasses.Track;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

//another singlet, for learning and convenience's sake. will just store the data as binary data for now.
//the file will keep a record of the absolute paths of any tracks or albums that are added/saved to the
//music player. this way will not need to copy/move files around to new directories for albums/playlists,
//as playlists will be played by reference to the paths the files are in(i.e. there is only ever one copy of any
//given track).
public class DataStore {
    private Path musicStorage = FileSystems.getDefault().getPath("trackStorage.txt");
    private ObservableList<Playlist> playlistList = FXCollections.observableArrayList();
    private ObservableList<Album> albumList =FXCollections.observableArrayList();
    private ObservableList<Track> trackList = FXCollections.observableArrayList();
    private static DataStore instance = new DataStore();


    private DataStore() {

    }

//    static {
//        File pathos = new File("plac-yunosuke-pathos-pathos.wav");
//        Track pathTrack = new Track("pathos", new Duration(366), pathos);
//        ArrayList<Track> tempList = new ArrayList<>();
//        tempList.add(pathTrack);
//        File gimme = new File("plac-yunosuke-pathos-gimmeyoutlove.wav");
//        Track gimmeTrack = new Track("gimme", new Duration(477), gimme);
//        tempList.add(gimmeTrack);
//        trackList.setAll(tempList);
//    }

    public static DataStore getInstance() {
       return instance;
    }

    public void addTrack(Track track) {
        trackList.add(track);
    }



    public  ObservableList<Track> getTracks() {
        return trackList;
    }

    public void setTrackList(ObservableList<Track> trackList) {
        this.trackList = trackList;
    }

    public void addPlaylist(Playlist playlist) {
        this.playlistList.add(playlist);
    }

   public void addAlbum(Album album) {
        this.albumList.add(album);
   }

   public ObservableList<Album> getAlbums() {
        return albumList;
   }



}
