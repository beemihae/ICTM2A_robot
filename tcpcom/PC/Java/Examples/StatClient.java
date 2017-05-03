// StatClient.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class StatClient implements TCPClientListener
{
  private final int sliceSize = 20000000;
  private StringEntry local = new StringEntry("Local Status: ");
  private StringEntry remote = new StringEntry("Remote Status: ");
  private EntryPane pane1 = new EntryPane(local, remote);
  private StringEntry locresult = new StringEntry("Local Result: ");
  private EntryPane pane2 = new EntryPane(locresult);
  private EntryDialog dlg = new EntryDialog(pane1, pane2);
  private TCPClient client;

  public StatClient()
  {
    initDialog();
    String host = "localhost";
    int port = 5000;
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    boolean success = client.connect();
    if (!success)
      return;
    int n = 0;
    int k = 0;
    long startTime = System.nanoTime();
    while (!dlg.isDisposed() && client.isConnected())
    {
      double zx = Math.random();
      double zy = Math.random();
      if (zx * zx + zy * zy < 1)
        k += 1;
      n += 1;
      if (n % sliceSize == 0)
      {
        double pi = 4.0 * k / n;
        double t = (System.nanoTime() - startTime) / 1E9;
        String info = String.format("n: %d; k: %d; pi: %f; t: %3.1f", n, k, pi, t);
        locresult.setValue(info);
        client.sendMessage(n + ";" + k);
      }
    }
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPClient.CONNECTION_FAILED))
      local.setValue("Connection failed");
    else if (state.equals(TCPClient.DISCONNECTED))
    {
      local.setValue("Connection broken");
      remote.setValue("Disconnected");
    }
    else if (state.equals(TCPServer.CONNECTED))
    {
      local.setValue("Working...");
      remote.setValue("Connected. Working...");
    }
  }

  private void initDialog()
  {
    local.setEditable(false);
    remote.setEditable(false);
    locresult.setEditable(false);
    dlg.setTitle("Client Information");
    dlg.show();

    local.setValue("Trying to connect...");
    remote.setValue("Disconnected");
    locresult.setValue("(n/a)");

    dlg.addExitListener(new ExitListener()
    {
      public void notifyExit()
      {
        dlg.dispose();
        client.disconnect();
      }
    }
    );
  }

  public static void main(String[] args)
  {
    new StatClient();
  }
}
