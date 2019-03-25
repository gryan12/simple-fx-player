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
import java.util.regex.Pattern;

public class TrackController {
    //for testing
    public Track currentSong;
    public File currentFile;
    public static Thread musicPlayerThread;
    private AutoPlayer player = new AutoPlayer();


    private Scene albumScene;


    @FXML
    private ObservableList<Track> currentTrackList = FXCollections.observableArrayList();

    @FXML
    BorderPane mainPane;

    @FXML
    Label currentSongLabel;

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
    ListView<Track> trackListView;

    @FXML
    Button textPrev;

    @FXML
    AnchorPane centrePane;

    @FXML
    Button testPause;

    @FXML
    Button testResume;

    @FXML
    Button testPrev;


    @FXML
    ContextMenu listContextMenu;

    public void initialize() {
    }


    public Scene getAlbumScene() {
        return albumScene;
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
                    rightDetailsArea.setText("Track: " + track.getTitle()
                            + "\n" + "Duration: " + track.getDuration().toString());
                }
            }
        });
        trackListView.setItems(DataStore.getInstance().getTracks());
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


    @FXML
    public void loadAlbum() {
        Album album = new Album();
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showDialog(mainPane.getScene().getWindow());
        if (file != null) {
            try {
                album = FileManager.getInstance().directoryToAlbum(FileSystems.getDefault().getPath(file.getAbsolutePath()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Track track : album.getTrackList()) {
                DataStore.getInstance().addTrack(track);
            }
        }
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
    public void testLoad() {
        DirectoryChooser chooser = new DirectoryChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showDialog(mainPane.getScene().getWindow());
        System.out.println(file.canRead());
        String[] albumDetails = file.toString().split("-");
        Album newAlbum = new Album(albumDetails[0], albumDetails[1]);
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

    @FXML
    public void handleControlls(ActionEvent ae) {
        if (ae.getSource() == play) {
            final Task task = new Task() {
                @Override
                protected Object call()  throws Exception {
                    FilePlayer.getInstance().playtest(trackListView.getSelectionModel().getSelectedItem().getFile());
                    return null;
                }
            };
            System.out.println("play pressed");
            musicPlayerThread = new Thread(task);
            musicPlayerThread.start();
        } else if (ae.getSource() == pause) {
            System.out.println("paused pressed");
            if (FilePlayer.isIsPlaying()) {
                FilePlayer.getInstance().pause();
            } else {
                return;
            }
        } else if (ae.getSource() == next) {
            trackListView.getSelectionModel().selectNext();
            if (FilePlayer.isIsPlaying()) {
                FilePlayer.getInstance().pause();
                play(trackListView.getSelectionModel().getSelectedItem().getFile());
            }
            } else if (ae.getSource() == prev) {
                trackListView.getSelectionModel().selectPrevious();
                if (FilePlayer.isIsPlaying()) {
                    FilePlayer.getInstance().pause();
                    play(trackListView.getSelectionModel().getSelectedItem().getFile());
                }

            }
        }


    @FXML
    public void testPlay() {
        player.populateList(trackListView.getItems());
        player.play();
    }

    @FXML
    public void testPause() {
        player.pause();
    }

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
        player.newPlay();

    }

    public void handleTestPlay() {

        if (!player.shouldResume()) {
            testPlay();
        } else {
            player.resume();
        }
    }


    public void testResume() {
        player.resume();
    }

    public void testNext() {
        player.next();
    }

    public void testPrev() {
        player.prev();
    }

    @FXML
    public void interrupt() throws InterruptedException {
        musicPlayerThread.interrupt();
    }

    @FXML
    public void play(File file) {
        final Task task = new Task() {
            @Override
            protected Object call()  throws Exception {
                FilePlayer.getInstance().playtest(trackListView.getSelectionModel().getSelectedItem().getFile());
                return null;
            }
        };
        System.out.println("play pressed");
        musicPlayerThread = new Thread(task);
        musicPlayerThread.start();

    }


    @FXML
    public void changeToAlbumView() {
        try {
            Parent albumViewParent = FXMLLoader.load(getClass().getResource("albumView.fxml"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
