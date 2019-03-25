package sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.trackClasses.Track;

import java.io.File;
import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader trackLoader = new FXMLLoader(getClass().getResource("trackView.fxml"));
        Parent trackPane = trackLoader.load();
        Scene trackScene = new Scene(trackPane, 800, 700);

        FXMLLoader albumLoader = new FXMLLoader(getClass().getResource("albumView.fxml"));
        Parent albumPane = albumLoader.load();
        Scene albumScene = new Scene(albumPane, 800, 700);

        TrackController trackController = (TrackController)trackLoader.getController();
        trackController.setAlbumScene(albumScene);

        AlbumController albumController = (AlbumController)albumLoader.getController();
        albumController.setScene(trackScene);
        albumController.setUpAlbumView();

        primaryStage.setTitle("All Tracks");
        primaryStage.setScene(trackScene);
        trackController.setUpListView();
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


    public void init() throws Exception {
       try {
           DataStore.getInstance().setTrackList(FXCollections.observableArrayList());
           File pathos = new File("plac-yunosuke-pathos-pathos.wav");
           Track pathTrack = new Track("pathos", new Duration(366), pathos);
           DataStore.getInstance().addTrack(pathTrack);
           File gimme = new File("plac-yunosuke-pathos-gimmeyourlove.wav");
           Track gimmeTrack = new Track("gimme", new Duration(477), gimme);
           DataStore.getInstance().addTrack(gimmeTrack);

           Album album = new Album();
           album.setName("Pathos");
           album.setArtist("Yunosuke");
//           Image pathosArtwork = new Image("cover.png");

           for (Track track: DataStore.getInstance().getTracks()) {
               album.addToAlbum(track);
           }

           DataStore.getInstance().addAlbum(album);

       } catch (Exception e) {
           System.out.println("wat in the fuq did u do");
       }
    }

    public void changeScenes(ActionEvent event) {
        try {
            System.out.println("pressed");
            Stage stageTheNodeBelongsTo = (Stage)((Node)event.getSource()).getScene().getWindow();

            Parent pane = FXMLLoader.load(getClass().getResource("albumView.fxml"));
            Scene albumScene = new Scene(pane, 900, 500);

        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }


}
