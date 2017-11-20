import java.sql.*;
import java.util.logging.Level;

public class Authorization {
    private Connection connection;
    private Statement statement;

    public void start() {
        try {
            ServerMain.logger.log(Level.INFO, "Старт сервиса авторизации");
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:server\\src\\main\\resources\\cloudstorage.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            ServerMain.logger.log(Level.WARNING, "Ошибка при загрузке класса JDBC " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e){
            ServerMain.logger.log(Level.WARNING, "Ошибка при попытке подключиться к бд " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop(){
        try{
            ServerMain.logger.log(Level.INFO, "Остановка сервиса авторизации");
            statement.close();
            connection.close();
        } catch (SQLException e){
            ServerMain.logger.log(Level.WARNING, "Ошибка при закрытии соединения с базой данных " + e.getMessage());
            e.printStackTrace();
        }
    }

    public String getNick(String login, String password){
        try {
            String sql = "SELECT nickname FROM user WHERE login = '" + login + "' AND password = '" + password + "'";
            ServerMain.logger.log(Level.INFO, "Запрс в бд: " + sql);
            ResultSet result = statement.executeQuery(sql);
            if (result.next()){
                ServerMain.logger.log(Level.INFO, "Получен никнейм: " + result.getString(1));
                return result.getString(1);
            }
        } catch (SQLException e){
            ServerMain.logger.log(Level.WARNING, "Ошибка при выполнении запроса к бд " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

}

