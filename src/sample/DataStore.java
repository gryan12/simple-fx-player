package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.trackClasses.Playlist;
import sample.trackClasses.Track;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//another singlet, for learning and convenience's sake. will just store the data as binary data for now.
//the file will keep a record of the absolute paths of any tracks or albums that are added/saved to the
//music player. this way will not need to copy/move files around to new directories for albums/playlists,
//as playlists will be played by reference to the paths the files are in(i.e. there is only ever one copy of any
//given track).

//the currentTrackList is used for displaying total traks in the list view, instrad of the overall track list,
//hjust to help maintain integrity of the main tracklist during sessions
public class DataStore {
    private Path musicStorage = FileSystems.getDefault().getPath("trackStorage.txt");
    private ObservableList<Playlist> playlistList = FXCollections.observableArrayList();
    private ObservableList<Album> albumList =FXCollections.observableArrayList();
    private ObservableList<Track> trackList = FXCollections.observableArrayList();
    private ObservableList<Track>  currentTrackList = FXCollections.observableArrayList();
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

    public void setCurrentTracks(List<Track> trackList) {
       currentTrackList = FXCollections.observableArrayList(trackList);
    }




    public static DataStore getInstance() {
       return instance;
    }

    public void addTrack(Track track) {
        trackList.add(track);
    }

    public void setCurrentToTotal() {
        currentTrackList = FXCollections.observableArrayList(trackList);
    }

    public void setCurrent(List<Track> list) {
        currentTrackList = FXCollections.observableArrayList(list);
    }


//    public void remakeCurrent(List<Track> list) {
//        currentTrackList = FXCollections.observableArrayList(list);
//    }


    public  ObservableList<Track> getTracks() {
        return trackList;
    }

    public ObservableList<Track> getCurrentTracks() {
        return currentTrackList;
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


   //storing via hashset just helps to ensure that duplicates are not sneakily being stored.
    //might be worth adding tracklist to equals method after finished testing
   public void storeAlbumsInFile() {
       try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("albums.dat")))) {
           Set<Album> albumSet = new HashSet<>();

           for (Album album: albumList) {
               albumSet.add(album);
           }
           for (Album album: albumSet) {
               output.writeObject(album);
           }
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

  public void storeTracksInFile() {
      try(ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("tracks.dat")))) {
          for (Track track: trackList) {
              output.writeObject(track);
          }
      } catch (IOException e) {
          e.printStackTrace();
      }
  }

  public void loadTracks() {
      try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream("tracks.dat")))) {
          boolean eof = false;
          while (!eof) {
              try {
                  Track track = (Track) input.readObject();
                  System.out.println("Track: " + track.getTitle() + " : " + track.getDuration().toString());
                  trackList.add(track);
              } catch (EOFException e) {
                  eof = true;
              }
          }
      } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
      }
      currentTrackList.setAll(trackList);
  }
  public void loadAlbums() {
      try (ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream("albums.dat")))) {
          boolean eof = false;
          while (!eof) {
              try {
                  Album album = (Album) input.readObject();
                  System.out.println("Album: " + album.getName() + " : " + album.getArtist());
                  albumList.add(album);
              } catch (EOFException e) {
                  eof = true;
              }
          }
      } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
      }

      for (Album album: albumList) {
          for (Track track: album.getTrackList()) {
              trackList.add(track);
          }
      }
      currentTrackList.setAll(trackList);

  }


   public void storeData() {

//        storeTracksInFile();
        storeAlbumsInFile();
   }
   public void loadData() {
//        loadTracks();
        loadAlbums();
   }

    public static void main(String[] args) {
        Album newAbum = new Album("Pathos", "Yunosuke");
            File pathos = new File("plac-yunosuke-pathos-pathos.wav");
           Track pathTrack = new Track("pathos", new Duration(366), pathos);
           File gimme = new File("plac-yunosuke-pathos-gimmeyourlove.wav");
           Track gimmeTrack = new Track("gimme", new Duration(477), gimme);
            instance.addTrack(gimmeTrack);
            instance.addTrack(pathTrack);
           newAbum.addToAlbum(gimmeTrack);
           newAbum.addToAlbum(pathTrack);
           instance.addAlbum(newAbum);

           instance.storeData();


    }

}
