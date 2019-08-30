package snowflake.components.files;

import snowflake.common.FileInfo;
import snowflake.components.newsession.SessionInfo;

import java.io.Serializable;
import java.util.Arrays;

public class DndTransferData implements Serializable {
    private int sessionHashcode;
    private FileInfo[] files;
    private String currentDirectory;
    private int source;

    @Override
    public String toString() {
        return "DndTransferData{" +
                "sessionHashcode=" + sessionHashcode +
                ", files=" + Arrays.toString(files) +
                ", currentDirectory='" + currentDirectory + '\'' +
                '}';
    }

    public DndTransferData(int sessionHashcode, FileInfo[] files, String currentDirectory, int source) {
        this.sessionHashcode = sessionHashcode;
        this.files = files;
        this.currentDirectory = currentDirectory;
        this.source = source;
    }

    public int getInfo() {
        return sessionHashcode;
    }

    public void setInfo(int info) {
        this.sessionHashcode = info;
    }

    public FileInfo[] getFiles() {
        return files;
    }

    public void setFiles(FileInfo[] files) {
        this.files = files;
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }
}