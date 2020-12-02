package command;

import client.*;
import common.ConsoleHelper;
import common.FileProperties;


import static client.DBManager.deleteAllFromTable;
import static client.DBManager.insertIntoTable;


public class CommandEXIT implements Command {

    public void execute() throws Exception {
        FilesListManager filesListManager = getFilesList();
        deleteAllFromTable(); //перед каждым сохранением удаляем все из БД
        if (filesListManager.size() > 0){
            for (FileProperties file : filesListManager.getFilesList()) {
                insertIntoTable(
                        file.getName(),
                        file.getSize(),
                        file.getAbsolutePath(),
                        file.getTimeWhenAdd().getTime()
                );
            }
            ConsoleHelper.writeMessage("Список файлов сохранен.");
        }
        ConsoleHelper.writeMessage("До встречи!");
    }
}