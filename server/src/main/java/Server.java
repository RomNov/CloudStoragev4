import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

public class Server {
    ServerSocket serverSocket;
    Authorization authorization;

    public Server(){
        try {
            ServerMain.logger.log(Level.INFO, "Старт сервера");
            serverSocket = new ServerSocket(8189);
            authorization = new Authorization();
            authorization.start();
            while (true){
                Socket socket = serverSocket.accept();
                new ClientHandler(this ,socket);
                ServerMain.logger.log(Level.INFO, "Клиент подключился");
            }
        } catch (IOException e) {
            ServerMain.logger.log(Level.WARNING, "Ошибка ввода-вывода " + e.getMessage());
            e.printStackTrace();
        } finally {
            authorization.stop();
            ServerMain.fh.close();
        }
    }
}
