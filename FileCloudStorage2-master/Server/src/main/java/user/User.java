package user;

import common.ConsoleHelper;
import common.FileProperties;
import common.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class User {
    private final int name;
    private final int password;
    private final long registration_date;
    private final long time_last_visit;
    private final String folderName;

    public User(int name, int password, long registration_date, long time_last_visit) {
        this.name = name;
        this.password = password;
        this.registration_date = registration_date;
        this.time_last_visit = time_last_visit;
        this.folderName = "Server/server_storage/" + this.name + "/";
    }

    public int getName() {
        return name;
    }

    public String getFolderName() {
        return folderName;
    }

    public ArrayList<FileProperties> getFileList() {
        ArrayList<FileProperties> fileList = new ArrayList<>();
        File folder = new File(folderName);
        if (folder.exists()){
            for (File file : Objects.requireNonNull(folder.listFiles()))
            {
                fileList.add(
                        new FileProperties(
                                file.getName(),
                                file.length(),
                                file.getAbsolutePath(),
                                new Date(file.lastModified())
                        )
                );
                ConsoleHelper.writeMessage(file.getName());
            }
            return fileList;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return (
                "Client name: " + this.name +
                ", registration date: " + Constants.simpleDateFormat.format(this.registration_date) +
                ", time last visit: " + Constants.simpleDateFormat.format(this.time_last_visit)
        );
    }
}
