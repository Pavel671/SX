package command;

import client.ConnectionManager;
import common.ConsoleHelper;
import common.Message;
import common.MessageType;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CommandDOWNLOAD implements Command {

    @Override
    public void execute() throws Exception {
        ConsoleHelper.writeMessage("Загрузка файла с сервера.");

        ConsoleHelper.writeMessage("Введите имя файла для загрузки:");
        downloadFile(ConsoleHelper.readString());
    }


    public static void downloadFile(String fileName) throws Exception {
        ConnectionManager.getConnectionManager(null).send(new Message(MessageType.DOWNLOAD_FILE, fileName));

        Message message = ConnectionManager.getConnectionManager(null).receive();
        FileOutputStream fileOutputStream = null;

        if (message.getType() == MessageType.DOWNLOAD_FILE_OK){
            String absolutePathName = "Client/client_storage/";
            Path path = Paths.get(absolutePathName);
            if (!Files.exists(path)) Files.createDirectories(path);
            fileOutputStream = new FileOutputStream(absolutePathName + message.getFile().getName());
            fileOutputStream.write(message.getBytes());
            fileOutputStream.close();
            ConsoleHelper.writeMessage(String.format("Файл %s успешно загружен.", fileName));
        }

        if (message.getType() == MessageType.DOWNLOAD_BIG_FILE_START){
            String absolutePathName = "Client/client_storage/";
            Path path = Paths.get(absolutePathName);
            if (!Files.exists(path)) Files.createDirectories(path);
            Path pathToFile = Paths.get(absolutePathName + message.getText());
            if (Files.exists(pathToFile)) Files.delete(pathToFile);
            message = ConnectionManager.getConnectionManager(null).receive();
            while (message.getType() == MessageType.DOWNLOAD_BIG_FILE){
                if (fileOutputStream == null){
                    fileOutputStream = new FileOutputStream(absolutePathName + message.getFile().getName(), true);
                }
                fileOutputStream.write(message.getBytes());
                message = ConnectionManager.getConnectionManager(null).receive();
            }
            if (message.getType() == MessageType.DOWNLOAD_BIG_FILE_END){
                ConsoleHelper.writeMessage(String.format("Файл %s успешно загружен.", fileName));
            } else {
                ConsoleHelper.writeMessage(String.format("Ошибка! Файл %s загружен не корректно.", fileName));
            }
            assert fileOutputStream != null;
            fileOutputStream.close();
        }

        if (message.getType() == MessageType.DOWNLOAD_FILE){
            ConsoleHelper.writeMessage(message.getText());
        }
    }
}

