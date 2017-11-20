import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;

public class Client {

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String nickName;
    private Controller controller;
    private ArrayList<String> fileList;
    private Thread thread;
    private Stage primaryStage;

    public ArrayList<String> getFileList() {
        return fileList;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Client(Controller controller, Stage stage){
        MainClient.logger.log(Level.INFO, "Инициализация клиента");
        this.controller = controller;
        this.primaryStage = stage;

        try {
            socket = new Socket("localhost", 8189);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            MainClient.logger.log(Level.INFO, "Подключились к серверу");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    AbstractMessage msg;
                    while (true){
                        try {
                            msg = (AbstractMessage) in.readObject();
                            messageAnalyzer(msg);
                        } catch (IOException e) {
                            MainClient.logger.log(Level.WARNING, "Ошибка ввода-вывода в потоке прослушивания" +
                                    " сообщений от сервера " + e.getMessage());
                            e.printStackTrace();
                            break;
                        } catch (ClassNotFoundException e) {
                            MainClient.logger.log(Level.WARNING, "Ошибка приведения типов в потоке прослушивания" +
                                    " сообщений от сервера " + e.getMessage());
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            });
            thread.start();
        } catch (IOException e) {
            MainClient.logger.log(Level.WARNING, "Ошибка при попытке подключиться к серверу " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void close(){
        MainClient.logger.log(Level.INFO, "Выход из клиента");
        try {
            thread.interrupt();
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            MainClient.logger.log(Level.WARNING, "Ошибка при попытке закрыть потоки клиента " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public void sendMessage(AbstractMessage msg){
        MainClient.logger.log(Level.INFO, "Отправка сообщения " + msg.getName());
        try {
            out.writeObject(msg);
        } catch (IOException e) {
            MainClient.logger.log(Level.WARNING, "Ошибка при попытке отправить сообщение " + msg.getName() +
                    " " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void messageAnalyzer(AbstractMessage msg){
        MainClient.logger.log(Level.INFO, "Получено сообщение " + msg.getName());
        if (msg.getName().equals("authanswer")){
            this.nickName = ((AuthAnswerMessage)msg).getNickName();
            if(nickName == null){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.stateLabel.setText("неверный логин или пароль");
                    }
                });
                MainClient.logger.log(Level.WARNING, "никнейм равен null");
                return;
            }
            MainClient.logger.log(Level.INFO, "никнейм - " + nickName);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.authBox.setVisible(false);
                    controller.authBox.setManaged(false);
                    controller.content.setVisible(true);
                    controller.content.setManaged(true);
                    primaryStage.setMinHeight(400);
                    primaryStage.setMinWidth(600);
                }
            });
            MainClient.logger.log(Level.INFO, "Авторизация прошла успешно");
            return;
        }
        if (msg.getName().equals("filelist")){
            fileList = ((FileListMessage)msg).getFileList();
            StringBuilder strB = new StringBuilder();
            for (String o:fileList) {
                strB.append(o).append(" || ");
            }
            MainClient.logger.log(Level.INFO, "получен список файлов от сервера: " + strB);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    controller.listView.setItems(FXCollections.observableList(fileList));
                }
            });
            return;
        }
        if (msg.getName().equals("fileanswer")){
            MainClient.logger.log(Level.INFO, "получен файл от сервера");
            FileAnswerMessage answerMsg = (FileAnswerMessage)msg;
            try {
                FileOutputStream fos = new FileOutputStream(answerMsg.getPath());
                fos.write(answerMsg.getFileBody());
                fos.close();
            } catch (FileNotFoundException e) {
                MainClient.logger.log(Level.WARNING, "Ошибка при попытке при попытке создать файл " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                MainClient.logger.log(Level.WARNING, "Ошибка при записи данных в файл " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
