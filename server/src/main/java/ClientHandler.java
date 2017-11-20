import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

public class ClientHandler {
    ObjectOutputStream out;
    ObjectInputStream in;
    String nickName;
    Server server;
    Socket socket;
    ArrayList<String> fileList;
    File directory;


    ClientHandler(Server server, Socket socket){
        ServerMain.logger.log(Level.INFO, "Создание клиент хэндлера на сервере");
        this.server = server;
        try {
            this.socket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ServerMain.logger.log(Level.INFO, "Вход в поток прослушивания сообщений от клиента");
                    AbstractMessage msg;
                    while(true){
                        try {
                            msg = (AbstractMessage) in.readObject();
                            messageAnalyzer(msg);
                        } catch (IOException e) {
                            ServerMain.logger.log(Level.WARNING, "Ошибка ввода-вывода " + e.getMessage());
                            e.printStackTrace();
                            break;
                        } catch (ClassNotFoundException e) {
                            ServerMain.logger.log(Level.WARNING, "Ошибка приведения типов " + e.getMessage());
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            ServerMain.logger.log(Level.WARNING, "Ошибка ввода-вывода " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void sendMessage(AbstractMessage msg){
        try {
            out.writeObject(msg);
            ServerMain.logger.log(Level.INFO, "Сообщение " + msg.getName() + " отправлено");
            System.out.println("Сообщение " + msg.getName() + " отправлено");
        } catch (IOException e) {
            ServerMain.logger.log(Level.WARNING, "Не удалось отправить сообщение " + msg.getName() +
                    " " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void messageAnalyzer(AbstractMessage msg){
        ServerMain.logger.log(Level.INFO, "Получено сообщение " + msg.name);
        if (msg.getName().equals("auth")){
            ServerMain.logger.log(Level.INFO, "Получен запрос авторизации " + ((AuthMessage)msg).getLogin() +
             " " + ((AuthMessage)msg).getPassword());
            nickName = server.authorization.getNick(((AuthMessage)msg).getLogin(), ((AuthMessage)msg).getPassword());
            if (nickName == null){
                sendMessage(new AuthAnswerMessage(nickName));
                ServerMain.logger.log(Level.WARNING, "неверный логин или пароль");
                return;
            }
            directory = new File("/CloudStorage/" + nickName);
            if (!directory.exists()){
                directory.mkdirs();
            }
            fileList = new ArrayList<String>(Arrays.asList(directory.list()));
            ServerMain.logger.log(Level.INFO, "Отправка никнейма и списка файлов клиенту");
            sendMessage(new AuthAnswerMessage(nickName));
            sendMessage(new FileListMessage(fileList));
            return;
        }
        if (msg.getName().equals("file")) {
            FileMessage fileMsg = (FileMessage)msg;
            ServerMain.logger.log(Level.INFO, "получене сообщение файл: " + fileMsg.fileName);
            File newFile = new File("/CloudStorage/" + nickName + "/" + fileMsg.fileName);
            try {
                FileOutputStream fos = new FileOutputStream(newFile);
                fos.write(fileMsg.fileBody);
                fos.close();
            } catch (FileNotFoundException e) {
                ServerMain.logger.log(Level.WARNING, "Ошибка при попытке создать файл " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e){
                ServerMain.logger.log(Level.WARNING, "Ошибка ввода-вывода " + e.getMessage());
                e.printStackTrace();
        }
            ServerMain.logger.log(Level.INFO, "Отправка списка файлов клиенту");
            fileList = new ArrayList<String>(Arrays.asList(directory.list()));
            sendMessage(new FileListMessage(fileList));
            return;
        }
        if (msg.getName().equals("filereq")) {
            FileRequestMessage fileReq = (FileRequestMessage) msg;
            ServerMain.logger.log(Level.INFO, "получене сообщение запрос файла: " + ((FileRequestMessage)msg).getFileName());
            FileAnswerMessage fileAnswer = new FileAnswerMessage(fileReq.getPath());
            try {
                FileInputStream fis = new FileInputStream("/CloudStorage/" + nickName + "/" + fileReq.getFileName());
                byte arr[] = new byte[fis.available()];
                fis.read(arr);
                fileAnswer.setFileBody(arr);
                fis.close();
            } catch (FileNotFoundException e) {
                ServerMain.logger.log(Level.WARNING, "Ошибка при попытке открыть файл " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e){
                ServerMain.logger.log(Level.WARNING, "Ошибка ввода-вывода " + e.getMessage());
                e.printStackTrace();
            }
            ServerMain.logger.log(Level.INFO, "Отправка файла клиенту");
            sendMessage(fileAnswer);
            return;
        }
        if (msg.getName().equals("rename")) {
            RenameMessage remsg = (RenameMessage)msg;
            ServerMain.logger.log(Level.INFO, "получене сообщение переименовать файл: " + remsg.getOldName() +
             " в " + remsg.getNewName());
            File file = new File("/CloudStorage/" + nickName + "/" + remsg.getOldName());
            file.renameTo(new File("/CloudStorage/" + nickName + "/" + remsg.getNewName()));
            fileList = new ArrayList<String>(Arrays.asList(directory.list()));
            ServerMain.logger.log(Level.INFO, "отправка списка файлов клиенту");
            sendMessage(new FileListMessage(fileList));
            return;
        }
        if (msg.getName().equals("delete")) {
            DeleteMessage delmsg = (DeleteMessage)msg;
            ServerMain.logger.log(Level.INFO, "получене сообщение удалить файл: " + delmsg.getFileName());
            File file = new File("/CloudStorage/" + nickName + "/" + delmsg.getFileName());
            file.delete();
            ServerMain.logger.log(Level.INFO, "отправка списка файлов клиенту");
            fileList = new ArrayList<String>(Arrays.asList(directory.list()));
            sendMessage(new FileListMessage(fileList));
        }
    }
}
