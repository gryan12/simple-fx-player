package sample.trackClasses;

import java.io.File;

public class LocatedAlbum extends Album {

    private File constituentDirectory;


    public LocatedAlbum(File file) {
        super();
       this.constituentDirectory = file;
    }

    public File getConstituentDirectory() {
        return constituentDirectory;
    }

    public void setConstituentDirectory(File constituentDirectory) {
        this.constituentDirectory = constituentDirectory;
    }
}
