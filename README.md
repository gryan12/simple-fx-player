# simple-fx-player
simple music player, experimenting with javafx

This is a simple wav player that was made as a learning exercise / experiment with javafx. 

The aim of the project was to make a music player for .wav files, with three arbitrary learning objectives: 
  1. Implement own music player functionality rather than use javafx's MediaPlayer class. 
  2. Experiment with using singletons in the application. 
  3. Experiment with the pre-loading of scenes by maintaining scene's variables whilst they are not loaded onto
      the stage. 
      
  Aim 1 - Player implementation - also had several learning sub-aims: 
        1.  Handle threading of player functionality using Tasks rather than Runnables
        2. Use java.sound.sampled.Clip and related classes to play music (instead of javafx's MediaPlayer and related classes)
        
  
  The above aims do not result in optimal code (quite the opposite), as the project was intended as a learning exercise. 
  
  
  
  The location of imported music files is stored, and then read from when the call to play a track is called. In the current state,       changing of the filepath of a music track will result in it needing to be reimported. 
  
  
  
      
