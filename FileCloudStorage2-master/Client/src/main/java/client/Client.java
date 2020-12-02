package client;

import common.ConsoleHelper;
import common.Message;
import common.MessageType;
import common.exception.InvalidInputFormatException;
import common.exception.PathIsNotFoundException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.sql.SQLException;

import static common.ConsoleHelper.*;


public class Client {

    private static int clientName;
    private static volatile boolean clientConnected = false;
    private static ConnectionManager connectionManager;


    static {
        try {
            connect();
            while (!clientConnected){
                writeMessage("\nЗарегистрируйтесь(1) или выполните вход(2).");
                try {
                    int i = readInt();
                    if (i == 1) registration();
                    else if (i == 2) authorization();
                    else throw new InvalidInputFormatException();
                } catch (InvalidInputFormatException e){
                    writeError("Пожалуйста, выберите из предложенного списка.");
                } catch (PathIsNotFoundException e) {
                    writeError("База данных повреждена, или отсутствует.");
                } catch (SQLException e) {
                    writeError("Не удалось подключиться к базе данных.");
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            writeError("Не удалось подключиться. Сервер не доступен.");
        } catch (InvalidInputFormatException e){
            writeError("Проверьте адрес сервера и адрес порта.");
        }

    }


    public static void main(String[] args) throws Exception {
        //main loop block
        if (clientConnected) {
            ClientOperation operation = null;
            do {
                try {
                    operation = askOperation();
                    CommandExecutor.execute(operation);
                } catch (InvalidInputFormatException | ArrayIndexOutOfBoundsException e) {
                    writeError("Пожалуйста, выберите из предложенного списка");
                }
            } while (operation != ClientOperation.EXIT);
        }
    }



    public static ClientOperation askOperation() throws IOException, InvalidInputFormatException, ArrayIndexOutOfBoundsException {
        writeMessage(              "");
        writeMessage(              "+----------------------------------------------------+");
        writeMessage(              "| Выберите операцию:                                 |");
        writeMessage(String.format("|\t %d - добавить файл в список файлов для отправки  |", ClientOperation.ADD.ordinal()));
        writeMessage(String.format("|\t %d - удалить файл из списка файлов для отправки  |", ClientOperation.REMOVE.ordinal()));
        writeMessage(String.format("|\t %d - просмотреть список файлов для отправки      |", ClientOperation.CONTENT.ordinal()));
        writeMessage(String.format("|\t %d - обновить список файлов для отправки         |", ClientOperation.REFRESH.ordinal()));
        writeMessage(String.format("|\t %d - отправить файл на сервер                    |", ClientOperation.UPLOAD.ordinal()));
        writeMessage(String.format("|\t %d - отправить все файлы из списка на сервер     |", ClientOperation.UPLOAD_ALL.ordinal()));
        writeMessage(String.format("|\t %d - загрузить файл с сервера                    |", ClientOperation.DOWNLOAD.ordinal()));
        writeMessage(String.format("|\t %d - загрузить все файлы с сервера               |", ClientOperation.DOWNLOAD_ALL.ordinal()));
        writeMessage(String.format("|\t %d - список файлов на сервере                    |", ClientOperation.FILE_LIST.ordinal()));
        writeMessage(String.format("|\t %d - удалить файл на сервере                     |", ClientOperation.DELETE_FILE_FROM_SERVER.ordinal()));
        writeMessage(String.format("|\t %d - выход                                      |", ClientOperation.EXIT.ordinal()));
        writeMessage(              "+----------------------------------------------------+");
        return ClientOperation.values()[readInt()];
    }



    private static void registration() throws IOException, ClassNotFoundException {
        int clientName = getUserName().hashCode();
        int clientPassword = getUserPassword().hashCode();
        connectionManager.send(new Message(MessageType.REGISTRATION, (clientName + " " + clientPassword)));
        Message message = connectionManager.receive();
        if (message.getType() == MessageType.REGISTRATION_OK) {
            clientConnected = true;
            Client.clientName = clientName;
            writeMessage(message.getText());
        }
        if (message.getType() == MessageType.REGISTRATION) {
            writeMessage(message.getText());
        }
    }


    private static void authorization() throws IOException, ClassNotFoundException, PathIsNotFoundException, SQLException {
        int clientName = getUserName().hashCode();
        int clientPassword = getUserPassword().hashCode();
        connectionManager.send(new Message(MessageType.AUTHORIZATION, (clientName + " " + clientPassword)));
        Message message = connectionManager.receive();
        if (message.getType() == MessageType.AUTHORIZATION_OK) {
            clientConnected = true;
            DBManager.returnFilesListFromDB();
            Client.clientName = clientName;
            writeMessage(message.getText());
        }
        if (message.getType() == MessageType.AUTHORIZATION) {
            writeMessage(message.getText());
        }
    }



    private static void connect() throws IOException, ConnectException, InvalidInputFormatException {
        String serverAddress = getServerAddress();
        int serverPort = getServerPort();
        connectionManager = ConnectionManager.getConnectionManager(new Socket(serverAddress, serverPort));
    }


    protected static String getServerAddress() throws IOException {
        writeMessage("Введите адрес сервера");
        String address = readString();
        writeMessage(address);
        return address;
    }


    protected static int getServerPort() throws IOException, InvalidInputFormatException {
        writeMessage("Введите адрес порта");
        int port = readInt();
        writeMessage(Integer.toString(port));
        return port;
    }

    protected static String getUserName() throws IOException {
        writeMessage("Введите имя пользователя");
        String userName = readString();
        writeMessage(userName);
        return userName;
    }

    protected static String getUserPassword() throws IOException {
        writeMessage("Введите пароль");
        String userPassword = readString();
        writeMessage(userPassword);
        return userPassword;
    }

}
