// SoundPlayerExt.java

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

import java.util.Map;
import java.util.Properties;
import javax.sound.sampled.*;
import java.io.*;
import org.tritonus.share.sampled.TAudioFormat;
import org.tritonus.share.sampled.file.TAudioFileFormat;


/**
 * Class for playing sound files using file streaming in a separate thread.
 * Sound format supported: WAV (PCM_SIGNED), MP3 (Layer 1,2,3)<p>
 *
 * MP3 decoder from JavaLayer distributed by SourceForge.net.
 * The following jar files (or later versions) must reside in the classpath:<br>
 * - jl1.0.jar<br>
 * - mp3spi1.9.4.jar<br>
 * - tritonius_share.jar<br>
 * Download from www.javazoom.net or www.sourceforge.net.<br>
 * Download of redistribution from www.aplu.ch/mp3support.
 * With thanks to the authors.
 */
public class SoundPlayerExt extends SoundPlayer
{
  private String mp3Info = "no info available";
  private boolean isMP3 = false;

  /**
   * Construct a sound player attached to given input stream.
   * @throws IllegalArgumentException if sound resource or sound system is unavailable.
   */
  public SoundPlayerExt(InputStream is)
  {
    super(is);
  }

  /**
   * Construct a sound player attached to given File instance.
   * @throws IllegalArgumentException if sound resource is unavailable.
   */
  public SoundPlayerExt(File audioFile)
  {
    super(audioFile);
  }

  /**
   * Construct a sound player attached to given pathname (relative or fully qualified).
   * @throws IllegalArgumentException if sound resource is unavailable.
   */
  public SoundPlayerExt(String audioPathname)
  {
    super(audioPathname);
  }

  /**
   * Return string with extended MP3 file information.
   */
  public String getMP3Info()
  {
    AudioFileFormat baseFileFormat = null;
    String s = "";
    try
    {
      baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
    }
    catch (Exception ex)
    {
      s = "no MP3 info";
      return s;
    }
    AudioFormat baseFormat = baseFileFormat.getFormat();

    String[] testPropsAFF = {"duration","title","author","album","date","comment",
                             "copyright","mp3.framerate.fps","mp3.copyright","mp3.padding",
                             "mp3.original","mp3.length.bytes","mp3.frequency.hz",
                             "mp3.length.frames","mp3.mode","mp3.channels","mp3.version.mpeg",
                             "mp3.framesize.bytes","mp3.vbr.scale","mp3.version.encoding",
                             "mp3.header.pos","mp3.version.layer","mp3.crc"};
    String[] testPropsAF = {"vbr", "bitrate"};


    if (baseFileFormat instanceof TAudioFileFormat)
    {
      Map properties = ((TAudioFileFormat)baseFileFormat).properties();
      for (int i = 0; i < testPropsAFF.length; i++)
      {
        String key = testPropsAFF[i];
        String val = null;
        if (properties.get(key) != null)
          val = (properties.get(key)).toString();
        s = s + key + "='" + val + "'\n";
      }
    }
    if (baseFormat instanceof TAudioFormat)
    {

      Map properties = ((TAudioFormat)baseFormat).properties();
      for (int i = 0; i < testPropsAF.length; i++)
      {
        String key = testPropsAF[i];
        String val = null;
        if (properties.get(key) != null)
          val = (properties.get(key)).toString();
        s = s + key + "='" + val + "'\n";
      }
    }
    if (s.equals(""))
      s = "no MP3 info";
    return s;
  }

  // Override skip() in class SoundPlayer
  protected long skip(AudioInputStream in, long nbBytes)
  {
    long nbSkip;
    if (isMP3)
    {
      float frameRate = audioFormat.getFrameRate();
      int frameSize = audioFormat.getFrameSize();
      float time = (float)nbBytes / (frameRate * frameSize);

      AudioFileFormat baseFileFormat = null;
      try
      {
        baseFileFormat = AudioSystem.getAudioFileFormat(audioFile);
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
        System.exit(1);
      }

      Map properties = ((TAudioFileFormat)baseFileFormat).properties();
      String key = "duration";
      long duration = ((Long)properties.get(key)).longValue(); // in us
      key = "mp3.length.bytes";
      long length_bytes = ((Integer)properties.get(key)).intValue();
      nbSkip = (long)(1000000.0 * time / duration * length_bytes);

      // System.out.println("duration: " + duration);
      // System.out.println("length_bytes: " + length_bytes);
      // System.out.println("nbSkip: " + nbSkip);
    }
    else
      nbSkip = nbBytes;

    long SKIP_INACCURACY_SIZE = 1200;
    long totalSkipped = 0;
    long skipped = 0;
    try
    {
      while (totalSkipped < (nbSkip - SKIP_INACCURACY_SIZE))
      {
        skipped = in.skip(nbSkip - totalSkipped);
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

  // Override isFormatSupported() in class SoundPlayer
  protected boolean isFormatSupported(AudioFormat audioFormat)
  {
      isMP3 = audioFormat.getEncoding().toString().substring(0, 4).equals("MPEG");
      if (audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ||
          audioFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED ||
          audioFormat.getEncoding() == AudioFormat.Encoding.ULAW ||
          audioFormat.getEncoding() == AudioFormat.Encoding.ALAW ||
          isMP3)
        return true;
      return false;
   }

}
