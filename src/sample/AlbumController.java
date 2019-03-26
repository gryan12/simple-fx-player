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

public void initialize() {
    }

    public void setTrackController(TrackController trackController) {
        this.trackController = trackController;
    }

    public void setScene(Scene trackScene) {
        this.trackScene = trackScene;
    }

    @FXML
    public void openTrackScene(ActionEvent ae) {
        Stage primaryStage = (Stage)((Node)ae.getSource()).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    //track view with all loaded tracks
    @FXML
    public void goToAllTracks() {
        System.out.println("all tracks called");

        DataStore.getInstance().remakeCurrent(DataStore.getInstance().getTracks());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        System.out.println("size current: " + DataStore.getInstance().getCurrentTracks().size());
        System.out.println("\n size total: " + DataStore.getInstance().getTracks().size());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    //trackview with that album's tracks
    @FXML
    public void openAlbum(Album album) {


        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
        DataStore.getInstance().setCurrentTracks(album.getTrackList());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());

    }

    @FXML
    public void openAlbum() {
        Album album = albumListView.getSelectionModel().getSelectedItem();
        trackController.setViewTracks(album.getTrackList());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }


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
    public void loadSong() {
        FileChooser chooser = new FileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        chooser.setInitialDirectory(new File("./"));

        try {
            File newFile = chooser.showOpenDialog(mainPane.getScene().getWindow());

            Track newTrack = FileManager.getInstance().loadTrack(newFile);
            DataStore.getInstance().addTrack(newTrack);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadAlbum() {
        DirectoryChooser chooser = new DirectoryChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showDialog(mainPane.getScene().getWindow());
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
                String[] details = streamPath.toString().split("-");
                String trackName = details[4];
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
            }
            DataStore.getInstance().addAlbum(newAlbum);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
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

    public void setPlayer(AutoPlayer player) {
        this.player = player;
    }
}
