package Chat.callBack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;

public class ClientApplication {

    String url      = "jdbc:mysql://127.0.0.1:3306/?user=root";
    String dbName   = "database auntification";
    String userName = "root";
    String password = "87654321";

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("localhost", 8189);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {
            System.out.println("Подключились к серверу");
            Scanner scanner = new Scanner(System.in);

            new Thread(() -> {
                try {
                    while (true) {
                        String message = in.readUTF();
                        System.out.println(message);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                String message = scanner.nextLine();
                out.writeUTF(message);
                if (message.equals("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Connection conn = null;
        long st = System.currentTimeMillis();
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/gleb?user=root + database auntification", "root", "87654321");
            log("---get connection in " + (System.currentTimeMillis() - st) + "(ms)");
            st = System.currentTimeMillis();
            if (conn != null)
                System.out.println("Приложение подключилось к БД !");
            else
                System.out.println("Приложение НЕ подключилось к БД ?");

            int userId = 2;
            String userPwd = "654321";


            String sql = "select UserId, UserName, Pwd from Users where UserId=? and Pwd=?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            log("---prepare stmt in " + (System.currentTimeMillis() - st) + "(ms)");
            st = System.currentTimeMillis();
            stmt.setInt(1, userId);
            stmt.setString(2, userPwd);

            log("--try execute stmt: " + stmt);

            java.sql.ResultSet rs = stmt.executeQuery();
            log("---executeQuery in " + (System.currentTimeMillis() - st) + "(ms)");
            st = System.currentTimeMillis();
            log("--rs:" + rs);
            boolean isUserExist = false;
            if (rs.next()) {
                isUserExist = true;
                log("--UserId: " + rs.getInt("UserId"));
                log("--UserName: " + rs.getString("UserName"));
                log("--Pwd: " + rs.getString("Pwd"));

            }
            log("---isUserExist: " + isUserExist);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }
        }
    }

    private static void log(String str) {
        System.out.println(str);
    }
}