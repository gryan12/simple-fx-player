package sample.trackClasses;
/*
Class: AlbumCollection

Author: George Ryan

Date: 04/11/2018

 */
import java.util.ArrayList;

public class AlbumCollection {

    private ArrayList<Album> albumList;

    public AlbumCollection() {
        this.albumList = new ArrayList<>();
    }

    public AlbumCollection(ArrayList<Album> albumList) {
        this.albumList = albumList;
    }

    public ArrayList<Album> addToAlbumList(Album newAlbum) {
        albumList.add(newAlbum);
        return albumList;
    }

    //used for alphabetical sorting
    public void swapPositions(ArrayList<Album> albumList, int k) {
        Album tempAlbum;
        tempAlbum = albumList.get(k - 1);
        albumList.set(k - 1, (albumList.get(k)));
        albumList.set(k, tempAlbum);
    }

    public Album getLastAlbum() {
        return albumList.get(albumList.size() - 1);
    }

    public ArrayList<Album> getAlbumList() {
        return albumList;
    }

}
