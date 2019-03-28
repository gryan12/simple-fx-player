package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.FileManager;
import sample.trackClasses.Track;
import unused.FilePlayer;


import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.*;
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

public class TrackController implements AutoPlayer.MyChangeListener {
    //for testing
    public Track currentSong;
    public File currentFile;
    public static Thread musicPlayerThread;
    private AutoPlayer player;
    private AlbumController albumController;
    private Scene albumScene;
    @FXML
    private ObservableList<Track> currentTrackList = FXCollections.observableArrayList();

    @FXML
    private BorderPane mainPane;
    @FXML
    private Label currentSongLabel;
    @FXML
    private TextArea rightDetailsArea;
    @FXML
    private Button play;
    @FXML
    private Button pause;
    @FXML
    private Button next;
    @FXML
    private Button prev;
    @FXML
    private ListView<Track> trackListView;
    @FXML
    private Button textPrev;
    @FXML
    private AnchorPane centrePane;
    @FXML
    private Button testPause;
    @FXML
    private Button testResume;
    @FXML
    private Button testPrev;
    @FXML
    private Label currentTrackLabel;
    @FXML
    private Label viewLabel;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private Slider slider;

    private FileManager manager;


    public TrackController() {
        player = new AutoPlayer();
        player.setListener(this);
        this.manager = new FileManager();
    }


    @Override
    public void onChangeHappened() {
        setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
    }









    @FXML
    public void initialize() {
    }

    public AutoPlayer getPlayer() {
        return player;
    }

    public Scene getAlbumScene() {
        return albumScene;
    }






    //====functions to change the view of tracks====

    public void setViewTracks(List<Track> trackList) {
        trackListView.getItems().setAll(trackList);
    }


    @FXML
   public void changeTrackView(List<Track> list) {
       DataStore.getInstance().setCurrent(list);
       setViewTracks(DataStore.getInstance().getCurrentTracks());
   }

   @FXML
   public void viewAllTracks() {
        changeTrackView(DataStore.getInstance().getTracks());
   }

    //=========

    public void setAlbumController(AlbumController albumController) {
        this.albumController = albumController;
    }

    public void setUpListView() {
        listContextMenu = new ContextMenu();
        MenuItem playMenuItem = new MenuItem("Play");
        playMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Track track = trackListView.getSelectionModel().getSelectedItem();
                try {
                    contextPlay();
                } catch (Exception e) {
                    System.out.println("cant play that sonny");
                    e.printStackTrace();
                }
            }
        });
        listContextMenu.getItems().addAll(playMenuItem);

        trackListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Track>() {
            @Override
            public void changed(ObservableValue<? extends Track> observable, Track oldValue, Track newValue) {
                if (newValue != null) {
                    Track track = trackListView.getSelectionModel().getSelectedItem();
//                    rightDetailsArea.setText("Name: " + track.getTitle()
//                            + "\n\n" + "Duration: " + track.getDuration().toString());
                }
            }
        });
        trackListView.setItems(DataStore.getInstance().getCurrentTracks());
        trackListView.setCellFactory(new Callback<ListView<Track>, ListCell<Track>>() {
            @Override
            public ListCell<Track> call(ListView<Track> param) {
                ListCell<Track> cell = new ListCell<Track>() {
                    @Override
                    protected void updateItem(Track item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getTitle());
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        });
                cell.hoverProperty().addListener(((observable, wasHovered, isNowHovered) -> {
                    if (isNowHovered && !cell.isEmpty()) {
                        Track track = cell.getItem();
                        rightDetailsArea.setText("Name: " + track.getTitle()
                                + "\n\n" + "Duration: " + track.getDuration().toString());
                    }
                }));
                return cell;
            }
        });
    }



    public void setAlbumScene(Scene scene) {
        albumScene = scene;
    }
    //use this genreal style to chang ethe infroamtio in the center of the screen based on which song is playing
    @FXML
    public void handleClickListView() {
        Track track = trackListView.getSelectionModel().getSelectedItem();
    }



    @FXML
    public void textPrev() {
      trackListView.getSelectionModel().selectPrevious();
    }



    @FXML
    public void openAlbumScene(ActionEvent ae) {
       Stage primaryStage = (Stage)((Node)ae.getSource()).getScene().getWindow();
       primaryStage.setScene(albumScene);
    }


    public void openAllTracks() {
        setViewTracks(DataStore.getInstance().getTracks());
    }

//update the list view for this
    @FXML
    public void loadSingleTrack() {
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
        if (file != null) {
            manager.loadTrack(file);
        }
    }




    @FXML
    public void loadAlbum() {
        DirectoryChooser chooser = new DirectoryChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showDialog(mainPane.getScene().getWindow());
        if (file != null) {

            manager.loadAlbum(file);
            changeTrackView(DataStore.getInstance().getTracks());
        } else {
            System.out.println("exiting chooser");
            return;
        }
    }



        //funfctions to handle music player controls =============== many unused need to cleanup
    //the player seems to work fine even though it shouldnt - for some reason the designated track plays even
    //when an index is not surplied.
    @FXML
    public void testPlay() {
        player.populateList(trackListView.getItems());
        System.out.println(player.getCurrentIndex());
        player.play();
    }


    //PLACEHOLDER ======== NEED FUNCTION IN AUTOPLAYER WHICH INFORMS CONTROLLER OF CURRENT TRACK, RATHER THAN
    //IS IMPLEMENTED IN THIS MEHTOD
    public void contextPlay() {
        int count = 0;
        int index = 0;
        for (Track track: trackListView.getItems()) {
            if (track == trackListView.getSelectionModel().getSelectedItem()) {
               index = count;
            }
            count ++;
        }
        player.populateList(trackListView.getItems(), index);
        System.out.println(player.getCurrentIndex());
        setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
        player.newPlay();

    }

    //again change label to auto updating, placeholder currently
    @FXML
    public void handlePlayerControlls(ActionEvent ae) {
        if (ae.getSource() == play) {
            if (!player.shouldResume()) {
                contextPlay();
            } else {
                player.resume();
                setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
            }
        } else if (ae.getSource() == pause) {
            System.out.println("pause");
            player.pause();
        } else if (ae.getSource() == next) {
            System.out.println("next");
            player.next();
            setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()+1));
        } else if (ae.getSource() == prev) {
            System.out.println("prev");
            player.prev();
            setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()-1));
        }
    }


    public void handleTestPlay() {

        if (!player.shouldResume()) {
            contextPlay();
        } else {
            player.resume();
        }
    }


    //=======================

    //======================

    @FXML
    public void changeToAlbumView() {
        try {
            Parent albumViewParent = FXMLLoader.load(getClass().getResource("albumView.fxml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sliderListener() {

    }

    public void setViewLabel(String text) {
       this.viewLabel.setText(text);
    }
    public void setcurrentTrackLabel(Track track) {
       currentTrackLabel.setText(track.getTitle());
    }





}
