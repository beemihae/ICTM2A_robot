// TestServer.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class TestServer implements TCPServerListener
{
  private TCPServer server;
  private Console c;

  public TestServer()
  {
    c = new Console();
    c.setTitle("Test Server");
    System.out.println("Server running. Close to stop.");
    int port = 5000;
    server = new TCPServer(port, true);
    server.addTCPServerListener(this);
    while (!c.isDisposed())
      Thread.yield();
    server.terminate();
  }

  public void onStateChanged(String state, String msg)
  {
    System.out.println(state + " - " + msg);
  }

  public static void main(String[] args)
  {
    new TestServer();
  }
}
