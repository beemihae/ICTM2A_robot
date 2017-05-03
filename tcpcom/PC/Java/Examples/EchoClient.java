// EchoClient.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class EchoClient implements TCPClientListener
{
  private ModelessOptionPane mop;

  public EchoClient()
  {
    int port = 5000;
    InputDialog id = new InputDialog("Echo Client", "Host?");
    String host = id.readString();
    if (host == null)
      return;
    TCPClient client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    mop = new ModelessOptionPane("Trying to connect to " + host + ":" + port);
    mop.setTitle("Echo Client");
    boolean success = client.connect();
    if (!success)
    {
       mop.setText("Connection failed");
       return;
    }
    mop.setTitle("Reply from server");
    for (int i = 0; i < 100; i++)
    {  
      client.sendMessage("" + i);
      Console.delay(100); // slow down
    }
    client.disconnect();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPClient.MESSAGE))
        mop.setText("Got: " + msg);
    else if (state.equals(TCPClient.DISCONNECTED))
        mop.setTitle("Disconnected");
  }
  
  public static void main(String[] args)
  {
    new EchoClient();
  }
}
