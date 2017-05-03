// PearlClient.java

import ch.aplu.jgamegrid.*;
import ch.aplu.tcpcom.*;

public class PearlClient extends GameGrid
  implements TCPClientListener, GGMouseListener
{
  private TCPClient client;

  public PearlClient()
  {
    super(8, 6, 70, false);
    addStatusBar(30);
    addMouseListener(this, GGMouse.lPress | GGMouse.lRelease);
    PearlShare.initGame(this);
    show();
    int port = 5000;
    String host = "localhost";
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    client.connect();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPClient.CONNECTED))
    {
      setStatusText("Connection established. Remove any number of pearls from same row and click OK!");
      PearlShare.isMyMove = true; // You start!
    }
    else if (state.equals(TCPClient.CONNECTION_FAILED))
      setStatusText("Connection failed");
    else if (state.equals(TCPClient.DISCONNECTED))
    {
      setStatusText("Partner lost");
      PearlShare.isMyMove = false;
    }
    else if (state.equals(TCPClient.MESSAGE))
      PearlShare.handleMessage(this, msg);
  }

  public boolean mouseEvent(GGMouse mouse)
  {
    if (mouse.getEvent() == GGMouse.lPress)
      PearlShare.handleMousePress(this, client, mouse);
    if (mouse.getEvent() == GGMouse.lRelease)
      PearlShare.handleMouseRelease(this, client, mouse);
    return true;
  }

  public static void main(String[] args)
  {
    new PearlClient();
  }
}
