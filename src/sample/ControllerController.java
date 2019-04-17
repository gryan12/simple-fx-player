package sample;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import sample.trackClasses.Duration;
import sample.trackClasses.Track;

import javax.imageio.ImageIO;
import javax.lang.model.AnnotatedConstruct;
import javax.sound.sampled.Clip;
import java.applet.AudioClip;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

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
    @FXML
    private Label currentTime;
    @FXML
    private Label endTime;

    private Track currentTrack;
    private boolean isTrackingProgress = false;
    private int currentTrackPosition;
    private boolean resetSlider = false;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final DoubleProperty currentPos = new SimpleDoubleProperty(0);
    private  ScheduledFuture<?> lastFuture = null;


    public void initialize() {
        setUpSliderListener();
    }


    public void setCurrentTime(String text) {
        this.currentTime.setText(text);
    }

    public void setEndTime(String text) {
        this.endTime.setText(text);
    }

    private ScheduledFuture<?> registerFutures(ScheduledFuture<?> future) {
        if (lastFuture != null && future != lastFuture) {
            lastFuture.cancel(true);
            lastFuture = future;
        } else if (lastFuture == null){
           lastFuture = future;
        }
        return lastFuture;
    }

    private void checkTrackProgress() {
        resetSlider = false;
        final Runnable checkProgress = new Runnable() {
            @Override
            public void run() {
                    double value = AutoPlayer.getInstance().getRelativePosition();
                    slider.setValue(value);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        int value  = (int)(AutoPlayer.getInstance().getClip().getMicrosecondPosition()/1000000);
                        String current = Duration.format(value);
                        currentTime.setText(current);
//                        currentTime.setText("" + AutoPlayer.getInstance().getClip().getMicrosecondPosition()/1000000);
                    }
                });
            }
        };
        final ScheduledFuture<?> handleProgress = scheduler.scheduleAtFixedRate(checkProgress, 1, 1, SECONDS);
        registerFutures(handleProgress);
        if (resetSlider) {
            handleProgress.cancel(true);
        }

    }

    //when new task generated in the player class, update controller fields.
    @Override
    public void onChangeHappened() {
        Track track = AutoPlayer.getInstance().getToPlay().get(AutoPlayer.getInstance().getCurrentIndex());

        if (currentTrack == player.getToPlay().get(player.getCurrentIndex())) {
            return;
        }
       if ((currentTrack != null && track != currentTrack) || currentTrack == null){
           currentTrack = track;
            loadCurrentAlbumArtwork(track.getAlbum());
            setcurrentTrackLabel(player.getToPlay().get(player.getCurrentIndex()));
            setEndTime(currentTrack.getDuration().toString());
            setCurrentTime("0");
            refreshLabels(track);
            resetSlider = true;
            checkTrackProgress();
        } else if (currentTrack != null && track == currentTrack) {
       }
    }


    public ControllerController() {
        player.setListener(this);
    }

//    private void checkTrackProgress() {
//        final Runnable checkProgress = new Runnable() {
//            @Override
//            public void run() {
//                double value =(player.getClip().getFramePosition() / player.getClip().getFrameLength())*100;
//                System.out.println(value);
//                slider.setValue((player.getClip().getFramePosition() / player.getClip().getFrameLength())*100);
//                System.out.println("chcekr seems to be running");
//                isTrackingProgress = true;
//            }
//        };
//        final ScheduledFuture<?> handleProgress = service.scheduleAtFixedRate(checkProgress, 1, 1, SECONDS);
//    }

    @FXML
    public void handlePlayerControlls(ActionEvent ae) {
        if (ae.getSource() == play) {
            if (!player.shouldResume()) {
                return;
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

    /*
    onlu update the slider if incremented by a value greater than 3 (to not act upon tracking change) and less than 98
    (to not act upon auto-play changes).
     */
    public void setUpSliderListener() {
        slider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (Math.abs((double)oldValue - (double)newValue) > 3 && (Math.abs(oldValue.doubleValue() - newValue.doubleValue()) < 98)) {
                    AutoPlayer.getInstance().changeTrackPosition((newValue.intValue()));
                }
            }
        });
    }

    public void setUpSliderEvents() {
        slider.setOnMouseReleased(event -> {
            AutoPlayer.getInstance().changeTrackPosition((int)(slider.getValue()));
        });
    }

}
