// EchoServer.java

package ch.aplu.echoserver;

import ch.aplu.android.*;
import ch.aplu.tcpcom.*;

public class EchoServer extends GameGrid implements TCPServerListener
{
  private TCPServer server;
  private GGConsole c;

  public void main()
  {
    int port = 5000;
    c = GGConsole.init();
    c.println("Echo server started");
    server = new TCPServer(port);
    server.addTCPServerListener(this);
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.LISTENING))
      c.println("Waiting for a client...");
    else if (state.equals(TCPServer.MESSAGE))
    {
      c.println("Received msg: " + msg + " - echoing it...");
      server.sendMessage(msg);
    }
  }
}
