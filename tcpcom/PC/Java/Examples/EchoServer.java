// EchoServer.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class EchoServer implements TCPServerListener
{
  private TCPServer server;
  private ModelessOptionPane mop;

  public EchoServer()
  {
    int port = 5000;
    mop = new ModelessOptionPane("");
    mop.setTitle("Echo Server");
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    while (mop.isVisible())
      Thread.yield();
    server.terminate();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.MESSAGE))
    {
      mop.setText("Received msg: " + msg + " - echoing it...");
      server.sendMessage(msg);
    }
    if (state.equals(TCPServer.LISTENING))
    {
      mop.setText("Waiting for a client...");
    }
  }

  public static void main(String[] args)
  {
    new EchoServer();
  }
}
