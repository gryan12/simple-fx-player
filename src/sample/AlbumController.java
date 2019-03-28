package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
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
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class AlbumController {

    private Scene trackScene;
    private AutoPlayer player;
    private TrackController trackController;
    FileManager manager;


    @FXML
    ListView<Album> albumListView;

    @FXML
    BorderPane mainPane;


    @FXML
    TextArea rightDetailsArea;

    @FXML
    Button play;

    @FXML
    Button pause;

    @FXML
    Button next;

    @FXML
    Button prev;

    @FXML
    Label albumViewLabel;

    @FXML
    ContextMenu albumListContextMenu;

    @FXML
    ImageView tesdddddd;
//    @FXML
//    ImageView artworkImageView;

public void initialize() {
    }

    public AlbumController() {
        manager = new FileManager();
    }

    public void setTrackController(TrackController trackController) {
        this.trackController = trackController;
    }

    public void setScene(Scene trackScene) {
        this.trackScene = trackScene;
    }



    //============functions for moving between different view of tracks ===================
    @FXML
    public void openTrackScene(ActionEvent ae) {
        Stage primaryStage = (Stage)((Node)ae.getSource()).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    //track view with all loaded tracks
    @FXML
    public void goToAllTracks() {
        System.out.println("all tracks called");
        DataStore.getInstance().setCurrent(DataStore.getInstance().getTracks());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        trackController.setViewLabel("All Loaded Tracks");
        System.out.println("size current: " + DataStore.getInstance().getCurrentTracks().size());
        System.out.println("\n size total: " + DataStore.getInstance().getTracks().size());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    //trackview with that album's tracks
    @FXML
    public void openAlbum(Album album) {

        DataStore.getInstance().setCurrent(album.getTrackList());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        trackController.setViewLabel(album.getName() + " : " + album.getArtist());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    @FXML
    public void openAlbum() {
        Album album = albumListView.getSelectionModel().getSelectedItem();
        DataStore.getInstance().setCurrent(album.getTrackList());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        trackController.setViewLabel("All tracks in album: " + album.getName() + " by: " + album.getArtist());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

//=====================



    public void setUpAlbumView() {
        albumListContextMenu = new ContextMenu();
        MenuItem albumMenuItem = new MenuItem("Go To Album");
        albumMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Album album = albumListView.getSelectionModel().getSelectedItem();
                try {
                   openAlbum(album);
                } catch (Exception e) {
                    System.out.println("cant play that sonny");
                    e.printStackTrace();
                }
            }
        });
        albumListContextMenu.getItems().addAll(albumMenuItem);

//        albumListView.setItems(DataStore.getInstance().getAlbums());
        albumListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>() {
            @Override
            public void changed(ObservableValue<? extends Album> observable, Album oldValue, Album newValue) {
                if (newValue != null) {
                    Album album = albumListView.getSelectionModel().getSelectedItem();
                    rightDetailsArea.setText("Artist: " + album.getArtist()
                            + "\n" + "Album name: " + album.getName());
                    loadAlbumArtwork(album);
                }
            }
        });

        albumListView.setItems(DataStore.getInstance().getAlbums());
        albumListView.setCellFactory(new Callback<ListView<Album>, ListCell<Album>>() {
            @Override
            public ListCell<Album> call(ListView<Album> param) {
                ListCell<Album> cell = new ListCell<Album>() {
                    @Override
                    protected void updateItem(Album item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(albumListContextMenu);
                            }
                        });
                return cell;
            }
        });
    }

    @FXML
    public void loadSngleTrack() {
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
        } else {
            System.out.println("exiting chooser");
            return;
        }
    }
    @FXML
    public void handlePlayerControlls(ActionEvent ae) {
        if (ae.getSource() == play) {
            if (!player.shouldResume()) {
                player.populateList(albumListView.getSelectionModel().getSelectedItem().getTrackList());
                player.play();
            } else {
                player.resume();
            }
        } else if (ae.getSource() == pause) {
            player.pause();
        } else if (ae.getSource() == next) {
            player.next();
        } else if (ae.getSource() == prev) {
            player.prev();
        }
    }

    public AutoPlayer getPlayer() {
        return player;
    }

    public void loadAlbumArtwork(Album album) {
        System.out.println("this is being passed: " + album.getAlbumArtwork().getName());
//        Image img = new Image("/yunosuke-pathos/cover.jpg");
        tesdddddd = new ImageView(getClass().getResource("/yunosuke-pathos/cover.jpg").toExternalForm());
    }


    public void setPlayer(AutoPlayer player) {
        this.player = player;
    }
}
