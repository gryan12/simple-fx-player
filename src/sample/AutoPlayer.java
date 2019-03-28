package sample;

import javafx.concurrent.Task;
import sample.trackClasses.Duration;
import sample.trackClasses.Track;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class AutoPlayer  {

    private static boolean onRepeat = true;
    private static boolean onShuffle = false;
    private static List<Track> toPlay;
    private static int currentIndex = 0;
    private static boolean stop = false;
    private static Thread autoPlayerThread;
    private static corePlayer player = new corePlayer();
    private static boolean resuming = false;
    private static Task<?> lastTask = null;
    private static MyChangeListener listener;





//========================cancel any player-related task that is active when another one is called==========
    private static void runTask(Task<?> task) {
        registerTask(task);
        new Thread(task).start();
    }

    private static synchronized void registerTask(Task<?> task) {
        if (lastTask != null) {
            lastTask.cancel(true);
        }
        lastTask = task;
    }

    //================= generate player-related tasks========================
    private static Task<?> generatePlayTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.play(toPlay.get(currentIndex).getFile());
//                currentIndex++;
//                if (player.startNext && currentIndex < (toPlay.size() - 1) && player.clip.getFramePosition() == player.clip.getFrameLength()) {
//                    next();
//                }
                return null;
            }
        };
        return newTask;
    }

    private static Task<?> generateNewPlayTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                player.play(toPlay.get(currentIndex).getFile());
//                currentIndex++;
//                if (player.startNext && currentIndex < (toPlay.size() - 1) && player.clip.getFramePosition() == player.clip.getFrameLength()) {
//                    next();
//                }
                return null;
            }
        };
        return newTask;
    }
    private static Task<?> generatePauseTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                return null;
            }
        };
        return newTask;
    }


    private static Task<?> generateNextTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                currentIndex++;
                updateListener();
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    private static Task<?> generatePrevTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                currentIndex--;
                updateListener();
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    //=============the controller commands ==================

    public static void play() {

        runTask(generatePlayTask());
    }

    public static void newPlay() {
        if (player.currentFile == null) {
            runTask(generatePlayTask());
        } else {
            runTask(generateNewPlayTask());
        }
    }
    public static void resume() {
        if (player.isPlaying) {
            System.out.println("already playing");
            return;
        }
        runTask(generatePlayTask());
    }
    public static void pause() {
        if (player.isPaused) {
            System.out.println("already paused");
            return;
        }
        runTask(generatePauseTask());

    }

    public static void next() {
        if (currentIndex == toPlay.size()-1) {
            System.out.println("already at end of list");
            return;
        }
        runTask(generateNextTask());

    }
    public static void prev() {
        if (currentIndex == 0) {
            System.out.println("already at beginning of list");
            return;
        }
        runTask(generatePrevTask());
    }

//================================================================================================================
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
    public void setClipPosition(int x) {
        player.clip.setFramePosition(x);
    }


    public MyChangeListener getListener() {
        return listener;
    }

    public void setListener(MyChangeListener listener) {
        this.listener = listener;
    }

    //listener function that implements autoplay feature.
    private static void listenForAutoPlay(Clip clip) {
        clip.addLineListener(event -> {
            if (event.getType() == LineEvent.Type.STOP && clip.getFramePosition() == clip.getFrameLength()) {
                if (currentIndex < toPlay.size()-1) {
                    next();
                }
            }
        });
    }


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
                listenForFileEnd(clip);
                isPlaying = true;
                isPaused = false;
                clip.start();
                waitForSoundEnd();
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
                    listenForAutoPlay(clip);
                    listenForFileEnd(clip);
                    clip.open(audioIn);
                    System.out.println("prestart");
                    clip.start();
                    waitForSoundEnd();
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

        private void flush() {
            currentFile = null;
            clip = null;
            audioIn = null;
            isPaused = false;
            isPlaying = false;
        }

        private void pause() {
            isPaused = true;
            isPlaying = false;
            clip.stop();
        }

        private void breakOut() {
            instance = new corePlayer();
        }

        private void resume() {
            isPaused = false;
            isPlaying = true;
            clip.start();
        }

        private void listenForFileEnd(Clip clip) {
            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP){
                    waitOnBarrier();
                }
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



    }

    public interface MyChangeListener {
        public void onChangeHappened();
    }



}
