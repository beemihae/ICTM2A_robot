// TimeServer.java

import ch.aplu.tcpcom.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

public class TimeServer implements TCPServerListener
{
  private TCPServer server;

  public TimeServer()
  {
    int port = 5000;
    server = new TCPServer(port);
    server.addTCPServerListener(this);
    // Do not click the title bar's close button
    JOptionPane.showMessageDialog(null, "Time server running. OK to stop.",
      "Time Server", JOptionPane.INFORMATION_MESSAGE);
    server.terminate();
  }

  public void onStateChanged(String state, String msg)
  {
    System.out.println("State: " + state + "; Msg: " + msg);
    if (state.equals(TCPServer.CONNECTED))
    {
      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      Date date = new Date();
      server.sendMessage(dateFormat.format(date));
    }
  }

  public static void main(String[] args)
  {
    new TimeServer();
  }
}
