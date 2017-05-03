

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.io.EOFException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Rupert Young
 */
public class SocketClient {

    private final String server;
    private final int port;
    private Socket client;
    private DataInputStream in;
    private DataOutputStream out;

    public SocketClient(String server, int port) {
        this.server = server;
        this.port = port;
    }
 
    public void connect() throws IOException {
        System.out.println("Connecting to " + server + " on port " + port);
        client = new Socket(server, port);
        System.out.println("Just connected to " + client.getRemoteSocketAddress());
        in = new DataInputStream(client.getInputStream());
        out = new DataOutputStream(client.getOutputStream());
    }

    public String receiveAndSend(String response) throws IOException, EOFException {
        String message =  in.readUTF();

        out.writeUTF(response);
        return message;
    }

    public boolean isConnected() {
        if(client==null)
            return false;
        return client.isConnected();
    }

    public void close() throws IOException {
        client.close();
    }

    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);

        SocketClient sc = new SocketClient(serverName, port);
        String message = "hello";
        try {
            sc.connect();

            for (int i = 0; i < 10; i++) {
                message = sc.receiveAndSend("Ok");
                System.out.println(message);
            }
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




class MessageClient extends Thread {

    private static final Logger logger = Logger.getLogger(MessageClient.class.getName());

    private final SocketClient client;
    private boolean firstMessage = true;
    private boolean running = true;
    private String config = null;
    private String message = null;

    public MessageClient(String serverName, int port) {
        client = new SocketClient(serverName, port);
    }

    @SuppressWarnings("SleepWhileInLoop")
    @Override
    public void run() {

        while (!client.isConnected()) {
            try {
                client.connect();
            } catch (IOException e) {
                logger.info("Not connected");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        String msg;
        try {
            while (running) {
                msg = client.receiveAndSend("Ok");
                if (firstMessage) {
                    setConfig(msg);
                    firstMessage = false;
                } else {
                    setMessage(msg);
                }

                //System.out.println(msg);
            }
        } catch (EOFException ex) {
            Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            if (client != null) {
                client.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    synchronized public String getConfig() {
        return config;
    }

    synchronized public void setConfig(String config) {
        this.config = config;
    }

    synchronized public String getMessage() {
        return message;
    }

    synchronized public void setMessage(String message) {
        this.message = message;
    }

}

