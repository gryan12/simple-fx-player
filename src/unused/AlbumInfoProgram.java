
/*
Class: AlbumInfoProgram

Author: George Ryan

Date: 15/11/2018

 */
import sample.trackClasses.*;

import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.Scanner;
//package
//public class AlbumInfoProgram {
//
//    public static void main(String[] args) {
//
//        AlbumCollection mainCollection = new AlbumCollection();
//        Playlist mainPlaylist = new Playlist(mainCollection, new ArrayList<>());
//
//        //TASK ONE, READ IN ALBUM COLLECTION FROM ALBUMS.TXT
//        System.out.println(" TASK ONE: INPUT ALBUM COLLECTION FROM ALBUMS.TXT"
//                + "FILE: NO OUTPUT REQUIRED");
//
//        File albumFile = new File("albums.txt");
//        String[] splitString;
//
//        try {
//            BufferedReader albumReader
//
//                    = new BufferedReader(new FileReader(albumFile));
//
//            String line;
//
//            while ((line = albumReader.readLine()) != null) {
//                /* album file has the format 'Artist Name : Album Title,
//                so split on ':'  */
//                if (!line.contains(" - ")) {
//                    Album newAlbum;
//                    splitString = line.split(" : ");
//                    newAlbum = new Album(splitString[0], splitString[1]);
//                    mainCollection.addToAlbumList(newAlbum);
//                }
//                /*otherwise Tracks have the format
//                'Track Duration - Track Title' , so split on ' - ' */
//                else {
//                    Track newTrack;
//                    PlaylistTrack newListTrack;
//                    splitString = line.split(" - ");
//                    newTrack = new Track(splitString[1],
//                            new Duration(splitString[0]));
//                    /*Assigning the last album that was read into mainCollection
//                    as the album for the tracks(of class PlaylistTrack) */
//                    Album lastAlbum = (mainCollection.getLastAlbum());
//                    newListTrack = new PlaylistTrack(lastAlbum, newTrack);
//                    mainPlaylist.addTrack(newListTrack);
//                    lastAlbum.addToAlbum(newTrack);
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error: file not found");
//        }
//
//        //END OF TASK ONE
//        //TASK TWO: SHOW ALBUMS IN ALPHABETICAL ORDER
//        System.out.println("\n TASK TWO: PRINT ENTIRE ALBUM COLLECTION IN "
//                + "ALPHABETICAL ORDER OF ARTIST FOLLOWED BY NAME");
//
//        /*loop within loop to sort alphabetically.
//        2nd loop checks name of the previous album in the album list
//        and swaps them around if first letter is earlier in alphabet*/
//        Album tempAlbum;
//        ArrayList<Album> displayAlbumList = mainCollection.getAlbumList();
//        for (int i = 1; i < displayAlbumList.size(); i++) {
//            for (int k = 1; k < displayAlbumList.size(); k++) {
//                if (displayAlbumList.get(k - 1).getArtist().
//                        compareTo((displayAlbumList.get(k).getArtist())) > 0) {
//                    mainCollection.swapPositions(displayAlbumList, k);
//                }
//
//                /* if the artist of two adjacent albums int the list is the same,
//                then swap them around based ont he alphabetical order of
//                the album title*/
//                if (displayAlbumList.get(k - 1).getArtist().
//                        compareTo((displayAlbumList.get(k).getArtist())) == 0){
//                    if (displayAlbumList.get(k - 1).getName().
//                            compareTo((displayAlbumList.get(k).getName())) > 0){
//                        mainCollection.swapPositions(displayAlbumList, k);
//                    }
//                }
//            }
//        }
//        /*
//        now just looping through the ordered arraylist of albums and printing
//        them and their trakcks in the format
//        'Artist: Album'
//        'Track no: Title (Duration (mm:ss))
//        */
//        for (int i = 1; i < displayAlbumList.size(); i++) {
//            System.out.println(("\n" + displayAlbumList.get(i).getArtist())
//                    + " : " + displayAlbumList.get(i).getName());
//            for (int j = 0; j < displayAlbumList.get(i).
//                    getTrackList().size(); j++) {
//                System.out.println((j + 1) + ": " + (displayAlbumList.get(i).
//                        getTrackList().get(j).getTitle())
//                        + " (" + displayAlbumList.get(i).getTrackList().get(j).
//                                getDuration().toMinuteString() + ")");
//            }
//        }
//        //END OF TASK TWO
//
//        //TASK 3: DISPLAY TOTAL TIME FOR ALL PINK FLOYD ALBUMS
//        System.out.println("\n TASK THREE: DISPLAY TOTAL TIME FOR ALL PINK "
//                + "FLOYD ALBUMS");
//
//        ArrayList<PlaylistTrack> mainTrackList
//                = mainPlaylist.getPlaylistTrackList();
//
//        Duration floydDuration = new Duration(0, 0, 0);
//        //just looping through tracks in the playlist, if the album the tracks
//        //are in is pink floyd, then add the duration to the total duration
//        //of pink floyd tracks
//        for (int i = 0; i < mainTrackList.size(); i++) {
//            if (mainTrackList.get(i).getTrackAlbum().getArtist().
//                    compareTo("Pink Floyd") == 0) {
//                Duration tempDuration = mainTrackList.get(i).getDuration();
//
//                floydDuration = floydDuration.
//                        addDuration(tempDuration);
//            }
//        }
//        System.out.println("Answer: the total durtaion is:" + floydDuration);
//
//        //END OF TASK THREE
//        //TASK FOUR: DISPLAY THE ALBUM WITH THE LARGEST NUMBER OF TRACKS
//        System.out.println("\n TASK FOUR: "
//                + "DISPLAY THE ALBUM WITH THE LARGEST NUMBER OF TRACKS:");
//
//        int trackCount = 0;
//        int currentBiggest = 0;
//        String biggestAlbum = "default";
//        String currentAlbum = "default";
//        String currentArtist = "default";
//        String biggestArtist = "default";
//
//
//        /*  for all the albums in the album collection, loop through and count
//        every track; if the track count for an album is greater
//        than the current highest-recorded, then it replaces it   */
//        for (int i = 0; i < mainCollection.getAlbumList().size(); i++) {
//            for (int k = 0; k < mainCollection.getAlbumList().get(i).
//                    getTrackList().size(); k++) {
//               currentAlbum = mainCollection.getAlbumList().get(i).getName();
//               currentArtist = mainCollection.getAlbumList().get(i).getArtist();
//               trackCount++;
//            }
//            if (trackCount > currentBiggest) {
//                currentBiggest = trackCount;
//                biggestAlbum = currentAlbum;
//                biggestArtist = currentArtist;
//            }
//            trackCount = 0;
//        }
//
//        System.out.println(
//                "The largest album is \""
//                + biggestAlbum + "\", by"
//                + " the Artist \"" + biggestArtist + "\", "
//                + "with a track number of: " + currentBiggest);
//
//        //END OF TASK FOUR
//        /*TASK FIVE: DISPLAY THE DEATAILS OF
//       THE LONGEST TRACK IN THE ALBUM COLLECIToN
//         */
//        System.out.println("\n TASK FIVE: DISPLAY THE DETAILS OF THE LONGEST "
//                + "TRACK IN THE ALBUM COLLECTION");
//
//        PlaylistTrack tempTrack;
//        PlaylistTrack longestTrack = new PlaylistTrack();
//        int tempTime;
//        int longestTime = 0;
//        /* Just looping through all tracks in playlist and getting
//        their duration; if duration is higher than the current longest duration,
//        then it replaces it */
//        for (int i = 0; i < mainPlaylist.getPlaylistTrackList().size(); i++) {
//            tempTrack = mainPlaylist.getPlaylistTrackList().get(i);
//            tempTime = tempTrack.getDuration().getTotalSeconds();
//
//            if (tempTime > longestTime) {
//                longestTime = tempTime;
//                longestTrack = tempTrack;
//            }
//        }
//
//        System.out.println(""
//                + "The track with the longest duration is: \""
//                + longestTrack.getTitle()
//                + "\" by \"" + longestTrack.getTrackAlbum().getArtist()
//                + "\" with a duration of : " + longestTrack.getDuration());
//
//        //END OF TASK FIVE
//        /*TASK SIX: DISPLAY THE TOTAL PLAY
//          TIME OF THE PLAYLIAT IN THE PLAYLIST.TXT
//         */
//        System.out.println("\n TASK SIX: TOTAL DURATION OF PLAYLIST.TXT");
//        File playListFile = new File("playlist.txt");
//        Duration totalDuration = new Duration(0, 0, 0);
//
//        try {
//            BufferedReader playListReader
//                    = new BufferedReader(new FileReader(playListFile));
//            String line;
//
//            while ((line = playListReader.readLine()) != null) {
//                  /* playlist file in format: ' Track Title (Artist, Album) so
//                    split on " ( " to isolate track title*/
//                splitString = line.split(" \\(");
//                for (int i = 0; i < mainPlaylist.getPlaylistTrackList().
//                        size(); i++) {
//                    if (mainPlaylist.getPlaylistTrackList().get(i).getTitle().
//                            compareTo(splitString[0]) == 0) {
//                        totalDuration = totalDuration.addDuration(mainPlaylist.
//                                getPlaylistTrackList().get(i).getDuration());
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Error: problem reading from file");
//        }
//
//        System.out.println("Total duration of tracks on "
//                + "specified playlist file: " + totalDuration.toString());
//        //END OF TASK SIX
//    }
//}
