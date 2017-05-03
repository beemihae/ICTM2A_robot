// EV3LightClient.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;
import java.awt.Color;

public class EV3LightClient implements TCPClientListener
{
  private GPanel gp;
  private double t = 0;

  public EV3LightClient()
  {
    gp = new GPanel(0, 10, 0, 1000);
    drawGrid();
    String host = "192.168.0.17";
    int port = 5000;
    TCPClient client = new TCPClient(host, port);
    boolean success = client.connect();
    if (!success)
    {
      gp.title("Connection failed");
      return;
    }
    client.addTCPClientListener(this);
    gp.title("Connection established");
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPClient.MESSAGE))
    {
      int v = Integer.parseInt(msg);
      System.out.println("v = " + v);
      if (t == 0)
        gp.pos(0, v);
      else
        gp.draw(t, v);
      t += 0.1;
      if (t > 10)
      {
        t = 0;
        gp.clear();
        drawGrid();
      }
    }
    else if (state.equals(TCPClient.DISCONNECTED))
      gp.title("Disconnected");
  }

  private void drawGrid()
  {
    gp.color(Color.lightGray);
    for (int t = 0; t <= 10; t += 1)
      gp.line(t, 0, t, 1000);
    for (int y = 0; y <= 1000; y += 100)
      gp.line(0, y, 10, y);
    gp.color(Color.black);
  }

  public static void main(String[] args)
  {
    new EV3LightClient();
  }
}
