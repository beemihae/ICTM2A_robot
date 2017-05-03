// BobClient.java

import lejos.hardware.lcd.LCD;
import lejos.hardware.Sound;
import lejos.hardware.Button;
import ch.aplu.tcpcom.*;

public class BobClient implements TCPClientListener
{
  private TCPClient client;
  private boolean isWaiting = true;

  public BobClient()
  {
    String host = "192.168.0.17"; // ANNA
    int port = 5000;
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    LCD.drawString("BOB Client", 0, 1);
    Sound.beepSequenceUp();
    boolean success = client.connect();
    if (!success)
    {
      LCD.drawString("Partner not found", 0, 2);
      delay(3000);
      Sound.beepSequence();
      return;
    }
    LCD.drawString("Partner present", 0, 2);
    LCD.drawString("Have an ear..", 0, 4);
    client.sendMessage("done");  // Anna talks first
    while (true)
    {
      if (Button.getButtons() != 0)
        break;
      while (isWaiting && client.isConnected())
         delay(100);
      if (!client.isConnected())
        continue;
      LCD.clear(4);
      LCD.drawString("Talking now..", 0, 4);
      speak(500);
      if (!client.isConnected())
        continue;
      LCD.clear(4);
      LCD.drawString("Have an ear..", 0, 4);
      isWaiting = true;
      delay(1000);
      client.sendMessage("done");
    }
    Sound.beepSequence();
    client.disconnect();
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
    else if (state.equals(TCPClient.DISCONNECTED))
    {
      LCD.clear(4);
      LCD.clear(2);
      LCD.drawString("Partner lost", 0, 2);
      isWaiting = false;
    }
  }

  public static void main(String[] args)
  {
    new BobClient();
  }
}
