import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    public static Logger logger;
    public static FileHandler fh;
    public static void main(String[] args) {
        logger = Logger.getLogger(ServerMain.class.getName());
        try {
            fh = new FileHandler("%tLogCloudStorageMainServer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addHandler(fh);
        logger.log(Level.INFO, "Старт приложения");
        new Server();
    }

}
