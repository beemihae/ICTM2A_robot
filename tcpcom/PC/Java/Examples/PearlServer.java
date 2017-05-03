// PearlServer.java

import ch.aplu.jgamegrid.*;
import ch.aplu.tcpcom.*;

public class PearlServer extends GameGrid
  implements TCPServerListener, GGMouseListener
{
  private TCPServer server;
  private boolean gameStarted = false;

  public PearlServer()
  {
    super(8, 6, 70, false);
    addStatusBar(30);
    addMouseListener(this, GGMouse.lPress | GGMouse.lRelease);
    PearlShare.initGame(this);
    show();
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.PORT_IN_USE))
      setStatusText("TCP port occupied. Restart IDE.");
    else if (state.equals(TCPServer.LISTENING))
    {
      if (!PearlShare.isOver && !gameStarted)
        setStatusText("Waiting for a partner to play");
      if (gameStarted)
      {
        setStatusText("Partner lost.");
        PearlShare.isMyMove = false;
      }
    }
    else if (state.equals(TCPServer.CONNECTED))
    {
      setStatusText("Client connected. Wait for partner's move!");
      gameStarted = true;
    }

    else if (state.equals(TCPServer.MESSAGE))
      PearlShare.handleMessage(this, msg);
  }

  public boolean mouseEvent(GGMouse mouse)
  {
    if (mouse.getEvent() == GGMouse.lPress)
      PearlShare.handleMousePress(this, server, mouse);
    if (mouse.getEvent() == GGMouse.lRelease)
      PearlShare.handleMouseRelease(this, server, mouse);
    return true;
  }

  public static void main(String[] args)
  {
    new PearlServer();
  }
}
