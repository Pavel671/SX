package common;

import java.io.*;
import java.util.ArrayList;


public class Message implements Serializable {

    private final MessageType type;
    private final String text;
    private final File file;
    private byte[] bytes;
    private ArrayList<FileProperties> fileList;

    public Message(MessageType type) {
        this.type = type;
        text = null;
        file = null;
    }

    public Message(MessageType type, String text) {
        this.type = type;
        this.text = text;
        file = null;
    }

    public Message(MessageType type, File file) throws IOException {
        this.type = type;
        this.text = null;
        this.file = file;
        try (FileInputStream fileInputStream = new FileInputStream(file)){
            bytes = new byte[(int)file.length()];
            fileInputStream.read(bytes);
        }
    }

    public Message(MessageType type, File file, String text, byte[] bytes) throws IOException {
        this.type = type;
        this.file = file;
        this.text = text;
        this.bytes = bytes;
    }

    public Message(MessageType type, ArrayList<FileProperties> fileList){
        this.type = type;
        this.fileList = fileList;
        this.text = null;
        this.file = null;
    }

    public MessageType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public ArrayList<FileProperties> getFileList() {
        return fileList;
    }
}