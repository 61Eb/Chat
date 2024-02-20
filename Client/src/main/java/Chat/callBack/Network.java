package Chat.callBack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network implements AutoCloseable {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Callback onMessageReceived;

    public Network() {
    }

    public void setOnMessageReceived(Callback onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void connect(int port) throws IOException {
        this.socket = new Socket("localhost", port);
        this.in = new DataInputStream(this.socket.getInputStream());
        this.out = new DataOutputStream(this.socket.getOutputStream());
        (new Thread(() -> {
            try {
                while(true) {
                    String message = this.in.readUTF();
                    if (this.onMessageReceived != null) {
                        this.onMessageReceived.callback(new Object[]{message});
                    }
                }
            } catch (IOException var5) {
                var5.printStackTrace();
            } finally {
                this.close();
            }

        })).start();
    }

    public void sendMessage(String message) throws IOException {
        this.out.writeUTF(message);
    }

    public void close() {
        if (this.in != null) {
            try {
                this.in.close();
            } catch (IOException var4) {
                var4.printStackTrace();
            }
        }

        if (this.out != null) {
            try {
                this.out.close();
            } catch (IOException var3) {
                var3.printStackTrace();
            }
        }

        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (IOException var2) {
                var2.printStackTrace();
            }
        }

    }
}

