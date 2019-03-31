package sample.dialog;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import sample.trackClasses.Album;

public class DialogController {

    @FXML
    TextField artist;
    @FXML
    TextField albumFieldName;




    public void processFields(Album album) {
       album.setArtist(artist.getText());
       album.setName(albumFieldName.getText());
    }

    public void setDefaults(Album album) {
        artist.setText(album.getArtist());
        albumFieldName.setText(album.getName());
    }

}
