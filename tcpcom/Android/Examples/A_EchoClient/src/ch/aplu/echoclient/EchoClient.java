// EchoClient.java

package ch.aplu.echoclient;

import ch.aplu.android.*;
import ch.aplu.tcpcom.*;

public class EchoClient extends GameGrid implements TCPClientListener
{
  private GGConsole c;

  public void main()
  {
    String host = askIPAddress();
    int port = 5000;
    c = GGConsole.init();
    c.println("Hello. Trying to connect " + host);
    TCPClient client = new TCPClient(host, port);
    boolean success = client.connect(10);  // Timeout 10 s

    if (!success)
    {
      c.println("Connection failed");
      return;
    }
    client.addTCPClientListener(this);
    c.println("Connection established. Sending now...");
    for (int i = 0; i < 100; i++)
    {
      client.sendMessage("" + i);
      delay(100); // slow down
    }
    c.println("Program terminated");
    client.disconnect();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPClient.MESSAGE))
      c.println("Got: " + msg);
    else if (state.equals(TCPClient.DISCONNECTED))
      c.println("Disconnected");
  }

  private String askIPAddress()
  {
    GGPreferences prefs = new GGPreferences(this);
    String oldName = prefs.retrieveString("IPAddress");
    String newName = null;
    while (newName == null || newName.equals(""))
    {
      newName = GGInputDialog.show(this, "EV3", "Enter IP Address",
        oldName == null ? "10.0.1.1" : oldName);
    }
    prefs.storeString("IPAddress", newName);
    return newName;
  }
}
