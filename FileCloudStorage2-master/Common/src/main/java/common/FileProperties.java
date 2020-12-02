package common;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;


public class FileProperties implements Serializable {
    private final String name;
    private long size;
    private final String absolutePath;
    private final Date timeWhenAdd;
    private boolean fileExist;

    public FileProperties(String name, long size, String absolutePath, Date timeWhenAdd) {
        this.name = name;
        this.size = size;
        this.absolutePath = absolutePath;
        this.timeWhenAdd = timeWhenAdd;
        fileExist = !Files.notExists(Paths.get(absolutePath));
    }

    public FileProperties(String sourcePath) throws IOException {
        this.name = Paths.get(sourcePath).getFileName().toString();
        this.size = Files.size(Paths.get(sourcePath));
        this.absolutePath = sourcePath;
        this.timeWhenAdd = new Date();
        this.fileExist = true;
    }

    public void refresh() throws IOException {
        if(Files.notExists(Paths.get(absolutePath))){
            fileExist = false;
        } else {
            fileExist = true;
            this.size = Files.size(Paths.get(absolutePath));
        }
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public Date getTimeWhenAdd() {
        return timeWhenAdd;
    }

    public boolean isFileExist() {
        return fileExist;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(changeStringLength(name, 16));
        builder.append(changeStringLength(((size / 1024) + " "), 8));
        builder.append(" Kb, ");
        builder.append("absolute path: ");
        builder.append(changeStringLength(absolutePath, 32));
        builder.append(", when added: ");
        String date = Constants.simpleDateFormat.format(timeWhenAdd);
        builder.append(changeStringLength(date, 20));
        builder.append(" file exist: ");
        builder.append(isFileExist());
        return builder.toString();
    }

    private String changeStringLength(String text, int lengthToChange){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = text.length(); i < lengthToChange; i++) {
            stringBuilder.append(" ");
        }
        stringBuilder.append(text);
        return stringBuilder.toString();
    }
}
