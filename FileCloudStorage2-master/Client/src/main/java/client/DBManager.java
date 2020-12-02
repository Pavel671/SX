package client;

import common.ConsoleHelper;
import common.FileProperties;
import common.exception.PathIsNotFoundException;

import java.sql.*;
import java.util.Date;

import static client.FilesListManager.getFilesListManager;


public class DBManager {
    private static Connection connection;
    private static Statement statement;
    private static final String tableName = "fileslist";


    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:Client/src/main/java/database/files.db");
        statement = connection.createStatement();
    }


    public static void disconnect(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void insertIntoTable(String name, long size, String absolutePath, long timeWhenAdd) throws SQLException {
        try {
            connect();
            String sql = String.format("INSERT INTO %s (name, size, absolutePath, timeWhenAdd) " +
                    "VALUES ('%s', '%d', '%s', '%d');", tableName, name, size, absolutePath, timeWhenAdd);
            statement.execute(sql);
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при сохранении данных");
        } finally {
            disconnect();
        }
    }


    public static void returnFilesListFromDB() throws SQLException, PathIsNotFoundException {
        FilesListManager filesListManager = getFilesListManager();
        try {
            connect();
            String sql = String.format("SELECT * FROM %s;", tableName);
            ResultSet rs = statement.executeQuery(sql);
            while (rs.next()){
                filesListManager.addFileFromDB(new FileProperties(
                        rs.getString(2),
                        rs.getLong(3),
                        rs.getString(4),
                        new Date(rs.getLong(5))
                ));
            }
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при загрузке данных");
        } finally {
            disconnect();
        }
    }


    public static void deleteAllFromTable() throws SQLException {
        try {
            connect();
            String sql = String.format("DELETE FROM %s ", tableName);
            statement.execute(sql);
        } catch (ClassNotFoundException e) {
            ConsoleHelper.writeMessage("Ошибка при работе с БД");
        } finally {
            disconnect();
        }
    }

}
