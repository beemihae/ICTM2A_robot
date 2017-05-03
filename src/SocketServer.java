

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import lejos.hardware.lcd.LCD;
/**
 *
 * @author Rupert Young
 */
public class SocketServer {

    private final ServerSocket serverSocket;
    private Socket server;
    private DataOutputStream out;
    private DataInputStream in;

    public SocketServer(int port, int timeout) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(timeout);
    }

    public void connect() throws IOException {
        System.out.println("Wait on " + serverSocket.getLocalPort() + "...");
        server = serverSocket.accept();

        System.out.println("Connected to " + server.getRemoteSocketAddress());
        out = new DataOutputStream(server.getOutputStream());
        in = new DataInputStream(server.getInputStream());
    }

    public int getPort() {
        int port = 0;
        if (serverSocket != null) {
            port = serverSocket.getLocalPort();
        }
        return port;
    }

    public boolean isConnected() {
        if (server == null) {
            return false;
        }
        return server.isConnected();
    }

    public String socketStatus() {
        return server == null ? "null" : "not null";
    }

    public void connectionLost() {
        server = null;
    }

    public String sendAndReceive(String message) throws IOException {
        out.writeUTF(message);
        String response = in.readUTF();
        return response;
    }

   @SuppressWarnings("null")
    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        int timeout = 30000;
        SocketServer ss = null;

        try {
            ss = new SocketServer(port, timeout);
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ss.connect();
        } catch (java.net.SocketTimeoutException ex) {
            Logger.getLogger(ex.toString());
        } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (ss.isConnected()) {
            String response = null;
            try {
                response = ss.sendAndReceive("1");
                System.out.println("Response  " + response);
                response = ss.sendAndReceive("2");
                System.out.println("Response  " + response);
                response = ss.sendAndReceive("3");
                System.out.println("Response  " + response);
            } catch (IOException ex) {
                Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
 class MessageServer extends Thread {

   static final Logger logger = Logger.getLogger(SocketServer.class.getName());

   private SocketServer ss = null;
   private boolean finished = false;
   private BaseControlHierarchy ch;
   private String config;
   private boolean sentConfig = false;
   private int port;

   public MessageServer(int port, int timeout, BaseControlHierarchy ch,
         String config) {
      try {
         this.config = config;
         this.ch = ch;
         this.port = port;
         ss = new SocketServer(port, timeout);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public String getMessage() throws IOException {
      String rtn = null;
      if (!sentConfig) {
         Charset cs = Charset.defaultCharset();
         rtn = config + FileReader.readFile(config, cs);
         sentConfig = true;
      } else {
         rtn = ch.getDelimitedString();
      }

      return rtn;
   }

   public void run() {
      // int ctr=0;
      while (!finished) {
         try {
            if (!ss.isConnected()) {
               LCD.drawString(port + ".....", 0, 7);
               ss.connect();
               LCD.drawString("connected", 0, 7);            }
         } catch (java.net.SocketTimeoutException ex) {
            System.out.println("Timed out");
            continue;
         } catch (IOException ex) {
            Logger.getLogger(SocketServer.class.getName()).log(
                  Level.SEVERE, null, ex);
         } catch (Exception e) {
            e.printStackTrace();
         }

         if (ss.isConnected()) {
            String response = null;
            try {

               String msg = getMessage();
               // System.out.println("M:" + msg.substring(0, msg.length() >
               // 10 ? 10 : msg.length() ));

               response = ss.sendAndReceive(msg);
               // System.out.println("Response  " + response);

            } catch (IOException ex) {
               logger.warning(ex.toString());
               logger.info("socket " + ss.socketStatus());
               ss.connectionLost();
               sentConfig = false;
            } catch (Exception e) {
               e.printStackTrace();
            }
         }
         // if(ctr++>5)
         // finished = true;
      }
      // System.out.println("Server finished");

   }

   public boolean isFinished() {
      return finished;
   }

   public void setFinished(boolean finished) {
      this.finished = finished;
   }

}