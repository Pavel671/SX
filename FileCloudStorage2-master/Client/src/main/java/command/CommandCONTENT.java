package command;

import common.FileProperties;
import client.FilesListManager;
import common.ConsoleHelper;



public class CommandCONTENT implements Command {
    public void execute() {
        ConsoleHelper.writeMessage("Просмотр списока файлов, для отправки на сервер.");

        FilesListManager filesListManager = getFilesList();

        for (FileProperties file : filesListManager.getFilesList()) {
            ConsoleHelper.writeMessage(file.toString());
        }

    }
}
