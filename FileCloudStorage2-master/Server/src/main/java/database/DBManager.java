package database;

import common.ConsoleHelper;
import exeption.NoSuchUserException;
import user.User;

import java.sql.*;


public class DBManager {
    private static Connection connection;
    private static Statement statement;
    private static final String tableName = "users";


    private static synchronized void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:Server/src/main/java/database/" + tableName + ".db");
        statement = connection.createStatement();
    }


    private static synchronized void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static synchronized void insertIntoTable(int name, int password, long timeWhenAdd, long timeLastVisit) throws SQLException {
        try {
            connect();
            String sql = String.format("INSERT INTO %s (name, password, registration_date, time_last_visit) " +
                    "VALUES ('%d', '%d', '%d', '%d');",
                    tableName,
                    name,
                    password,
                    timeWhenAdd,
                    timeLastVisit
            );
            statement.execute(sql);
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при добавлении данных");
        } finally {
            disconnect();
        }
    }


    public static synchronized void updateUserTimeLastVisit(int name) throws Exception {
        try {
            connect();
            String sql = String.format("UPDATE %s SET time_last_visit = %d WHERE name = %d;",
                    tableName,
                    System.currentTimeMillis(),
                    name
            );
            int count = statement.executeUpdate(sql);

            if (count > 0){
                ConsoleHelper.writeMessage("Данные обновлены");
            } else {
                ConsoleHelper.writeMessage("К сожалению, обновления не произошло!");
            }
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при обновлении данных");
        } finally {
            disconnect();
        }
    }


    public static synchronized User returnUserFromDBbyNameAndPass(int name, int password) throws SQLException, NoSuchUserException {
        User user = null;
        try {
            connect();
            String sql = String.format("SELECT * FROM %s WHERE name = %d AND password = %d", tableName, name, password);
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                user = new User(
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getLong(4),
                        rs.getLong(5)
                );
            } else {
                throw new NoSuchUserException();
            }
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при загрузке данных");
        } finally {
            disconnect();
        }
        return user;
    }

}
