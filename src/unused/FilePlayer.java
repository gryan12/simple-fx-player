package unused;

import javafx.concurrent.Task;
import sample.trackClasses.Track;

import javax.annotation.processing.Filer;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.regex.Pattern;

//currently singleton class that am using as I/O model
import sample.trackClasses.*;

public class FilePlayer {
    //members =======

    private static FilePlayer instance = new FilePlayer();
    private static boolean isPlaying;
    private static File currentFile;
    private static int pausedPos;
    private static boolean isPaused;
    private final CyclicBarrier barrier = new CyclicBarrier(2);
    private static Clip clip;
    private static AudioInputStream audioIn;
    private AutoPlayer autoPlayer;



   //========


    private FilePlayer() {
        autoPlayer = new AutoPlayer();
    }

    //shjould sort out the playuer thread here rather than in the controller class i am pretty sure


// Thread.sleep(clip.getMicrosecondLength()/1000);

    public void playtest(File file) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        System.out.println("getting called?");
        if (clip != null) {
            System.out.println("HELLO WTF");
            clip.stop();
        }  if (isPaused && file.getName() == currentFile.getName()) {
            System.out.println("should be restarting song here");
            try {
                clip.start();
                isPlaying = true;
                isPaused = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                currentFile = file;
                audioIn = AudioSystem.getAudioInputStream(currentFile);
                isPlaying = true;
                isPaused = false;
                clip = AudioSystem.getClip();
                listenForFileEnd(clip);
                clip.open(audioIn);
                clip.start();
                waitForSoundEnd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        pausedPos = clip.getFramePosition();
        isPaused = true;
        isPlaying = false;
        clip.stop();
    }

    public static FilePlayer getInstance() {
        return instance;
    }


    public void addToAutoPlay(List<Track> trackList) {
        this.autoPlayer.addToAutoplay(trackList);
    }

    public void startAutoPlayer() {
        this.autoPlayer.autoPlay();
    }

    private void listenForFileEnd(Clip clip) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP) waitOnBarrier();
        });
    }

    private void waitOnBarrier() {
        try {
            barrier.await();
        }catch (InterruptedException | BrokenBarrierException e) {
          throw new RuntimeException(e);
        }
    }

    private void waitForSoundEnd() {
        waitOnBarrier();
    }



    public static boolean isIsPlaying() {
        return isPlaying;
    }

    public static File getCurrentFile() {
        return currentFile;
    }

    public static int getPausedPos() {
        return pausedPos;
    }

    public static boolean isIsPaused() {
        return isPaused;
    }

    public CyclicBarrier getBarrier() {
        return barrier;
    }

    public static Clip getClip() {
        return clip;
    }


    private class AutoPlayer {

        private HashMap<Integer, File> toPlay;
        private boolean shuffle;
        private boolean repeat;
        private int current;

        private int start;

        private void autoPlay() {
            if (toPlay == null) {
                return;
            }
            System.out.println("check1");
            if (currentFile == null) {
                currentFile = toPlay.get(1);
                start = 1;
                System.out.println("check2");

            } else  if (toPlay.containsValue(currentFile)) {
                start = findFile(currentFile) + 1;
                System.out.println("check3");
            }

            System.out.println(toPlay.get(start).toString());

//            playByPlayer(toPlay.get(start));

            System.out.println(start);
            if (toPlay.containsKey(start + 1)) {
                System.out.println(start);
                autoPlay();

            }
            }

        private void listenForTrackEnd() {
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    autoPlay();
                }
            });
        }

        private void generateAndRunAutoTask(File file) {
            final Task task = new Task() {
                @Override
                protected Object call() throws Exception {
                    playByPlayer(file);
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.start();
        }

        public int findFile(File file) {
            if (toPlay == null || toPlay.size() < 1) {
                return -1;
            }
            for (int i = 1; i < toPlay.size() -1; i++) {
                if (toPlay.get(i) == currentFile) {
                    return i;
                }
            }
            return -1;
        }


        public void playByPlayer() {
            try {
                playtest(toPlay.get(start));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        public void playByPlayer(File file) {
            try {
                playtest(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addToAutoplay(List<Track> trackList) {
            if (trackList == null) {
                return;
            } else {
                toPlay = new HashMap<>();
                for (int i = 0; i < trackList.size(); i++) {
                    toPlay.put((i+1), trackList.get(i).getFile());
                    System.out.println("in use");
                }
            }


        }


        }

    public static void main(String[] args) {

        List<Track> testList = new ArrayList<>();

        File pathos = new File("plac-yunosuke-pathos-pathos.wav");
        Track pathTrack = new Track("pathos", new Duration(366), pathos);
        File gimme = new File("plac-yunosuke-pathos-gimmeyourlove.wav");
        Track gimmeTrack = new Track("gimme", new Duration(477), gimme);
        testList.add(pathTrack); testList.add(gimmeTrack);

        instance = new FilePlayer();

        instance.addToAutoPlay(testList);
        instance.startAutoPlayer();

//        instance.autoPlayer.playByPlayer();

//        try {
//            instance.playtest(gimmeTrack.getFile());
//        }catch (Exception e) {
//            e.printStackTrace();
//        }


    }



    }






