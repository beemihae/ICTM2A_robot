import ch.aplu.tcpcom.*;
import ch.aplu.util.*;

public class ButtonClient implements TCPClientListener
{
  private ModelessOptionPane mop;

  public ButtonClient()
  {
    int port = 5000;
    InputDialog id = new InputDialog("Button Client", "Host?");
    String host = id.readString();
    if (host == null)
      return;
    TCPClient client = new TCPClient(host, port);
    boolean success = client.connect();
    if (!success)
    {
       mop = new ModelessOptionPane("Connection failed");
       return;
    }
    mop = new ModelessOptionPane("Information from server:");
    mop.setTitle("Button Client");
    client.addTCPClientListener(this);
  }

  public void onStateChanged(String state, String msg)
  {
     mop.setText("State: " + state + ". Message: " + msg);
  }
  
  public static void main(String[] args)
  {
    new ButtonClient();
  }
}