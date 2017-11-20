import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MainClient extends Application {

    public Client client;
    Controller controller;
    public static Logger logger;
    FileHandler fh;

    @Override
    public void start(Stage primaryStage){
        logger = Logger.getLogger(MainClient.class.getName());
        try {
            fh = new FileHandler("%tLogCloudStorageMainClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addHandler(fh);
        logger.log(Level.INFO, "Старт приложения");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = null;
        try {
            root = (Parent) loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        controller = loader.<Controller>getController();
        client = new Client(controller, primaryStage);
        controller.setClient(client);
        controller.fileChooser = new FileChooser();
        primaryStage.setTitle("CloudStorage");
        primaryStage.setScene(new Scene(root, 400, 60));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        controller.client.close();
        logger.log(Level.INFO, "Остановка приложения");
        fh.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
