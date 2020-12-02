package client;

import common.ConsoleHelper;
import common.FileProperties;
import common.exception.FileAlreadyExistException;
import common.exception.PathIsNotFoundException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class FilesListManager {

    private static FilesListManager filesListManager;
    private final List<FileProperties> filesList;

    private FilesListManager() {
        this.filesList = new ArrayList<>();
    }

    public static synchronized FilesListManager getFilesListManager(){
        if (filesListManager == null) filesListManager = new FilesListManager();
        return filesListManager;
    }


    public void addFile(FileProperties file) throws PathIsNotFoundException, FileAlreadyExistException {
        if(!file.isFileExist()){
            throw new PathIsNotFoundException();
        }
        if (filesListContainsPath(Paths.get(file.getAbsolutePath()))){
            throw new FileAlreadyExistException();
        } else {
            this.filesList.add(file);
        }
    }


    public void addFileFromDB(FileProperties file) {
        this.filesList.add(file);
    }


    public void removeFile(Path sourcePath){
        for (int i = 0; i < filesList.size(); i++) {
            if (filesList.get(i).getAbsolutePath().equals(sourcePath.toString())){
                ConsoleHelper.writeMessage(String.format("Файл %s удален.", filesList.get(i).getAbsolutePath()));
                filesList.remove(i);
                return;
            }
        }
        ConsoleHelper.writeMessage(String.format("Файл %s не найден.", sourcePath));
    }


    public void refreshFilesList() throws IOException {
        for (FileProperties file : filesList) {
            file.refresh();
        }
    }

    public int size(){
        return filesList.size();
    }

    public List<FileProperties> getFilesList() {
        return filesList;
    }


    public boolean filesListContainsPath(Path sourcePath){
        for (FileProperties file : filesList) {
            if (file.getAbsolutePath().equals(sourcePath.toString())){
                return true;
            }
        }
        return false;
    }

}
