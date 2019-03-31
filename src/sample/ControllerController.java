package sample;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.trackClasses.Album;
import sample.trackClasses.Track;

import javax.imageio.ImageIO;
import javax.lang.model.AnnotatedConstruct;
import javax.sound.sampled.Clip;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ControllerController implements AutoPlayer.MyChangeListener {

    private AutoPlayer player = AutoPlayer.getInstance();
    private Track currentlyPlaying;
    private boolean sliderIsSet = false;

    @FXML
    Button play;
    @FXML
    Button pause;
    @FXML
    Button next;
    @FXML
    Button prev;

    @FXML
    BorderPane controlPane;
    @FXML
    private Label currentTrackLabel;

    @FXML
    private Label labelTwo;

    @FXML
    private  Label artistLabel;
    @FXML
    private Label albumLabel;

    @FXML
    private HBox trackLabelBox;

    @FXML
    private VBox controlBox;
    @FXML
    private ImageView currentlyPlayingAlbumArtwork;

    @FXML
    private AnchorPane imagePane;
    @FXML
    private Slider slider;

    public ControllerController() {
        player.setListener(this);
    }

    public void initialize() {
    }
    @Override
    public void onChangeHappened() {
        Track track = AutoPlayer.getInstance().getToPlay().get(AutoPlayer.getInstance().getCurrentIndex());
        System.out.println("change called");
        loadCurrentAlbumArtwork(track.getAlbum());
        setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
        refreshLabels(track);
        if (!sliderIsSet) {
            setUpSlider();
        }
    }
    @FXML
    public void handlePlayerControlls(ActionEvent ae) {
        if (ae.getSource() == play) {
            if (!player.shouldResume()) {
                return;
            } else {
                player.resume();
//                setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
            }
        } else if (ae.getSource() == pause) {
            System.out.println("pause");
            System.out.println("THIS IS IN THE CONTROLLERCONTROLLER");
            player.pause();
        } else if (ae.getSource() == next) {
            System.out.println("next");
            player.next();
//            setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()+1));
        } else if (ae.getSource() == prev) {
            System.out.println("prev");
            player.prev();
//            setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()-1));
        }
    }



    //PLACEHOLDER ======== NEED FUNCTION IN AUTOPLAYER WHICH INFORMS CONTROLLER OF CURRENT TRACK, RATHER THAN
    //IS IMPLEMENTED IN THIS MEHTOD



    public void loadCurrentAlbumArtwork(Album album) {

        if (album.getAlbumArtwork() == null) {
            currentlyPlayingAlbumArtwork.setImage(null);
            return;
        } else {
            BufferedImage bufferedImage;
            try {
                bufferedImage = ImageIO.read(album.getAlbumArtwork());
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                this.currentlyPlayingAlbumArtwork.setImage(image);
                this.currentlyPlayingAlbumArtwork.fitWidthProperty().bind(imagePane.widthProperty());
                this.currentlyPlayingAlbumArtwork.setPreserveRatio(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setcurrentTrackLabel(Track track) {
       currentTrackLabel.setText(track.getTitle());
    }

    public void refreshLabels(Track track) {
       if (track.getAlbum() != null) {

           albumLabel.setText(track.getAlbum().getName());
           artistLabel.setText(track.getAlbum().getArtist());
       }
    }

    public void setUpSlider() {
        player.bindSliderToPosition(slider);
    }
}
