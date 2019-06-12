package com.project.exercise.bean;

import java.io.File;

public class FileMsg {
    private File file;
    private String fromName;
    private String toName;
    private String fromGroupChat;
    public FileMsg(File file,String fromName,String toName,String fromGroupChat){
        this.file=file;
        this.fromName=fromName;
        this.toName=toName;
        this.fromGroupChat=fromGroupChat;
    }

    public File getFile() {
        return file;
    }

    public String getFromName() {
        return fromName;
    }

    public String getToName() {
        return toName;
    }

    public String getFromGroupChat() {
        return fromGroupChat;
    }
}
