package Chat.callBack;

import java.io.IOException;
import java.util.Scanner;

class ClientApplicationWithCallbacks {
    ClientApplicationWithCallbacks() {
    }

    public static void main(String[] args) {
        try {
            Network network = new Network();

            try {
                network.setOnMessageReceived((arguments) -> {
                    System.out.println((String)arguments[0]);
                });
                network.connect(8189);
                System.out.println("Подключились к серверу");
                Scanner scanner = new Scanner(System.in);

                String message;
                do {
                    message = scanner.nextLine();
                    network.sendMessage(message);
                } while(!message.equals("/exit"));
            } catch (Throwable var5) {
                try {
                    network.close();
                } catch (Throwable var4) {
                    var5.addSuppressed(var4);
                }

                throw var5;
            }

            network.close();
        } catch (IOException var6) {
            var6.printStackTrace();
        }

    }
}

