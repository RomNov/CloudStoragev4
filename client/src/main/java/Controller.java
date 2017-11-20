
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.util.logging.Level;


public class Controller {
    public ListView<String> listView;
    public Label stateLabel;
    public VBox root;
    public HBox authBox;
    public TextField loginText;
    public PasswordField passwordText;
    public Button loginButton;
    public VBox content;
    public FileChooser fileChooser;
    static String newFileName;
    public Client client;

    public static class ModalWindow{

        public static void startWindow(String oldName) {
            MainClient.logger.log(Level.INFO, "Создание модального окна для переименовывания файла");

            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            Pane root = new VBox();
            HBox hBox = new HBox();
            hBox.setAlignment(Pos.CENTER);
            TextField newName = new TextField(oldName);
            newName.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    newFileName = newName.getText();
                    window.close();
                }
            });
            Button btnOk = new Button("Ок");
            btnOk.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    newFileName = newName.getText();
                    window.close();
                }
            });
            Button btnCancel = new Button("Отмена");
            btnCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    window.close();
                }
            });
            hBox.getChildren().addAll(btnOk, btnCancel);
            root.getChildren().addAll(newName, hBox);



            Scene scene = new Scene(root, 300, 60);
            window.setScene(scene);
            window.setTitle("Введите новое имя");
            window.showAndWait();
        }
    }

    public void delete() {
        stateLabel.setText("");
        String fileName = listView.getSelectionModel().getSelectedItem();
        MainClient.logger.log(Level.INFO, "Отправка команды серверу на удаление файла " + fileName);
        if (fileName == null){
            stateLabel.setStyle("-fx-text-fill:#ff0000");
            stateLabel.setText("Ошибка: Вы не выбрали файл");
            return;
        }
        client.sendMessage(new DeleteMessage(fileName));
    }

    public void rename() {
        newFileName = null;
        stateLabel.setText("");
        String oldFileName = listView.getSelectionModel().getSelectedItem();
        if (oldFileName == null){
            stateLabel.setStyle("-fx-text-fill:#ff0000");
            stateLabel.setText("Ошибка: нужно выбрать файл");
            MainClient.logger.log(Level.WARNING, "Не выбран файл для переименовывания");
            return;
        }
        ModalWindow.startWindow(oldFileName);
        if (newFileName == null){
            stateLabel.setStyle("-fx-text-fill:#ff0000");
            stateLabel.setText("Ошибка: имя файла не может быть пустым");
            MainClient.logger.log(Level.WARNING, "Новое имя файла пусто");
            return;
        }
        if (newFileName.equals(oldFileName)){
            MainClient.logger.log(Level.WARNING, "Новое имя равно старому");
            return;
        }
        for (String o: listView.getItems()) {
            if (o.equals(newFileName)){
                stateLabel.setStyle("-fx-text-fill:#ff0000");
                stateLabel.setText("Ошибка: Файл с таким именем уже существует на сервере");
                MainClient.logger.log(Level.WARNING, "Новое имя совпадает с именем другого файла на сервере");
                return;
            }
        }
        MainClient.logger.log(Level.INFO, "Отправка команды серверу на переименовывание файла " + oldFileName
        +" в " + newFileName);
        client.sendMessage(new RenameMessage(newFileName, oldFileName));
    }

    public void setClient(Client client) {
        MainClient.logger.log(Level.INFO, "Установка текущего клиента " + client.getNickName());
        this.client = client;
    }

    public void sendAuth() {
        client.sendMessage(new AuthMessage(loginText.getText(), passwordText.getText()));
        MainClient.logger.log(Level.INFO, "Отправка запроса на автоизацию " + loginText.getText() +
                " " + passwordText.getText());
    }

    public void fileupload() {
        MainClient.logger.log(Level.INFO, "Попытка загузить файл на сервер");
        stateLabel.setText("");
        fileChooser.setInitialDirectory(new File("C:/"));
        fileChooser.setTitle("Выберите файл для загрузки");
        File file = fileChooser.showOpenDialog(client.getPrimaryStage());
        if(file == null){
            MainClient.logger.log(Level.WARNING, "файл отсутствует");
            return;
        }
        for (String o: listView.getItems()) {
            if (o.equals(file.getName())){
                stateLabel.setStyle("-fx-text-fill:#ff0000");
                stateLabel.setText("Ошибка: Файл с таким именем уже существует на сервере");
                MainClient.logger.log(Level.WARNING, "имя файла совпадает с именем файла на сервере");
                return;
            }
        }
        FileMessage newFile = new FileMessage(file);
        if (newFile.getFileBody() == null){
            stateLabel.setStyle("-fx-text-fill:#ff0000");
            stateLabel.setText("Ошибка: Файл слишком велик. Выберите файл размером меньше 5 МБ");
            MainClient.logger.log(Level.WARNING, "файл больше 5 мегабайт");
            return;
        }
        MainClient.logger.log(Level.INFO, "файл отправлен " + newFile.getFileName());
        client.sendMessage(newFile);

    }

    public void fileDownload() {
        MainClient.logger.log(Level.INFO, "попытка скачать файл с сервера");
        stateLabel.setText("");
        String fileName = listView.getSelectionModel().getSelectedItem();
        if (fileName == null){
            stateLabel.setStyle("-fx-text-fill:#ff0000");
            stateLabel.setText("Ошибка: Вы не выбрали файл");
            MainClient.logger.log(Level.WARNING, "файл не был выбран");
            return;
        }
        fileChooser.setInitialDirectory(new File("C:/"));
        fileChooser.setInitialFileName(fileName);
        fileChooser.setTitle("Выберите куда сохранить");
        File savePath = fileChooser.showSaveDialog(client.getPrimaryStage());
        System.out.println(savePath);
        if (savePath == null){
            MainClient.logger.log(Level.WARNING, "не был выбран путь для сохранения");
            return;
        }
        MainClient.logger.log(Level.INFO, "Отправка запроса на скачивание файла на сервер " +
                savePath + " " + fileName);
        client.sendMessage(new FileRequestMessage(savePath, fileName));
    }

    public void exit() {
        MainClient.logger.log(Level.INFO, "клиент вышел");
        Stage stage = client.getPrimaryStage();
        client.close();
        authBox.setVisible(true);
        authBox.setManaged(true);
        content.setVisible(false);
        content.setManaged(false);
        this.client = new Client(this, stage);
    }
}
