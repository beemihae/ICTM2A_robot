// TestClient.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class TestClient implements TCPClientListener
{
  private TCPClient client;
  private Console c;

  public TestClient()
  {
    c = new Console();
    c.setTitle("Test Client");
    System.out.println("Trying to connect..");
    String host = "localhost";
    int port = 5000;
    client = new TCPClient(host, port, true);
    client.addTCPClientListener(this);
    boolean success = client.connect();
    if (success)
    {
      System.out.println("Connection successful. Close to disconnect");
//      client.sendMessage("Test Client says hello to you!");
      while (!c.isDisposed())
        Thread.yield();
      client.disconnect();
    }
    else
      System.out.println("Connection failed");
  }

  public void onStateChanged(String state, String msg)
  {
    System.out.println(state + " - " + msg);
  }

  public static void main(String[] args)
  {
    new TestClient();
  }
}
