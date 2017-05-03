// AnnaBasic.java

import lejos.hardware.Sound;
import ch.aplu.tcpcom.*;

public class AnnaBasic implements TCPServerListener
{
  private TCPServer server;
  private boolean isWaiting = true;

  public AnnaBasic()
  {
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    Sound.beepSequenceUp();
    while (true)
    { 
      while (isWaiting)
        continue;
      speak(1000);
      isWaiting = true;
      delay(1000);  
      server.sendMessage("done");
    }  
  }
  
  private void speak(int freq)
  {
    int n = 1 + (int)(5 * Math.random());
      for (int i = 0; i < n; i++)
      {
        Sound.playTone(freq, 500);
        delay(100);
      }
  }
  
  private void delay(int duration)
  {
    try
    {
      Thread.sleep(duration);
    }  
    catch (InterruptedException ex)
    {
    }
  }
  
  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.MESSAGE))
       isWaiting = false;
  }

    
  public static void main(String[] args)
  {
    new AnnaBasic();
  }
}
