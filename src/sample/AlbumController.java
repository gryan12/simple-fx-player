package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import sample.dialog.DialogController;
import sample.trackClasses.Album;
import sample.trackClasses.Duration;
import sample.FileManager;
import sample.trackClasses.Track;
import unused.FilePlayer;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class AlbumController {

    private Scene trackScene;
    private AutoPlayer player = AutoPlayer.getInstance();
    private TrackController trackController;
    private FileManager manager;
    private Parent controllerRoot;

    @FXML
    private ListView<Album> albumListView;
    @FXML
    private BorderPane mainPane;
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
    private Label albumViewLabel;
    @FXML
    private ContextMenu albumListContextMenu;
    @FXML
    private ImageView imageID;
    @FXML
    private AnchorPane imagePane;
    @FXML
    private Label currentTrackLabel;


    public void initialize() {

    }

    public AlbumController() {
        manager = new FileManager();
    }

    public void loadPlayerControlls() {
        Parent root = trackController.getControllerRoot();
        mainPane.setBottom(root);
    }

    public void loadControlls(Parent root) {
       mainPane.setBottom(root);
    }

    public Parent getControllerRoot() {
        return controllerRoot;
    }

    public AutoPlayer getPlayer() {
        return player;
    }

    public void setPlayer(AutoPlayer player) {
        this.player = player;

    }
    public void setControllerRoot(Parent controllerRoot) {
        this.controllerRoot = controllerRoot;
    }


    public void setCurrentTrackLabel(Track track) {
        this.currentTrackLabel.setText(track.getTitle());
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
        DataStore.getInstance().setCurrent(DataStore.getInstance().getTracks());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        trackController.setViewLabel("All Loaded Tracks");
        System.out.println("size current: " + DataStore.getInstance().getCurrentTracks().size());
        System.out.println("\n size total: " + DataStore.getInstance().getTracks().size());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
        trackController.loadControlls(controllerRoot);
    }

    //trackview with that album's tracks
    @FXML
    public void openAlbum(Album album) {

        DataStore.getInstance().setCurrent(album.getTrackList());
        trackController.setViewTracks(DataStore.getInstance().getCurrentTracks());
        trackController.setViewLabel(album.getName() + " : " + album.getArtist());
        Stage primaryStage = (Stage)((Node)mainPane).getScene().getWindow();
        primaryStage.setScene(trackScene);
        trackController.loadControlls(controllerRoot);
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
        MenuItem editMenuItem = new MenuItem("Edit Album Details");
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
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Album album = albumListView.getSelectionModel().getSelectedItem();
                if (album != null) {
                    editAlbumDialog(album);
                } else {
                    return;
                }
            }
        });

        albumListContextMenu.getItems().addAll(albumMenuItem, editMenuItem);

        albumListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>() {
            @Override
            public void changed(ObservableValue<? extends Album> observable, Album oldValue, Album newValue) {
                if (newValue != null) {
                    Album album = albumListView.getSelectionModel().getSelectedItem();
                    rightDetailsArea.setText("Artist: " + album.getArtist()
                            + "\n" + "Album name: " + album.getName());
                    if (album.getAlbumArtwork() != null) {
                        loadAlbumArtwork(album);
                    }
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


//    public void loadAlbumArtwork(Album album) {
//        System.out.println("this is being passed: " + album.getAlbumArtwork().getName());
////        Image img = new Image("/yunosuke-pathos/cover.jpg");
//        tesdddddd = new ImageView(getClass().getResource("/yunosuke-pathos/cover.jpg").toExternalForm());
//    }

    public void loadAlbumArtwork(Album album) {

        if (album.getAlbumArtwork() == null) {
            imageID.setImage(null);
        } else {
            BufferedImage bufferedImage;

            try {
                bufferedImage = ImageIO.read(album.getAlbumArtwork());
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.imageID.setImage(image);
                this.imageID.fitWidthProperty().bind(imagePane.widthProperty());
                this.imageID.setPreserveRatio(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void editAlbumDialog(Album album) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Edit An Album");
        dialog.setHeight(800);
        dialog.setWidth(800);
        DialogController controller = null;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("dialog/editDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(loader.load());
            controller = loader.getController();
            controller.setDefaults(album);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            controller.processFields(album);
        } else {
            return;
        }
        albumListView.refresh();

//        albumListView = new ListView<>(DataStore.getInstance().getAlbums());
    }
}
