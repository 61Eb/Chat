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
    private String role;

    private static int clientsCount = 0;



    public String getUsername() {
        return username;
    }
    public ClientHandler(Server server, Socket socket, String role) throws IOException {
        this.server = server;
        this.socket = socket;
        this.role = role;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        clientsCount++;
        new Thread(() -> {
            try {
                authentication();
                listenUserChatMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }
    private void listenUserChatMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith("/")) {
                if (message.equals("/exit")) {
                    break;
                }
                if (message.startsWith("/w ")) {
                    // TODO homework chat part 1
                }
                if (message.startsWith("/kick ")) {
                    String userToKick = message.split(" ")[1];
                    server.kickUser(userToKick);
                }
            }
            server.broadcastMessage(username + ": " + message);
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean tryToAuthenticate(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1
        if (elements.length != 3) {
            sendMessage("СЕРВЕР: неверная команда аутентификации.");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String usernameFromUserService = server.getUserService().getUsernameByLoginAndPassword(login, password);
        if (usernameFromUserService == null) {
            sendMessage("СЕРВЕР: Пользователь с указанным логином/паролем не существует.");
            return false;
        }
        if (server.isUserBusy(usernameFromUserService)) {
            sendMessage("СЕРВЕР: Аккаунт уже используется");
            return false;
        }
        username = usernameFromUserService;
        role = server.getUserService().getUserRole(username);
        server.subscribe(this);
        sendMessage("/authok " + username);
        sendMessage("СЕРВЕР: " + username + ", добро пожаловать в чат!");
        return true;
    }

    private boolean register(String message) {
        String[] elements = message.split(" "); // /auth login1 pass1 user1
        if (elements.length != 4) {
            sendMessage("СЕРВЕР: неверная команда аутентификации.");
            return false;
        }
        String login = elements[1];
        String password = elements[2];
        String registrationUsername = elements[3];
        if (server.getUserService().isLoginAlreadyExist(login)) {
            sendMessage("СЕРВЕР: указанный логин уже занят.");
            return false;
        }
        if (server.getUserService().isUsernameAlreadyExist(registrationUsername)) {
            sendMessage("СЕРВЕР: указанное имя пользователя уже занято.");
            return false;
        }
        server.getUserService().createNewUser(login, password, registrationUsername);
        username = registrationUsername;
        role = "USER";
        sendMessage("/authok " + username);
        sendMessage("СЕРВЕР: " + username + ", вы успешно зарегистрировались и вошли в чат!");
        server.subscribe(this);
        return true;
    }

    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            boolean isSucceed = false;
            if (message.startsWith("/auth ")) {
                isSucceed = tryToAuthenticate(message);
            } else if (message.startsWith("/register ")) {
                isSucceed = register(message);
            } else {
                sendMessage("СЕРВЕР: Вам необходимо войти или зарегистрироваться");
            }
            if (isSucceed) {
                break;
            }
        }
    }
}
