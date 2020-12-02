package command;

import client.FilesListManager;


public interface Command {
    void execute() throws Exception;

    default FilesListManager getFilesList() {
        return FilesListManager.getFilesListManager();
    }
}
