// SoundPlayer.java

/*
  This software is part of the JEX (Java Exemplarisch) Utility Library.
  It is Open Source Free Software, so you may
    - run the code for any purpose
    - study how the code works and adapt it to your needs
    - integrate all or parts of the code in your own programs
    - redistribute copies of the code
    - improve the code and release your improvements to the public
  However the use of the code is entirely your responsibility.
 */

package ch.aplu.util;

import javax.sound.sampled.*;
import java.io.*;

/**
 * Class for playing sound files using file streaming in a separate thread.
 * Sound format supported: WAV (PCM_SIGNED)
 * For MP3 format, use class SoundPlayerExt.
 * @see SoundPlayerExt
 */
public class SoundPlayer
{
  private class PlayerThread extends Thread
  {
    public void run()
    {
      int frameSize = audioFormat.getFrameSize();
      byte buf[] = new byte[1000*frameSize];
      try
      {
        if (forwardStep > 0)  // if advanceXXX() was called before play()
        {
          skip(audioInputStream, forwardStep * frameSize);
          forwardStep = 0;
        }

        int cnt = 0;
        if (isRewind)
          isRewind = false;
        else
          if (soundPlayerListener != null)
            soundPlayerListener.notifySoundPlayerStateChange(0);  // Start

        while (isRewindWhilePaused || (isRunning && (cnt = audioInputStream.read(buf, 0, buf.length)) != -1))
        {
          if (isRewindWhilePaused)
          {
            isPaused = true;
          }
          else
          {
            if  (cnt > 0)
            {
              if (soundConverter != null)
              {
                // Eventually update sound conversion parameters
                soundConverter.update();

                // Feed the samples into sound converter
                soundConverter.putSamples(buf, cnt);
                // Fetch the samples from sound converter and play them
                int nbBytes = 0;
                do
                {
                  nbBytes = soundConverter.receiveSamples(buf, buf.length);
                  if (nbBytes > 0)
                    sourceDataLine.write(buf, 0, nbBytes);
                } while (nbBytes != 0);
              }
              else  // no sound converter
                 sourceDataLine.write(buf, 0, cnt);

              currentPos = currentPos + cnt / frameSize;
            }
          }
          if (isPaused)
          {
            if (!isRewindWhilePaused)
              if (soundPlayerListener != null)
                soundPlayerListener.notifySoundPlayerStateChange(2);  // Pause

            synchronized(monitor)
            {
              try
              {
               if (isRewindWhilePaused)
                 isRewindWhilePaused = false;
                monitor.wait();
                if (soundPlayerListener != null)
                  soundPlayerListener.notifySoundPlayerStateChange(1);  // Resume
              }
              catch (InterruptedException ex)
              {}
            }
          }
          if (forwardStep > 0)  // Advance
          {
            synchronized(monitor)
            {
              skip(audioInputStream, forwardStep * frameSize);
              forwardStep = 0;
            }
          }
        }
        if (soundConverter != null)
        {
          soundConverter.cleanup();
          int nbBytes = 0;
          do
          {
            nbBytes = soundConverter.receiveSamples(buf, buf.length);
            if (nbBytes > 0)
               sourceDataLine.write(buf, 0, nbBytes);
           } while (nbBytes != 0);
        }
        sourceDataLine.close();
        audioInputStream.close();
        currentPos = 0;
        forwardStep = 0;
        if (soundPlayerListener != null)
        {
          if (isRunning)
          {
            isRunning = false;
            if (!isRewind)
              if (soundPlayerListener != null)
                soundPlayerListener.notifySoundPlayerStateChange(4);  // End of resource
          }
          else
          {
            if (!isRewind)
              if (soundPlayerListener != null)
                soundPlayerListener.notifySoundPlayerStateChange(3);  // Stop
          }
        }
      }
      catch (IOException ex)
      {
        System.out.println(ex);
        System.exit(1);
      }
    }
  }

  private InputStream is = null;
  protected File audioFile = null;
  protected AudioFormat audioFormat;
  private AudioFormat audioFormatBase;
  private AudioInputStream audioInputStream;
  private SourceDataLine sourceDataLine;
  private FloatControl gainControl;
  private float volume;
  private PlayerThread playerThread =  null;
  private volatile boolean isRunning = false;
  private volatile boolean isPaused = false;
  private SoundPlayerListener soundPlayerListener = null;
  private Object monitor = new Object();
  private volatile long forwardStep = 0;
  private volatile long currentPos = 0;
  private volatile boolean isRewindWhilePaused = false;
  private volatile boolean isRewind = false;
  private SoundConverter soundConverter = null;

  /**
   * Construct a sound player attached to given input stream.
   * @throws IllegalArgumentException if sound resource or sound system is unavailable.
   */
  public SoundPlayer(InputStream is)
  {
    this.is = is;
    int rc = init(true);
    if (rc == 1)
      throw(new IllegalArgumentException("Can't open sound resource.\nAudio format not supported."));
    if (rc == 2)
      if (audioFormatBase == null)
        throw(new IllegalArgumentException("Audio format not supported"));
      else
        throw(new IllegalArgumentException("Audio format " +
                                            audioFormatBase.toString() + " not supported"));
    if (rc == -1 && audioFormat != null)
      throw(new IllegalArgumentException("Can't access sound system for audio format " +
                                          audioFormatBase.toString()));
  }

  /**
   * Construct a sound player attached to given File instance.
   * @throws IllegalArgumentException if sound resource is unavailable.
   */
  public SoundPlayer(File audioFile)
  {
    this.audioFile = audioFile;
    int rc = init(true);
    if (rc == 1)
      throw(new IllegalArgumentException("Can't open sound resource.\nAudio format not supported."));
    if (rc == 2)
      if (audioFormatBase == null)
        throw(new IllegalArgumentException("Audio format not supported"));
      else
        throw(new IllegalArgumentException("Audio format " +
                                            audioFormatBase.toString() + " not supported"));
    if (rc == -1 && audioFormat != null)
      throw(new IllegalArgumentException("Can't access sound system for audio format " +
                                          audioFormatBase.toString()));
  }

  /**
   * Construct a sound player attached to given pathname (relative or fully qualified).
   * @throws IllegalArgumentException if sound resource is unavailable.
   */
  public SoundPlayer(String audioPathname)
  {
    this(new File(audioPathname));
  }

  private int init(boolean doClose)
  {
    try
    {
      if (is != null)
        audioInputStream = AudioSystem.getAudioInputStream(is);
      else
        audioInputStream = AudioSystem.getAudioInputStream(audioFile);
      if (audioInputStream == null)
        return 1;

      audioFormatBase = audioInputStream.getFormat();
      if (!isFormatSupported(audioFormatBase))
        return 2;  // Illegal format

      // Decoded format
      audioFormat = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        audioFormatBase.getSampleRate(), 16, audioFormatBase.getChannels(),
        audioFormatBase.getChannels() * 2, audioFormatBase.getSampleRate(), false);

      // Redefine audioInputStream to decoded format
      audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);
      if (doClose && audioInputStream != null)
        audioInputStream.close();
    }
    catch (Exception ex)
    {
      return 1;
    }

    try
    {
      DataLine.Info dataLineInfo =
        new DataLine.Info(SourceDataLine.class, audioFormat);
      sourceDataLine =
        (SourceDataLine)AudioSystem.getLine(dataLineInfo);

      sourceDataLine.open(audioFormat);

      javax.sound.sampled.Control[] controls = sourceDataLine.getControls();
      for (int i = 0; i < controls.length; i++)
      {
        if (controls[i].getType() == FloatControl.Type.MASTER_GAIN)
        {
          gainControl = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
          break;
        }
        if (gainControl != null)
          volume = gainControl.getValue();
      }
    }
    catch (Exception ex)
    {
      return -1;
    }
    return 0;
  }

  /**
   * Register a SoundPlayerListener to get notifications from the SoundPlayer.
   */
  public void addSoundPlayerListener(SoundPlayerListener listener)
  {
    soundPlayerListener = listener;
  }

  /**
   * Start or resume playing and return immediately.<br>
   * When the playing is over, paused or stopped, notifySoundPlayerStateChange(int reason) is invoked.
   * Return values:<br>
   *  0: Successfully started<br>
   *  1: Successfully resumed<br>
   *  2: Playing underway. Nothing happens.
   */
  public int play()
  {
    if (isPaused)  // Resume
    {
      isPaused = false;
      synchronized(monitor)
      {
        try
        {
          monitor.notify();
        }
        catch (Exception ex) {}
      }
      return 1;
    }

    if (playerThread != null && playerThread.isAlive())
      return 2;

    // Must (re)open stream because we have to position the file pointer
    // to the beginning. (No seek-method to offset zero available.)
    init(false);
    sourceDataLine.start();
    if (gainControl != null)
      volume = gainControl.getValue();
    playerThread = new PlayerThread();
    isRunning = true;
    playerThread.start();
    return 0;
  }

  /**
   * Stop playing. If playing or pausing, play() will restart
   * playing from the beginning. Block until all resources are released.
   */
  public void stop()
  {
    if (!isRunning)
      return;
    isRunning = false;
    playerThread.interrupt();  // If paused
    try
    {
      playerThread.join();  // Wait the player thread to die
    }
    catch (InterruptedException ex) {}
    playerThread = null;
    isPaused = false;
    currentPos = 0;
  }

  /**
   * Stop playing momentarily. If not playing nothing happens.
   * play() must be called to resume playing.
   */
  public void pause()
  {
    if (isPlaying())
      isPaused = true;
  }

  /**
   * Return true, if playing (and not pausing), otherwise false.
   * (In other words, if true, you should hear something).
   */
  public boolean isPlaying()
  {
    if (playerThread == null)
      return false;
    if (isPaused)
      return false;
    return (playerThread.isAlive());
  }

  /**
   * Return the current position (in frames from beginning).
   */
  public long getCurrentPos()
  {
    return currentPos;
  }

  /**
   * Return the current time (in ms from beginning).
   */
  public double getCurrentTime()
  {
    float frameRate = audioFormat.getFrameRate();
//    currentPos = currentTime * frameRate
    return 1000.0 * currentPos / frameRate;
  }

  /**
   * Return AudioFormat of player's (decoded) resource.
   * If playing MP3 files, the  decoded format is AudioFormat.Encoding.PCM_SIGNED.
   * @see javax.sound.sampled.AudioFormat
   */
  public AudioFormat getFormat()
  {
    return audioFormat;
  }

  /**
   * Return frame size (nb of bytes per sound sample) of player's (decoded) resource.
   */
  public int getFrameSize()
  {
     return audioFormat.getFrameSize();
  }


  /**
   * Return frame rate (number of frames per seconds) of player's (decoded) resource.
   */
  public float getFrameRate()
  {
     return audioFormat.getFrameRate();
  }

  /**
   * Advance current position (number of frames). If pausing, remain pausing
   * at new position. If still stopped, advance start position.
   * If advanced past end of resource, advance to end of resource.
   * May be called before play() to start playing at given byte offset.
   */
  public void advanceFrames(long nbFrames)
  {
    if (nbFrames > 0)
    {
      synchronized(monitor)  // because player thread may change value at same time
      {
        forwardStep += nbFrames;
        currentPos += nbFrames;
      }
    }
  }

  /**
   * Advance current time (in ms). If pausing, remain pausing
   * at new time. If still stopped, advance start time.
   * If new time exceeds length of resource, advance to end of resource.
   * May be called before play() to start playing at given time offset.
   */
  public void advanceTime(double time)
  {
    if (time > 0)
      advanceFrames((long)(time / 1000.0 * audioFormat.getFrameRate()));
  }

  /**
   * Rewind current position (number of frames).
   * If pausing, remain pausing  at new position. If stopped, rewind
   * start position, if eventually advanced (never below 0).
   * If given nbBytes is greater than current position, rewind to start of resource.
   */
  public void rewindFrames(long nbFrames)
  {
    if (nbFrames > 0)
    {
      if (!isRunning)  // Still stopped, must compensate advances
      {
        forwardStep -= nbFrames;
        currentPos -= nbFrames;
        if (forwardStep < 0)
          forwardStep = 0;
        if (currentPos < 0)
          currentPos = 0;
        return;
      }
      boolean pausing = isPaused;
      long pos = currentPos;
      double currentTime = getCurrentTime() / 1000.0;
      isRewind = true;
      stop();  // Resets isPaused
      if (pausing)
        isRewindWhilePaused = true;
      forwardStep = (pos - nbFrames > 0) ? (pos - nbFrames) : 0;
      currentPos = forwardStep;
      play();
      // Must wait until thread enters wait state, otherwise in a
      // following play(), notify() may be missed
      while (isRewindWhilePaused)
      {
        try
        {
          Thread.currentThread().sleep(1);
        }
        catch (InterruptedException ex) {}
      }
    }
  }

  /**
   * Rewind current time (in ms). If stopped, nothing happens.
   * If pausing, remain pausing at new time. If stopped, rewind
   * start time, if eventually advanced (never below 0).
   * If given time is greater than current time, rewind to start of resource.
   */
  public void rewindTime(double time)
  {
    if (time > 0)
      rewindFrames((long)(time / 1000.0 * audioFormat.getFrameRate()));
  }

  /**
   * Set the volume to the give value (range 0..1000).
   * May be called in any player's state.
   * If the sound system has no support for volume control, nothing happens.
   */
  public void setVolume(int value)
  {
    if (gainControl == null)
      return;
    float gainMax = gainControl.getMaximum();
    float gainMin = gainControl.getMinimum();
    float width = gainMax - gainMin;
    int valMax = 1000;
    volume = gainMin + (float)value / valMax * width;
    gainControl.setValue(volume);
  }

  /**
   * Return the current volume (range 0..1000).
   * If the sound system has no support for volume control, return -1.
   */
  public int getVolume()
  {
    if (gainControl == null)
      return -1;
    float gainMax = gainControl.getMaximum();
    float gainMin = gainControl.getMinimum();
    float width = gainMax - gainMin;
    int valMax = 1000;
    return Math.round((volume - gainMin) * valMax / width);
  }


  /**
   * Registers a sound converter. See documentation for interface 'SoundConverter'
   * for more information.
   * @see SoundConverter
   */
  public void addSoundConverter(SoundConverter soundConverter)
  {
    this.soundConverter = soundConverter;
  }

  // Overridden in class SoundPlayerExt
  protected long skip(AudioInputStream in, long nbBytes)
  {
    long SKIP_INACCURACY_SIZE = 1200;
    long totalSkipped = 0;
    long skipped = 0;
    try
    {
      while (totalSkipped < (nbBytes - SKIP_INACCURACY_SIZE))
      {
        skipped = in.skip(nbBytes - totalSkipped);
        if (skipped == 0)
          break;
        totalSkipped = totalSkipped + skipped;
      }
    }
    catch (IOException ex)
    {
      ex.printStackTrace();
      System.exit(1);
    }
    return totalSkipped;
  }

  // Overridden in class SoundPlayerExt
  protected boolean isFormatSupported(AudioFormat audioFormat)
  {
    if (audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ||
        audioFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED ||
        audioFormat.getEncoding() == AudioFormat.Encoding.ULAW ||
        audioFormat.getEncoding() == AudioFormat.Encoding.ALAW)
      return true;
    return false;
   }
}