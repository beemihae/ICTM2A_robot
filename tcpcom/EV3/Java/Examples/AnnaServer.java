// AnnaServer.java

import lejos.hardware.lcd.LCD;
import lejos.hardware.Sound;
import lejos.hardware.Button;
import ch.aplu.tcpcom.*;

public class AnnaServer implements TCPServerListener
{
  private TCPServer server;
  private boolean isWaiting = true;

  public AnnaServer()
  {
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    LCD.drawString("ANNA Server", 0, 1);
    Sound.beepSequenceUp();
    while (true)
    { 
      if (Button.getButtons() != 0)
        break;
      while (isWaiting && server.isConnected())
        continue;
      if (!server.isConnected())
        continue;
      LCD.clear(4);
      LCD.drawString("Talking now..", 0, 4);
      speak(1000);
      if (!server.isConnected())
        continue;
      LCD.clear(4);
      LCD.drawString("Have an ear..", 0, 4);
      isWaiting = true;
      delay(1000);  
      server.sendMessage("done");
    }  
    Sound.beepSequence();
    server.terminate();
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
    else if (state.equals(TCPServer.LISTENING))
    {
      LCD.clear(4);
      LCD.clear(2);
      LCD.drawString("No partner", 0, 2);
      isWaiting = true;
    }
    else if (state.equals(TCPServer.CONNECTED))
    {
      LCD.drawString("Partner present", 0, 2);
      LCD.drawString("Talking now..", 0, 4);
    }
  }

    
  public static void main(String[] args)
  {
    new AnnaServer();
  }
}
