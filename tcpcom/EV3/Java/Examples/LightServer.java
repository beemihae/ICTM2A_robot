// LightServer.java

import ch.aplu.ev3.*;
import lejos.hardware.lcd.LCD;
import ch.aplu.tcpcom.*;

public class LightServer implements TCPServerListener
{
  private TCPServer server;

  public LightServer()
  {
    LegoRobot robot = new LegoRobot();
    LightSensor ls = new LightSensor(SensorPort.S1);
    robot.addPart(ls);
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    LCD.drawString("Start Light Server", 0, 1);

    while (!robot.isEscapeHit())
    {  
      int v = ls.getValue();
      server.sendMessage("" + v);
      LCD.clear(3);
      LCD.drawString("Light value: " + v, 0, 3);
      Tools.delay(300);
    }
    server.terminate();
    robot.exit();
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
  }

  public static void main(String[] args)
  {
    new LightServer();
  }
} 