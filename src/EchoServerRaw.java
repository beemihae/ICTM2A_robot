package GeneralPackage;

import ch.aplu.tcpcom.*;
import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;

public class EchoServerRaw implements TCPServerListener
{
  private TCPServer server;
  
  public EchoServerRaw()
  {
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    LCD.drawString("Start Echo Server", 0, 1);
    Sound.beepSequenceUp();
    Button.waitForAnyPress();
    Sound.beepSequence();
    server.terminate();
  }
  
  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.LISTENING))
    {
      LCD.clear(2);
      LCD.drawString("Listening", 0, 2);
    }
    else if (state.equals(TCPServer.CONNECTED))
    {
      LCD.clear(2);
      LCD.drawString("Connected", 0, 2);
    }
    else if (state.equals(TCPServer.MESSAGE))
    {
      LCD.clear(4);
      LCD.drawString(msg, 0, 4);
      server.sendMessage(msg);
    }
  }

    
  public static void main(String[] args)
  {
	  LCD.drawString("Listening", 0, 2);
    new EchoServerRaw();
    
  }
}