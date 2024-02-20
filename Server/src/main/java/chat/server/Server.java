package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);

            try {
                System.out.printf("Сервер запущен на порту %d. Ожидание подключения клиентов\n", this.port);

                while(true) {
                    Socket socket = serverSocket.accept();

                    try {
                        this.subscribe(new ClientHandler(this, socket));
                    } catch (IOException var5) {
                        System.out.println("Не удалось подключить клиента");
                    }
                }
            } catch (Throwable var6) {
                try {
                    serverSocket.close();
                } catch (Throwable var4) {
                    var6.addSuppressed(var4);
                }

                throw var6;
            }
        } catch (IOException var7) {
            var7.printStackTrace();
        }
    }

    public synchronized void broadcastMessage(String message) {
        Iterator var2 = this.clients.iterator();

        while(var2.hasNext()) {
            ClientHandler clientHandler = (ClientHandler)var2.next();
            clientHandler.sendMessage(message);
        }

    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
        System.out.println("Подключился новый клиент " + clientHandler.getUsername());
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
        System.out.println("Отключился клиент " + clientHandler.getUsername());
    }

    public synchronized void sendPrivateMessage(ClientHandler sender, String receiverUsername, String message) {
        Iterator var4 = this.clients.iterator();

        while(var4.hasNext()) {
            ClientHandler client = (ClientHandler)var4.next();
            if (client.getUsername().equals(receiverUsername)) {
                client.sendMessage("Пользователь " + sender.getUsername() + " пишет: " + message);
            }
        }

    }
}

