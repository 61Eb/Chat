package chat.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;
    private String username;
    private static int clientsCount = 0;

    public String getUsername() {
        return this.username;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        ++clientsCount;
        this.username = "user" + clientsCount;
        (new Thread(() -> {
            try {
                while(true) {
                    String rawMessage = this.in.readUTF();
                    if (rawMessage.startsWith("/")) {
                        if (rawMessage.equals("/exit")) {
                            break;
                        }

                        if (rawMessage.startsWith("/w ")) {
                            String[] elements = rawMessage.split(" ", 3);
                            String recipient = elements[1];
                            String message = elements[2];
                            server.sendPrivateMessage(this, recipient, message);
                        }
                    }

                    server.broadcastMessage(this.username + ": " + rawMessage);
                }
            } catch (IOException var9) {
                var9.printStackTrace();
            } finally {
                this.disconnect();
            }

        })).start();
    }

    public void sendMessage(String message) {
        try {
            this.out.writeUTF(message);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public void disconnect() {
        this.server.unsubscribe(this);

        try {
            if (this.in != null) {
                this.in.close();
            }
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        try {
            if (this.out != null) {
                this.out.close();
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        try {
            if (this.socket != null) {
                this.socket.close();
            }
        } catch (IOException var2) {
            var2.printStackTrace();
        }

    }
}
