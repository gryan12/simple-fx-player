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

    private boolean onRepeat = true;
    private boolean onShuffle = false;
    private List<Track> toPlay;
    private int currentIndex = 0;
    private boolean stop = false;
    private static Thread autoPlayerThread;
    private static corePlayer player = new corePlayer();
    private boolean resuming = false;
    private Task<?> lastTask = null;

    private void runTask(Task<?> task) {
        registerTask(task);
        new Thread(task).start();
    }

    private synchronized void registerTask(Task<?> task) {
        if (lastTask != null) {
            lastTask.cancel(true);
        }
        lastTask = task;
    }

    private Task<?> generatePlayTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.play(toPlay.get(currentIndex).getFile());
                return null;
            }
        };
        return newTask;
    }

    private Task<?> generateNewPlayTask() {
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
    private Task<?> generatePauseTask() {
        Task newTask = new Task() {
            @Override
            protected Object call() throws Exception {
                player.pause();
                return null;
            }
        };
        return newTask;
    }



    private Task<?> generateNextTask() {
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

    private Task<?> generatePrevTask() {
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

    public void next() {
        if (currentIndex == toPlay.size()-1) {
            System.out.println("already at end of list");
            return;
        }
        runTask(generateNextTask());
    }

    public void play() {
        runTask(generatePlayTask());
    }

    public void newPlay() {
        if (player.currentFile == null) {
            runTask(generatePlayTask());
        } else {
            runTask(generateNewPlayTask());
        }
    }
    public void resume() {
        if (player.isPlaying) {
            System.out.println("already playing");
            return;
        }
        runTask(generatePlayTask());
    }
    public void pause() {
        if (player.isPaused) {
            System.out.println("already paused");
            return;
        }
        runTask(generatePauseTask());
    }

    public void prev() {
        if (currentIndex == 0) {
            System.out.println("already at beginning of list");
            return;
        }
        runTask(generatePrevTask());
    }

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




    private static class corePlayer {
        private static corePlayer instance = new corePlayer();
        private static boolean isPlaying;
        private static File currentFile;
        private static int pausedPos;
        private static boolean isPaused;
        private final CyclicBarrier barrier = new CyclicBarrier(2);
        private static Clip clip;
        private static AudioInputStream audioIn;


        private void printStatus() {
            System.out.println("is playing: " + isPlaying
                + "\ncurrent file: " + currentFile.getName().toString()
                + "\npaused position: " + pausedPos
                + "\nis paused: " + isPaused
                + "\nbarrier status@: " + barrier.getNumberWaiting()
                + "\nclip status: " + clip.isActive()
                + "\nclip position: " + clip.getFramePosition()
                + "\nstream status: " + audioIn.getClass().toString());
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





    }








}
