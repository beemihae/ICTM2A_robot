// TrainClient.java

import ch.aplu.ev3.*;
import ch.aplu.tcpcom.*;

public class TrainClient implements TCPClientListener
{
  private TCPClient client;
  private LegoRobot robot;
  private Gear gear;
  private boolean isWaiting = false;
  private long startTime;

  public TrainClient()
  {
    robot = new LegoRobot();
    gear = new Gear();
    robot.addPart(gear);
    gear.setSpeed(25);
    LightSensor lsBottom = new LightSensor(SensorPort.S3);
    robot.addPart(lsBottom);
    LightSensor lsFront = new LightSensor(SensorPort.S1);
    robot.addPart(lsFront);
    String host = "192.168.0.17";
    int port = 5000;
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    robot.drawString("Train Client", 0, 1);
    robot.drawString("Trying to connect..", 0, 2);
    boolean success = client.connect();
    if (!success)
    {  
      robot.drawString("Connection failed", 0, 3);
      while (!robot.isEscapeHit())
        continue;
      robot.exit();
      return;
    }
    startTime = System.nanoTime();
    while (!robot.isEscapeHit())
    {
      if (isWaiting)
        gear.stop();
      int vB = lsBottom.getValue();
      int vF = lsFront.getValue();
      if (vB < 500)
        gear.leftArc(0.1);
      else
        gear.rightArc(0.1);
      if (!isWaiting && vF > 500 && System.nanoTime() - startTime > 5000000000L)
      {
        client.sendMessage("go");
        robot.drawString("Stopped", 0, 2);
        isWaiting = true;
      }
    }
    client.disconnect();
    robot.exit();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.CONNECTED))
    {  
      robot.playTone(1000, 100);
      robot.drawString("Connected", 0, 2);
    }
    else if (state.equals(TCPClient.DISCONNECTED))
    {  
      robot.playTone(500, 100);
      robot.drawString("Disconnected", 0, 2);
      isWaiting = true;
    }
    else if (state.equals(TCPServer.MESSAGE))
    {
      isWaiting = false;
      startTime = System.nanoTime();
      robot.drawString("Running", 0, 2);
    }
  }

  public static void main(String[] args)
  {
    new TrainClient();
  }
}
