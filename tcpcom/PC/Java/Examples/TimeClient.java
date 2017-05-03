// TimeClient.java

import ch.aplu.tcpcom.*;
import javax.swing.JOptionPane;

public class TimeClient implements TCPClientListener
{
  private TCPClient client;
  private String host;

  public TimeClient()
  {
    int port = 5000;
    host = JOptionPane.showInputDialog("IPAddress", "localhost");
    if (host == null)
      return;
    client = new TCPClient(host, port);
    client.addTCPClientListener(this);
    client.connect();
  }

  public void onStateChanged(String state, String msg)
  {
    System.out.println("State: " + state + "; Msg: " + msg);
    if (state.equals(TCPClient.MESSAGE))
    {
      client.disconnect();
      JOptionPane.showMessageDialog(null,
        "Server reports local date/time: " + msg,
        "Time Client", JOptionPane.INFORMATION_MESSAGE);
    }
    else if (state.equals(TCPClient.CONNECTION_FAILED))
      JOptionPane.showMessageDialog(null,
        "Server " + host + " not available",
        "Time Client", JOptionPane.INFORMATION_MESSAGE);
  }

  public static void main(String[] args)
  {
    new TimeClient();
  }
}
