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

    private static Thread musicPlayerThread;

public void initialize() {
    }

    public void setScene(Scene trackScene) {
        this.trackScene = trackScene;
    }

    @FXML
    public void openTrackScene(ActionEvent ae) {
        Stage primaryStage = (Stage)((Node)ae.getSource()).getScene().getWindow();
        primaryStage.setScene(trackScene);
    }

    public void setUpAlbumView() {

        albumListView.setItems(DataStore.getInstance().getAlbums());
        albumListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Album>() {
            @Override
            public void changed(ObservableValue<? extends Album> observable, Album oldValue, Album newValue) {
                if (newValue != null) {
                    Album album = albumListView.getSelectionModel().getSelectedItem();
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("albumView.fxml"));
                    AlbumController controller = loader.getController();
                    controller.rightDetailsArea.setText("Artist: " + album.getArtist()
                            + "\n" + "Album name: " + album.getName());
                }
            }
        });


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
//                cell.emptyProperty().addListener(
//                        (obs, wasEmpty, isNowEmpty) -> {
//                            if (isNowEmpty) {
//                                cell.setContextMenu(null);
//                            } else {
//                                cell.setContextMenu(listContextMenu);
//                            }
//                        });
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
    public void testLoad() {
        DirectoryChooser chooser = new DirectoryChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("wav files", "*.wav");
        chooser.setInitialDirectory(new File("./"));
        File file = chooser.showDialog(mainPane.getScene().getWindow());
        System.out.println(file.canRead());


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(file.toPath())) {
            for (Path streamPath : stream) {
                String[] details = streamPath.toString().split("-");
                String trackName = details[3];
                Duration duration = new Duration();
                AudioFormat format;
                try (AudioInputStream streaminput = AudioSystem.getAudioInputStream(streamPath.toFile())) {
                    format = streaminput.getFormat();
                    long size = streamPath.toFile().length();
                    int frameSize = format.getFrameSize();
                    float frameRate = format.getFrameRate();
                    float totalLength = (size / (frameSize * frameRate));
                    duration = new Duration((int) totalLength);
                    DataStore.getInstance().addTrack(new Track(trackName, duration, streamPath.toFile()));
                    for (String detail : details) {
                        System.out.println("\t" + detail);
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
