// StatServer.java

import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class StatServer implements TCPServerListener
{
  private final int sliceSize = 20000000;
  private StringEntry local = new StringEntry("Local Status: ");
  private StringEntry remote = new StringEntry("Remote Status: ");
  private EntryPane pane1 = new EntryPane(local, remote);
  private StringEntry locresult = new StringEntry("Local Result: ");
  private StringEntry remresult = new StringEntry("Remote Result: ");
  private StringEntry totresult = new StringEntry("Total Result: ");
  private EntryPane pane2 = new EntryPane(locresult, remresult, totresult);
  private EntryDialog dlg = new EntryDialog(pane1, pane2);
  private int local_n = 0;
  private int local_k = 0;
  private int remote_n = 0;
  private int remote_k = 0;
  private TCPServer server;

  public StatServer()
  {
    initDialog();
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    Monitor.putSleep();
    if (dlg.isDisposed())
      return;
    local.setValue("Working...");
    int n = 0;
    int k = 0;
    long startTime = System.nanoTime();
    while (!dlg.isDisposed() && server.isConnected())
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
        local_n = n;
        local_k = k;
        showTotal();
      }
    }
    server.terminate();
  }

  public void onStateChanged(String state, String msg)
  {
    if (state.equals(TCPServer.LISTENING))
    {
      local.setValue("Waiting");
      remote.setValue("Not connected.");
    }
    else if (state.equals(TCPServer.CONNECTED))
    {
      remote.setValue("Connected. Working...");
      Monitor.wakeUp();
    }
    else if (state.equals(TCPServer.TERMINATED))
    {
      local.setValue("Terminated");
      remote.setValue("Not connected.");
    }
    else if (state.equals(TCPServer.MESSAGE))
    {
      String[] li = msg.split(";");
      int n = Integer.parseInt(li[0].trim());
      int k = Integer.parseInt(li[1].trim());
      double pi = 4.0 * k / n;
      String info = String.format("n: %d; k: %d; pi: %f", n, k, pi);
      remresult.setValue(info);
      remote_n = n;
      remote_k = k;
      showTotal();
    }
  }

  private void initDialog()
  {
    local.setEditable(false);
    remote.setEditable(false);
    locresult.setEditable(false);
    remresult.setEditable(false);
    totresult.setEditable(false);
    dlg.setTitle("Server Information");
    dlg.show();

    local.setValue("Waiting for connection...");
    locresult.setValue("(n/a)");
    remresult.setValue("(n/a)");
    totresult.setValue("(n/a)");

    dlg.addExitListener(new ExitListener()
    {
      public void notifyExit()
      {
        dlg.dispose();
        server.terminate();
        Monitor.wakeUp();
      }
    }
    );
  }

  private void showTotal()
  {
    int n = remote_n + local_n;
    int k = remote_k + local_k;
    double pi = 4.0 * k / n;
    String info = String.format("n: %d; k: %d; pi: %f", n, k, pi);
    totresult.setValue(info);
  }

  public static void main(String[] args)
  {
    new StatServer();
  }
}
