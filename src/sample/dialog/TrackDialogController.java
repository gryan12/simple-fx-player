package sample.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.trackClasses.Album;
import sample.trackClasses.Track;

public class TrackDialogController {

    @FXML
    private TextField trackNameField;
//    @FXML
//    private TextField assignedAlbumField;


    public void processFields(Track track) {
       track.setTitle(trackNameField.getText());
    }

    public void setDefaults(Track track ) {
        trackNameField.setText(track.getTitle());
//        if (track.getAlbum() != null) {
//            assignedAlbumField.setText(track.getAlbum().getName());
//        }
    }
}
