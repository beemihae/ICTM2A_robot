// BobBasic.java

import lejos.hardware.Sound;
import ch.aplu.tcpcom.*;

public class BobBasic implements TCPClientListener
{
  private TCPClient client;
  private boolean isWaiting = true;

  public BobBasic()
  {
    String host = "192.168.0.17"; // ANNA
    int port = 5000;
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    Sound.beepSequenceUp();
    client.connect();
    client.sendMessage("done");
    while (true)
    {
      while (isWaiting)
         delay(100);
      speak(500);
      isWaiting = true;
      delay(1000);
      client.sendMessage("done");
    }
  }

  private void speak(int freq)
  {
    int n = 1 + (int)(5 * Math.random());
    for (int i = 0; i < n; i++)
    {
      Sound.playTone(freq, 100);
      delay(50);
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
    if (state.equals(TCPClient.MESSAGE))
      isWaiting = false;
  }

  public static void main(String[] args)
  {
    new BobBasic();
  }
}
