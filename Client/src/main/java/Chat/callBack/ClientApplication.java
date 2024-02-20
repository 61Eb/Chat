package Chat.callBack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    static String username;

    public ClientApplication() {
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8189);

            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());

                try {
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                    try {
                        System.out.println("Подключились к серверу");
                        Scanner scanner = new Scanner(System.in);
                        (new Thread(() -> {
                            try {
                                while(true) {
                                    String message = in.readUTF();
                                    if (message.startsWith("/") && message.startsWith("/authok ")) {
                                        username = message.split(" ")[1];

                                        while(true) {
                                            message = in.readUTF();
                                            System.out.println(message);
                                        }
                                    }

                                    System.out.println(message);
                                }
                            } catch (IOException var2) {
                                var2.printStackTrace();
                            }
                        })).start();

                        String message;
                        do {
                            message = scanner.nextLine();
                            out.writeUTF(message);
                        } while(!message.equals("/exit"));
                    } catch (Throwable var9) {
                        try {
                            out.close();
                        } catch (Throwable var8) {
                            var9.addSuppressed(var8);
                        }

                        throw var9;
                    }

                    out.close();
                } catch (Throwable var10) {
                    try {
                        in.close();
                    } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                    }

                    throw var10;
                }

                in.close();
            } catch (Throwable var11) {
                try {
                    socket.close();
                } catch (Throwable var6) {
                    var11.addSuppressed(var6);
                }

                throw var11;
            }

            socket.close();
        } catch (IOException var12) {
            var12.printStackTrace();
        }

    }
}

