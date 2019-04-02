package sample;

import com.sun.deploy.panel.IProperty;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.control.Slider;
import sample.trackClasses.Duration;
import sample.trackClasses.Track;

import javax.sound.sampled.*;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.SECONDS;


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

    public static AutoPlayer instance;

    public static AutoPlayer getInstance() {
        return instance;
    }

    public AutoPlayer() {
       instance = this;
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
            System.out.println("task interrupted");
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


   public void bindSliderToPosition(Slider slider) {
      slider.valueProperty().bindBidirectional(player.currentPosition());
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

        public DoubleProperty currentPosition() {
            return currentPos;
        }

        public Double getCurrentPos() {
            return currentPos.get();
        }

        public final void setValue(Double value) {
            this.currentPos.set(value);
        }

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
                    clip.start();
                    currentPos.setValue(clip.getFramePosition());
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
//                throw new RuntimeException(e);
//                System.out.println("heads up - broke the barrier");
            }
        }

        private void waitForSoundEnd() {
            waitOnBarrier();
        }


        int audiolength, audioposition;

        private void initiateProgressTracking() {

        }
//        private void tick() {
//            if (clip.isActive() && isPlaying) {
//                audioposition = (int)(clip.getMicrosecondPosition());
//                updateSlider();
//            } else {
//                return;
//            }
//        }
//
//        public void skip(int position) {
//            if (position < 0 || position > clip.getMicrosecondLength())
//                return;
//            clip.setMicrosecondPosition(position * 1000);
////            progress.setValue(position);
//            updateSlider();
//


        //==slider update functions

        private final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        private final DoubleProperty currentPos = new SimpleDoubleProperty(0);

        private void checkTrackProgress() {
            final Runnable checkProgress = new Runnable() {
                @Override
                public void run() {
                    double value =(clip.getFramePosition() / clip.getFrameLength())*100;
                    System.out.println(value);
                    currentPos.setValue((clip.getFramePosition() / clip.getFrameLength())*100);
                    System.out.println("chcekr seems to be running");
                }
            };
            final ScheduledFuture<?> handleProgress = service.scheduleAtFixedRate(checkProgress, 1, 1, SECONDS);
        }

        private double getPosition() {
            return clip.getMicrosecondPosition();
        }
        private double getRelativePosition() {
            double length = clip.getMicrosecondLength();
            double position = clip.getMicrosecondPosition();

            double relativePos =(((double)position *100/ (double)length));

            return relativePos;
        }
    }

    public interface MyChangeListener {
        public void onChangeHappened();
    }

    public double getClipPosition() {
        return player.getPosition();
    }

    public double getRelativePosition() {
        return player.getRelativePosition();
    }
}
