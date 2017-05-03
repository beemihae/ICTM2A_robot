// TrainServer.java

import ch.aplu.ev3.*;
import ch.aplu.tcpcom.*;

public class TrainServer implements TCPServerListener
{
  private TCPServer server;
  private LegoRobot robot;
  private Gear gear;
  private boolean isWaiting = true;
  private long startTime;

  public TrainServer()
  {
    robot = new LegoRobot();
    gear = new Gear();
    robot.addPart(gear);
    gear.setSpeed(25);
    LightSensor lsBottom = new LightSensor(SensorPort.S3);
    robot.addPart(lsBottom);
    LightSensor lsFront = new LightSensor(SensorPort.S1);
    robot.addPart(lsFront);
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    robot.drawString("Train Server", 0, 1);
    startTime = System.nanoTime();
    while (!robot.isEscapeHit())
    {
      if (isWaiting)
         gear.stop();
      int vB = lsBottom.getValue();
      int vF = lsFront.getValue();
      if (vB < 500)
        gear.leftArc(0.10);
      else
        gear.rightArc(0.10);
      if (!isWaiting && vF > 500 && System.nanoTime() - startTime > 5000000000L)
      {
        server.sendMessage("go");
        robot.drawString("Stopped", 0, 2);
        isWaiting = true;
      }
    }
    server.terminate();
    robot.exit();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.LISTENING))
    {
      robot.drawString("Stopped", 0, 2);
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
    new TrainServer();
  }
}
