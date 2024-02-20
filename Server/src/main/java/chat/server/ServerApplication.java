package chat.server;

public class ServerApplication {
    public ServerApplication() {
    }
    public static void main(String[] args) {
        Server server = new Server(8189);
        server.start();
    }
}