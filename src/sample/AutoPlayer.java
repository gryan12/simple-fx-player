package sample;

import javafx.application.Platform;
import javafx.concurrent.Task;
import sample.trackClasses.Track;
import javax.sound.sampled.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.*;

/*
    as a learning exercise, this class had three aims: function as a singleton, play audio
    using java.sound.sampled.Clip, and use tasks to implement player functionality.
    It is thus inherently far from optimal.
    If this was going to be a real application, you'd just use the javafx MediaPlayer class.
 */

public class AutoPlayer  {

    private static boolean onRepeat = true;
    private static boolean onShuffle = false;
    private static List<Track> toPlay;
    private static int currentIndex = 0;
    private static corePlayer player = new corePlayer();
    private static boolean resuming = false;
    private static Task<?> lastTask = null;
    private static MyChangeListener listener;

    public static AutoPlayer instance;

    public static AutoPlayer getInstance() {
        return instance;
    }

    public AutoPlayer() {
       instance = this;
    }

    public boolean isPlaying() {
        if (player.isPlaying) {
            return true;
        }
        return false;
    }

//========================cancel any player-related task that is active when another one is called==========
    private static void runTask(Task<?> task) {
        registerTask(task);
        new Thread(task).start();
        updateListener();
    }

    private static synchronized void registerTask(Task<?> task) {
        if (lastTask != null) {
            lastTask.cancel(true);
        }
        lastTask = task;
    }

    //================= generate player-related tasks========================
    private static Task<?> generatePlayTask()throws BrokenBarrierException, InterruptedException {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    private static Task<?> generateNewPlayTask() throws BrokenBarrierException, InterruptedException{
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }
    private static Task<?> generatePauseTask() throws BrokenBarrierException, InterruptedException{
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                return null;
            }
        };
        return newTask;
    }


    private static Task<?> generateNextTask() throws BrokenBarrierException, InterruptedException{
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                currentIndex++;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateListener();
                    }
                });
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    private static Task<?> generatePrevTask() throws BrokenBarrierException, InterruptedException{
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                currentIndex--;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        updateListener();
                    }
                });
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    //=============the controller commands ==================

    public static void play() {
        try {
            runTask(generatePlayTask());
        } catch (BrokenBarrierException | InterruptedException e) {
        }
    }

    public static void newPlay() {
        if (player.currentFile == null) {
            try {
            runTask(generatePlayTask());
            } catch (BrokenBarrierException | InterruptedException e) {
                System.out.println("task interrupted");
            }
        } else {
            try {
                runTask(generateNewPlayTask());
            } catch (InterruptedException | BrokenBarrierException e) {
                System.out.println("task interrupted");
            }
        }
    }
    public static void resume() {
        if (player.isPlaying) {
            System.out.println("already playing");
            return;
        }
        try {

            runTask(generatePlayTask());
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("task interupted");
        }
    }
    public static void pause() {
        if (player.isPaused) {
            System.out.println("already paused");
            return;
        }
        try {

            runTask(generatePauseTask());
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("task interrupted");
        }

    }

    public static void next() {
        if (currentIndex == toPlay.size()-1) {
            System.out.println("already at end of list");
            return;
        }
        try {
            runTask(generateNextTask());
        }  catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("task interrupted");
        }

    }
    public static void prev() {
        if (currentIndex == 0) {
            System.out.println("already at beginning of list");
            return;
        }
        try {

            runTask(generatePrevTask());
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("thread interrupted");
        }
    }
//===============================================================================================================
//functions to load reference to the list of tracks to be played.
    public void populateList(List<Track> trackList) {
        if (!onShuffle) {
            int index =0;

        }
        toPlay = trackList;
    }


    public void populateList(List<Track> trackList, int index) {
        currentIndex = index;
        toPlay = trackList;
    }
   public boolean shouldResume() {
        if (toPlay == null && !player.isPlaying) {
            return false;
        }
            return true;
   }
    public static void incrementCurrent() {
            currentIndex++;
    }

    //allows for dynamically updating the controller ui with current track information
    public static void updateListener() {
            listener.onChangeHappened();
    }


//======setters and getters===============================
    public List<Track> getToPlay() {
        return toPlay;
    }

    public void setToPlay(List<Track> toPlay) {
        this.toPlay = toPlay;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }


    //for slider functionality
    public Clip getClip() {
        return player.clip;
    }


    public void setListener(MyChangeListener listener) {
        this.listener = listener;
    }


    public interface MyChangeListener {
        void onChangeHappened();
    }


    public double getRelativePosition() {
        return player.getRelativePosition();
    }
    public void changeTrackPosition(int relativePosition) {
        player.changeTrackPosition(relativePosition);
    }



   //inner class with functions to play wav files
    private static class corePlayer {
        private static corePlayer instance = new corePlayer();
        private static boolean isPlaying;
        private static File currentFile;
        private static int pausedPos;
        private static boolean isPaused;
        private final CyclicBarrier barrier = new CyclicBarrier(2);
        private static Clip clip;
        private static AudioInputStream audioIn;
        private static boolean startNext = false;



        public void play(File file) {

            if (isPaused && file == currentFile) {
//                listenForFileEnd(clip);
                isPlaying = true;
                isPaused = false;
                clip.start();
//                waitForSoundEnd();
            } else {
                if (isPaused && file != currentFile) {
                    currentFile = file;
                    isPaused = false;
                }
                try {
                    currentFile = file;
                    audioIn = AudioSystem.getAudioInputStream(currentFile);
                    isPlaying = true;
                    isPaused = false;
                    clip = AudioSystem.getClip();
//                    listenForFileEnd(clip);
                    clip.open(audioIn);
                    listenForAutoPlay(clip);
                    clip.start();
//                    waitForSoundEnd();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

       private static void listenForAutoPlay(Clip clip) {
           clip.addLineListener(event -> {
               if (event.getType() == LineEvent.Type.STOP && (((clip.getMicrosecondLength() - clip.getMicrosecondPosition())/1000000) == 0)){
                   if (currentIndex < toPlay.size()-1) {
                       next();
                   }
               }
           });
       }
        private void pause() {
            isPaused = true;
            isPlaying = false;
            clip.stop();
        }

        private void resume() {
            isPaused = false;
            isPlaying = true;
            clip.start();
        }

//        private void listenForFileEnd(Clip clip) {
//            clip.addLineListener(event -> {
//                if (event.getType() == LineEvent.Type.STOP){
//                    waitOnBarrier();
//                }
//            });
//        }

//        private void waitOnBarrier() {
//            try {
//                barrier.await();
//            }catch (InterruptedException | BrokenBarrierException e) {
////                throw new RuntimeException(e);
////                System.out.println("heads up - broke the barrier");
//            }
//        }

//        private void waitForSoundEnd() {
//            waitOnBarrier();
//        }

        //==slider update functions

        private double getPosition() {
            return clip.getMicrosecondPosition();
        }

        private double getRelativePosition() {
            double length = clip.getMicrosecondLength();
            double position = clip.getMicrosecondPosition();
            double relativePos =(((double)position *100/ (double)length));
            return relativePos;
        }
        public void changeTrackPosition(int relativePosition) {
            System.out.println("change called at relative pos: " + relativePosition);
            System.out.println("total: " + clip.getFrameLength());
            double newFramePosition = ((double)relativePosition/100) * (double)clip.getFrameLength();
            System.out.println("new frame position: " + newFramePosition);
            if (clip == null) {
                return;
            } else {
                if (clip.isActive()) {
                    clip.stop();
                    clip.setFramePosition((int)newFramePosition);
                    clip.start();
                } else if (!clip.isActive()) {
                    clip.setFramePosition(relativePosition);
                }
            }
        }
    }
}
